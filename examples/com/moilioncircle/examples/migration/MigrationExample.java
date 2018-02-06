/*
 * Copyright 2016-2018 Leon Chen
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

package com.moilioncircle.examples.migration;

import com.moilioncircle.examples.migration.cmd.DefaultCommand;
import com.moilioncircle.examples.migration.rdb.MigrationKeyValuePair;
import com.moilioncircle.redis.replicator.CloseListener;
import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisURI;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import redis.clients.jedis.Client;
import redis.clients.jedis.Protocol;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static redis.clients.jedis.Protocol.Command.AUTH;
import static redis.clients.jedis.Protocol.Command.RESTORE;

/**
 * @author Leon Chen
 * @since 2.4.3
 */
public class MigrationExample {

    public static void main(String[] args) throws IOException, URISyntaxException {
        sync("redis://127.0.0.1:6379", "redis://127.0.0.1:6380");
    }

    /*
     * Precondition:
     * 1. Make sure the two redis version is same.
     * 2. Make sure the single key-value is not very big.(highly recommend less then 1 MB)
     *
     * We running following steps to sync two redis.
     * 1. Get rdb stream from source redis.
     * 2. Convert source rdb stream to redis dump format.
     * 3. Use Jedis RESTORE command to restore that dump format to target redis.
     * 4. Get aof stream from source redis and sync to target redis.
     */
    @SuppressWarnings("resource")
    public static void sync(String sourceUri, String targetUri) throws IOException, URISyntaxException {
        RedisURI suri = new RedisURI(sourceUri);
        RedisURI turi = new RedisURI(targetUri);
        final ExampleClient target = new ExampleClient(turi.getHost(), turi.getPort());
        Configuration tconfig = Configuration.valueOf(turi);
        if (tconfig.getAuthPassword() != null) {
            String auth = target.send(AUTH, tconfig.getAuthPassword().getBytes());
            System.out.println("AUTH:" + auth);
        }
        final AtomicInteger dbnum = new AtomicInteger(-1);
        Replicator r = new RedisMigrationReplicator(suri.getHost(), suri.getPort(), Configuration.valueOf(suri));
        r.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (!(kv instanceof MigrationKeyValuePair)) return;
                // Step1: select db
                DB db = kv.getDb();
                int index;
                if (db != null && (index = (int) db.getDbNumber()) != dbnum.get()) {
                    target.select(index);
                    dbnum.set(index);
                }

                // Step2: restore dump data
                MigrationKeyValuePair mkv = (MigrationKeyValuePair) kv;
                if (mkv.getExpiredMs() == null) {
                    String r = target.restore(mkv.getRawKey(), 0L, mkv.getValue(), true);
                    System.out.println(r);
                } else {
                    long ms = mkv.getExpiredMs() - System.currentTimeMillis();
                    if (ms <= 0) return;
                    String r = target.restore(mkv.getRawKey(), ms, mkv.getValue(), true);
                    System.out.println(r);
                }
            }
        });
        r.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (!(command instanceof DefaultCommand)) return;
                // Step3: sync aof command
                DefaultCommand dc = (DefaultCommand) command;
                String r = target.send(dc.getCommand(), dc.getArgs());
                System.out.println(r);
            }
        });
        r.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                target.close();
            }
        });
        r.open();
    }

    /*
     * Jedis is not a reliable redis client.
     * For simplicity we use Jedis to show this example.
     * In production you need to replace following code to yours.
     * Don't forget to replace DefaultCommandParser too. :)
     */
    public static class ExampleClient extends Client {

        public ExampleClient(final String host, final int port) {
            super(host, port);
        }

        public String send(Protocol.Command cmd, final byte[]... args) {
            sendCommand(cmd, args);
            return getStatusCodeReply();
        }

        public String send(final byte[] cmd, final byte[]... args) {
            return send(Protocol.Command.valueOf(new String(cmd, UTF_8).toUpperCase()), args);
        }

        public String restore(byte[] key, long expired, byte[] dumped, boolean replace) {
            if (!replace) {
                return send(RESTORE, key, String.valueOf(expired).getBytes(), dumped);
            } else {
                return send(RESTORE, key, String.valueOf(expired).getBytes(), dumped, "REPLACE".getBytes());
            }
        }
    }
}
