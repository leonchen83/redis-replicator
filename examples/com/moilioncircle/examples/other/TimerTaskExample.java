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

package com.moilioncircle.examples.other;

import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.event.PreRdbSyncEvent;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@SuppressWarnings("resource")
public class TimerTaskExample {
    public static void main(String[] args) throws IOException {
        final Timer timer = new Timer("sync");
        timer.schedule(new Task(), 30000, 30000);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                timer.cancel();
            }
        });
        System.in.read();
    }

    private static class Task extends TimerTask {
        @Override
        public void run() {
            try {
                Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
                replicator.addEventListener(new EventListener() {
                    @Override
                    public void onEvent(Replicator replicator, Event event) {
                        if (event instanceof PreRdbSyncEvent) {
                            System.out.println("data sync started");
                        }
                        if (event instanceof KeyValuePair<?, ?>) {
                            //shard kv.getKey to different thread so that speed up save process.
                            save((KeyValuePair<?, ?>) event);
                        }
                        if (event instanceof PostRdbSyncEvent) {
                            try {
                                replicator.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("data sync done");
                        }
                    }
                });

                replicator.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void save(KeyValuePair<?, ?> kv) {
        System.out.println(kv);
        //save kv to mysql or to anywhere.
    }
}
