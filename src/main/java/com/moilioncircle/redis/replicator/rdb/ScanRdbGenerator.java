/*
 * Copyright 2016-2017 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.rdb;

import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_AUX;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_EOF;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_EXPIRETIME_MS;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_RESIZEDB;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_SELECTDB;
import static com.moilioncircle.redis.replicator.util.Strings.lappend;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RESP2;
import com.moilioncircle.redis.replicator.io.CRCOutputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;
import com.moilioncircle.redis.replicator.util.ByteArray;
import com.moilioncircle.redis.replicator.util.Strings;
import com.moilioncircle.redis.replicator.util.type.Tuple2;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
public class ScanRdbGenerator {
    
    protected final int port;
    protected final String host;
    
    private CRCOutputStream out;
    private RESP2.Client client;
    private Configuration configuration;
    
    private static Map<String, Integer> VERSIONS = new HashMap<>();
    
    static {
        VERSIONS.put("2.6", 6);
        VERSIONS.put("2.8", 6);
        VERSIONS.put("3.0", 6);
        VERSIONS.put("3.2", 7);
        VERSIONS.put("4.0", 8);
        VERSIONS.put("5.0", 9);
        VERSIONS.put("6.0", 9);
        VERSIONS.put("6.2", 9);
        VERSIONS.put("7.0", 10);
    }
    
    public ScanRdbGenerator(String host, int port, Configuration configuration, OutputStream out) {
        this.host = host;
        this.port = port;
        this.configuration = configuration;
        this.out = new CRCOutputStream(out);
    }
    
    public void generate() throws IOException {
        try {
            this.client = new RESP2.Client(host, port, configuration);
            /*
             * rdb version
             */
            int version = 0;
            String ver = null;
            
            RESP2.Node server = retry(client -> {
                RESP2.Response r = client.newCommand();
                return r.invoke("info", "server");
            });
            
            if (server.type == RESP2.Type.ERROR) {
                throw new IOException(Strings.toString(server.value));
            } else {
                String value = Strings.toString(server.value);
                String[] line = value.split("\n");
                ver = line[1].split(":")[1];
                ver = ver.substring(0, ver.lastIndexOf('.'));
                if (!VERSIONS.containsKey(ver)) {
                    throw new UnsupportedOperationException("unsupported redis version :" + ver);
                }
                
                version = VERSIONS.get(ver);
                
                out.write("REDIS".getBytes());
                out.write(lappend(version, 4, '0').getBytes());
            }
            
            /*
             * aux
             */
            if (version >= 7) {
                generateAux("redis-ver", ver);
                generateAux("ctime", String.valueOf(System.currentTimeMillis() / 1000L));
            }
            
            if (version >= 10) {
                /*
                 * rdb function
                 */
                RESP2.Node functions = retry(client -> {
                    RESP2.Response r = client.newCommand();
                    return r.invoke("function", "dump");
                });
                
                if (functions.type == RESP2.Type.ERROR) {
                    throw new IOException(Strings.toString(functions.value));
                } else {
                    byte[] funcs = (byte[]) functions.value;
                    out.write(funcs, 0, funcs.length - 10);
                }
            }
            
            /*
             * rdb db info
             */
            RESP2.Node keyspace = retry(client -> {
                RESP2.Response r = client.newCommand();
                return r.invoke("info", "keyspace");
            });
            
            String[] line = Strings.toString(keyspace.value).split("\n");
            for (int i = 1; i < line.length; i++) {
                // db0:keys={dbsize},expires={expires},avg_ttl=0
                String[] ary = line[i].split(":");
                Integer dbnum = Integer.parseInt(ary[0].substring(2));
                ary = ary[1].split(",");
                long dbsize = Long.parseLong(ary[0].split("=")[1]);
                long expires = Long.parseLong(ary[1].split("=")[1]);
                DB db = new DB(dbnum, dbsize, expires);
                generateDB(db, version);
            }
            
            out.write(RDB_OPCODE_EOF);
            out.write(out.getCRC64());
        } finally {
            close();
        }
    }
    
    private void generateAux(String key, String val) throws IOException {
        BaseRdbEncoder encoder = new BaseRdbEncoder();
        out.write(RDB_OPCODE_AUX);
        encoder.rdbGenericSaveStringObject(new ByteArray(key.getBytes()), out);
        encoder.rdbGenericSaveStringObject(new ByteArray(val.getBytes()), out);
    }
    
    private void generateDB(DB db, int version) throws IOException {
        BaseRdbEncoder encoder = new BaseRdbEncoder();
        RESP2.Node select = retry(client -> {
            RESP2.Response r = client.newCommand();
            return r.invoke("select", String.valueOf(db.getDbNumber()));
        });
        
        if (select.type == RESP2.Type.ERROR) {
            throw new IOException(Strings.toString(select.value));
        }
        
        /*
         * db
         */
        out.write(RDB_OPCODE_SELECTDB);
        encoder.rdbSaveLen(db.getDbNumber(), out);
        if (version >= 7) {
            out.write(RDB_OPCODE_RESIZEDB);
            encoder.rdbSaveLen(db.getDbsize(), out);
            encoder.rdbSaveLen(db.getExpires(), out);
        }
        
        /*
         * scan
         */
        String cursor = "0";
        String count = String.valueOf(configuration.getScanCount());
        do {
            String temp = cursor;
            RESP2.Node scan = retry(client -> {
                RESP2.Response r = client.newCommand();
                return r.invoke("scan", temp, "count", count);
            });
            if (scan.type == RESP2.Type.ERROR) {
                throw new IOException(Strings.toString(scan.value));
            }
            
            RESP2.Node[] ary = (RESP2.Node[]) scan.value;
            cursor = Strings.toString(ary[0].value);
            
            // pipeline
            RESP2.Response response = retry(client -> {
                RESP2.Response r = client.newCommand();
                RESP2.Node[] nodes = (RESP2.Node[]) ary[1].value;
                for (int i = 0; i < nodes.length; i++) {
                    byte[] key = (byte[]) nodes[i].value;
                    if (version >= 10) {
                        r.post(new ExpireNodeConsumer(out), "pexpiretime".getBytes(), key);
                    } else {
                        r.post(new TTLNodeConsumer(out), "pttl".getBytes(), key);
                    }
                    r.post(new DumpNodeConsumer(key, out), "dump".getBytes(), key);
                }
                return r;
            });
            retry(response);
        } while (!cursor.equals("0"));
    }
    
    private static class TTLNodeConsumer implements RESP2.NodeConsumer {
        
        private OutputStream out;
        private BaseRdbEncoder encoder = new BaseRdbEncoder();
        
        public TTLNodeConsumer(OutputStream out) {
            this.out = out;
        }
        
        @Override
        public void accept(RESP2.Node node) throws IOException {
            if (node.type == RESP2.Type.ERROR) {
                throw new IOException(Strings.toString(node.value));
            }
            Long ttl = (Long) node.value;
            if (ttl >= 0) {
                ttl = System.currentTimeMillis() + ttl;
                out.write(RDB_OPCODE_EXPIRETIME_MS);
                encoder.rdbSaveMillisecondTime(ttl, out);
            }
        }
    }
    
    private static class ExpireNodeConsumer implements RESP2.NodeConsumer {
        
        private OutputStream out;
        private BaseRdbEncoder encoder = new BaseRdbEncoder();
        
        public ExpireNodeConsumer(OutputStream out) {
            this.out = out;
        }
        
        @Override
        public void accept(RESP2.Node node) throws IOException {
            if (node.type == RESP2.Type.ERROR) {
                throw new IOException(Strings.toString(node.value));
            }
            Long ttl = (Long) node.value;
            if (ttl >= 0) {
                out.write(RDB_OPCODE_EXPIRETIME_MS);
                encoder.rdbSaveMillisecondTime(ttl, out);
            }
        }
    }
    
    private static class DumpNodeConsumer implements RESP2.NodeConsumer {
        
        private byte[] key;
        private OutputStream out;
        private BaseRdbEncoder encoder = new BaseRdbEncoder();
        
        public DumpNodeConsumer(byte[] key, OutputStream out) {
            this.key = key;
            this.out = out;
        }
        
        @Override
        public void accept(RESP2.Node node) throws IOException {
            if (node.type == RESP2.Type.ERROR) {
                throw new IOException(Strings.toString(node.value));
            }
            
            byte[] value = (byte[]) node.value;
            byte type = value[0];
            out.write(type);
            encoder.rdbGenericSaveStringObject(new ByteArray(key), out);
            out.write(value, 1, value.length - 11);
        }
    }
    
    private <T> T retry(RESP2.Function<RESP2.Client, T> function) throws IOException {
        IOException exception = null;
        for (int i = 0; i < configuration.getRetries() || configuration.getRetries() <= 0; i++) {
            try {
                return function.apply(client);
            } catch (EOFException e) {
                throw e;
            } catch (IOException e) {
                exception = e;
                try {
                    this.client = RESP2.Client.valueOf(this.client);
                } catch (IOException ex) {
                }
            }
        }
        throw exception;
    }
    
    private void retry(RESP2.Response prev) throws IOException {
        IOException exception = null;
        for (int i = 0; i < configuration.getRetries() || configuration.getRetries() <= 0; i++) {
            try {
                prev.get();
                return;
            } catch (EOFException e) {
                throw e;
            } catch (IOException e) {
                exception = e;
                try {
                    Queue<Tuple2<RESP2.NodeConsumer, byte[][]>> responses = prev.responses();
                    RESP2.Response next = retry(client -> {
                        RESP2.Response r = client.newCommand();
                        while (!responses.isEmpty()) {
                            Tuple2<RESP2.NodeConsumer, byte[][]> tuple2 = responses.poll();
                            r.post(tuple2.getV1(), tuple2.getV2());
                        }
                        return r;
                    });
                    prev = next;
                } catch (IOException ex) {
                }
            }
        }
        throw exception;
    }
    
    private void close() {
        if (out != null) {
            try {
                out.flush();
            } catch (IOException e) {
            }
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
            }
        }
    }
}
