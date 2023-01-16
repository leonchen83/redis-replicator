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

package com.moilioncircle.redis.replicator;

import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_EOF;

import java.io.IOException;

import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.io.CRCOutputStream;
import com.moilioncircle.redis.replicator.io.XPipedInputStream;
import com.moilioncircle.redis.replicator.io.XPipedOutputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.Function;

/**
 * @author Baoyi Chen
 */
public class Main {
    public static void main(String[] args) throws Exception {
        XPipedOutputStream out = new XPipedOutputStream();
        CRCOutputStream crc = new CRCOutputStream(out);
        Thread thread = new Thread(() -> {
            try(RESP2.Client client = new RESP2.Client("127.0.0.1", 6379, Configuration.defaultSetting())) {
                RESP2.Response response = client.newCommand();
                response.post(node -> {
                    try {
                        crc.write("REDIS0010".getBytes());
                    } catch (IOException e) {
                    }
                }, "info", "server");
                response.post(node -> {
                    byte[] bytes = (byte[])node.value;
                    try {
                        crc.write(bytes, 0, bytes.length - 10);
                    } catch (IOException e) {
                    }
                }, "function", "dump");
                response.post(node -> {
                    byte[] bytes = (byte[]) node.value;
                    System.out.println(new String(bytes));
                }, "info", "keyspace");
                response.get();
                crc.write(RDB_OPCODE_EOF);
                crc.write(crc.getCRC64());
            } catch (IOException e) {
                
            }
        });
        thread.start();
        RedisRdbReplicator replicator = new RedisRdbReplicator(new XPipedInputStream(out), Configuration.defaultSetting());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof Function) {
                    Function function = (Function) event;
                    System.out.println(new String(function.getCode()));
                }
            }
        });
        replicator.open();
    }
}
