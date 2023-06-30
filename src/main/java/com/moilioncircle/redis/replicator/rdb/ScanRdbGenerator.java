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

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.client.RESP2;
import com.moilioncircle.redis.replicator.client.RESP2Client;
import com.moilioncircle.redis.replicator.io.CRCOutputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;
import com.moilioncircle.redis.replicator.util.ByteArray;
import com.moilioncircle.redis.replicator.util.type.Tuple2;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
public class ScanRdbGenerator {
    
    private int db = 0;
    private RESP2Client client;
    
    protected final int port;
    protected final String host;
    private final CRCOutputStream out;
    private final Configuration configuration;
    
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
        VERSIONS.put("7.2", 11);
    }
    
    public ScanRdbGenerator(String host, int port, Configuration configuration, OutputStream out) {
        this.host = host;
        this.port = port;
        this.configuration = configuration;
        this.out = new CRCOutputStream(new BufferedOutputStream(out, this.configuration.getBufferSize()));
    }
    
    public void generate() throws IOException {
        try {
            this.client = new RESP2Client(host, port, configuration);
            /*
             * rdb version
             */
            int version = 0;
            String ver = null;
            String bits = null;
            
            RESP2.Node server = retry(client -> {
                RESP2Client.Command r = client.newCommand();
                return r.invoke("info", "server");
            });
            
            if (server.type == RESP2.Type.ERROR) {
                throw new IOException(server.getError());
            } else {
                String value = server.getString();
                String[] lines = value.split("\r\n");
                for (int i = 1; i < lines.length; i++) {
                    String[] kv = lines[i].split(":");
                    String key = kv[0];
                    
                    if (key.equals("redis_version")) {
                        String val = kv[1];
                        ver = val;
                        
                        val = val.substring(0, val.lastIndexOf('.'));
                        if (!VERSIONS.containsKey(val)) {
                            throw new AssertionError("unsupported redis version :" + val);
                        }
                        
                        version = VERSIONS.get(val);
                    } else if (key.equals("arch_bits")) {
                        String val = kv[1];
                        bits = val;
                    }
                }
            }
            
            /*
             * version
             */
            out.write("REDIS".getBytes());
            out.write(lappend(version, 4, '0').getBytes());
            
            /*
             * aux
             */
            if (version >= 7) {
                generateAux("redis-ver", ver);
                generateAux("redis-bits", bits);
                generateAux("ctime", String.valueOf(System.currentTimeMillis() / 1000L));
                
                // used-memory
                RESP2.Node memory = retry(client -> {
                    RESP2Client.Command r = client.newCommand();
                    return r.invoke("info", "memory");
                });
                
                if (memory.type == RESP2.Type.STRING) {
                    String value = memory.getString();
                    String[] lines = value.split("\r\n");
                    for (int i = 1; i < lines.length; i++) {
                        String[] kv = lines[i].split(":");
                        String key = kv[0];
                        
                        if (key.equals("used_memory")) {
                            String val = kv[1];
                            generateAux("used-mem", val);
                        }
                    }
                }
            }
            
            if (version >= 10) {
                /*
                 * rdb function
                 */
                RESP2.Node functions = retry(client -> {
                    RESP2Client.Command r = client.newCommand();
                    return r.invoke("function", "dump");
                });
                
                if (functions.type == RESP2.Type.ERROR) {
                    throw new IOException(functions.getError());
                } else {
                    ByteArray funcs = functions.getBytes();
                    if (funcs != null) {
                        funcs.writeTo(out, 0, funcs.length() - 10);
                    }
                }
            }
            
            /*
             * rdb db info
             */
            RESP2.Node keyspace = retry(client -> {
                RESP2Client.Command r = client.newCommand();
                return r.invoke("info", "keyspace");
            });
            
            String[] line = keyspace.getString().split("\r\n");
            for (int i = 1; i < line.length; i++) {
                // db{dbnum}:keys={dbsize},expires={expires},avg_ttl=0
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
            out.flush();
        } finally {
            close();
        }
    }
    
    private void generateAux(String key, String val) throws IOException {
        if (val == null) return;
        BaseRdbEncoder encoder = new BaseRdbEncoder();
        out.write(RDB_OPCODE_AUX);
        encoder.rdbGenericSaveStringObject(new ByteArray(key.getBytes()), out);
        encoder.rdbGenericSaveStringObject(new ByteArray(val.getBytes()), out);
    }
    
    private void generateDB(DB db, int version) throws IOException {
        BaseRdbEncoder encoder = new BaseRdbEncoder();
        RESP2.Node select = retry(client -> {
            RESP2Client.Command r = client.newCommand();
            return r.invoke("select", String.valueOf(db.getDbNumber()));
        });
        
        /*
         * select
         */
        if (select.type == RESP2.Type.ERROR) {
            throw new IOException(select.getError());
        } else {
            this.db = (int) db.getDbNumber();
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
        String step = String.valueOf(configuration.getScanStep());
        do {
            String temp = cursor;
            RESP2.Node scan = retry(client -> {
                RESP2Client.Command r = client.newCommand();
                return r.invoke("scan", temp, "count", step);
            });
            if (scan.type == RESP2.Type.ERROR) {
                throw new IOException(scan.getError());
            }
            
            RESP2.Node[] ary = scan.getArray();
            cursor = ary[0].getString();
            
            // key value pipeline
            RESP2Client.Command command = retry(client -> {
                RESP2Client.Command r = client.newCommand();
                RESP2.Node[] nodes = ary[1].getArray();
                for (int i = 0; i < nodes.length; i++) {
                    byte[] key = nodes[i].getBytes().first();
                    if (version >= 10) {
                        PExpireTimeNodeConsumer context = new PExpireTimeNodeConsumer();
                        r.post(context, "pexpiretime".getBytes(), key);
                        r.post(new DumpNodeConsumer(key, out, context), "dump".getBytes(), key);
                    } else {
                        PTTLNodeConsumer context = new PTTLNodeConsumer();
                        r.post(context, "pttl".getBytes(), key);
                        r.post(new DumpNodeConsumer(key, out, context), "dump".getBytes(), key);
                    }
                }
                return r;
            });
            retry(command);
        } while (!cursor.equals("0"));
    }
    
    private static interface TTLContext {
        Long getTTL();
    }
    
    private static class PTTLNodeConsumer implements RESP2Client.NodeConsumer, TTLContext {
        
        private Long ttl;
        
        @Override
        public Long getTTL() {
            return this.ttl;
        }
        
        @Override
        public void accept(RESP2.Node node) throws IOException {
            if (node.type == RESP2.Type.ERROR) {
                throw new IOException(node.getError());
            }
            Long ttl = node.getNumber();
            if (ttl >= 0) {
                this.ttl = System.currentTimeMillis() + ttl;
            }
        }
    }
    
    private static class PExpireTimeNodeConsumer implements RESP2Client.NodeConsumer, TTLContext {
        
        private Long ttl;
        
        @Override
        public Long getTTL() {
            return this.ttl;
        }
        
        @Override
        public void accept(RESP2.Node node) throws IOException {
            if (node.type == RESP2.Type.ERROR) {
                throw new IOException(node.getError());
            }
            Long ttl = node.getNumber();
            if (ttl >= 0) {
                this.ttl = ttl;
            }
        }
    }
    
    private static class DumpNodeConsumer implements RESP2Client.NodeConsumer {
        
        private byte[] key;
        private OutputStream out;
        private TTLContext context;
        private BaseRdbEncoder encoder = new BaseRdbEncoder();
        
        public DumpNodeConsumer(byte[] key, OutputStream out, TTLContext context) {
            this.key = key;
            this.out = out;
            this.context = context;
        }
        
        @Override
        public void accept(RESP2.Node node) throws IOException {
            if (node.type == RESP2.Type.ERROR) {
                throw new IOException(node.getError());
            }
            
            if (node.value != null) {
                Long ttl = context.getTTL();
                if (ttl != null) {
                    out.write(RDB_OPCODE_EXPIRETIME_MS);
                    encoder.rdbSaveMillisecondTime(ttl, out);
                }
                ByteArray value = node.getBytes();
                byte type = value.get(0);
                out.write(type);
                encoder.rdbGenericSaveStringObject(new ByteArray(key), out);
                value.writeTo(out, 1, value.length() - 11);
            }
        }
    }
    
    private RESP2Client recreate(RESP2Client prev, int db, IOException reason) throws IOException {
        IOException exception = reason;
        for (int i = 0; i < configuration.getRetries() || configuration.getRetries() <= 0; i++) {
            try {
                return RESP2Client.valueOf(prev, db, exception, i + 1);
            } catch (IOException e) {
                exception = e;
            }
        }
        throw exception;
    }
    
    private <T> T retry(RESP2Client.Function<RESP2Client, T> function) throws IOException {
        IOException exception = null;
        for (int i = 0; i < configuration.getRetries() || configuration.getRetries() <= 0; i++) {
            try {
                return function.apply(client);
            } catch (EOFException e) {
                throw e;
            } catch (IOException e) {
                exception = e;
                this.client = recreate(this.client, this.db, e);
            }
        }
        throw exception;
    }
    
    private void retry(RESP2Client.Command prev) throws IOException {
        IOException exception = null;
        for (int i = 0; i < configuration.getRetries() || configuration.getRetries() <= 0; i++) {
            try {
                prev.get();
                return;
            } catch (EOFException e) {
                throw e;
            } catch (IOException e) {
                exception = e;
                this.client = recreate(this.client, this.db, e);
                Queue<Tuple2<RESP2Client.NodeConsumer, byte[][]>> commands = prev.getCommands();
                RESP2Client.Command next = retry(client -> {
                    RESP2Client.Command r = client.newCommand();
                    while (!commands.isEmpty()) {
                        Tuple2<RESP2Client.NodeConsumer, byte[][]> tuple = commands.poll();
                        r.post(tuple.getV1(), tuple.getV2());
                    }
                    return r;
                });
                prev = next;
            }
        }
        throw exception;
    }
    
    private void close() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
            }
        }
    }
}
