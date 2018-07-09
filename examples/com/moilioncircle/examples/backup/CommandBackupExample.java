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

package com.moilioncircle.examples.backup;

import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.io.RawByteListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@SuppressWarnings("resource")
public class CommandBackupExample {
    public static void main(String[] args) throws IOException, URISyntaxException {
        final OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("/path/to/appendonly.aof")));
        final RawByteListener rawByteListener = new RawByteListener() {
            @Override
            public void handle(byte... rawBytes) {
                try {
                    out.write(rawBytes);
                } catch (IOException ignore) {
                }
            }
        };

        //save 1000 records commands
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    replicator.addRawByteListener(rawByteListener);
                }
                if (event instanceof Command) {
                    if (acc.incrementAndGet() == 1000) {
                        try {
                            out.close();
                            replicator.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        replicator.open();

        //check aof file
        replicator = new RedisReplicator("redis:///path/to/appendonly.aof");
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                System.out.println(event);
            }
        });
        replicator.open();
    }
}
