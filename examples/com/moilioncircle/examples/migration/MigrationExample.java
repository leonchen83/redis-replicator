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
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import redis.clients.jedis.Client;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Protocol;

import java.io.IOException;

/**
 * @author Leon Chen
 * @since 2.4.3
 */
public class MigrationExample {

    public static void main(String[] args) throws IOException {
        sync("127.0.0.1", 6379, "127.0.0.1", 6380);
    }

    @SuppressWarnings("resource")
    public static void sync(String sourceHost, int sourcePort, String targetHost, int targetPort) throws IOException {
        final ExampleClient target = new ExampleClient(targetHost, targetPort);
        Replicator r = new RedisMigrationReplicator(sourceHost, sourcePort, Configuration.defaultSetting());
        r.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (!(kv instanceof MigrationKeyValuePair)) return;
                MigrationKeyValuePair mkv = (MigrationKeyValuePair) kv;
                if (mkv.getExpiredMs() == null) {
                    String r = target.send(Protocol.Command.RESTORE, mkv.getRawKey(), "0".getBytes(), mkv.getValue(), "REPLACE".getBytes());
                    System.out.println(r);
                } else {
                    long ms = mkv.getExpiredMs() - System.currentTimeMillis();
                    if (ms <= 0) return;
                    String r = target.send(Protocol.Command.RESTORE, mkv.getRawKey(), String.valueOf(ms).getBytes(), mkv.getValue(), "REPLACE".getBytes());
                    System.out.println(r);
                }
            }
        });
        r.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (!(command instanceof DefaultCommand)) return;
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

    public static class ExampleClient extends Client {

        public ExampleClient(final String host, final int port) {
            super(host, port);
        }

        public String send(final Protocol.Command cmd, final byte[]... args) {
            sendCommand(cmd, args);
            return getStatusCodeReply();
        }

        public Connection sendCommand(final Protocol.Command cmd, final byte[]... args) {
            return super.sendCommand(cmd, args);
        }
    }
}
