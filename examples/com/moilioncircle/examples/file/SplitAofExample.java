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

package com.moilioncircle.examples.file;

import com.moilioncircle.redis.replicator.CloseListener;
import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.util.ByteBuilder;
import com.moilioncircle.redis.replicator.util.type.Tuple2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Leon Chen
 * @since 2.3.2
 */
public class SplitAofExample {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        final Replicator replicator = new RedisReplicator(
                new File("./src/test/resources/appendonly2.aof"), FileType.AOF,
                Configuration.defaultSetting());

        final OutputStream[] outs = new BufferedOutputStream[4];
        for (int i = 0; i < outs.length; i++) {
            outs[i] = new BufferedOutputStream(new FileOutputStream(new File("./src/test/resources/appendonly2-split-" + i + ".aof")));
        }

        final Tuple2<Boolean, ByteBuilder> tuple = new Tuple2<>();
        tuple.setV1(false);
        tuple.setV2(ByteBuilder.allocate(128));

        final RawByteListener rawByteListener = new RawByteListener() {
            @Override
            public void handle(byte... rawBytes) {
                if (tuple.getV1()) {
                    try {
                        int idx = ThreadLocalRandom.current().nextInt(outs.length);
                        outs[idx].write(tuple.getV2().array());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }

                    tuple.setV1(false);
                    tuple.setV2(ByteBuilder.allocate(128));
                }
                for (byte b : rawBytes) tuple.getV2().put(b);
            }
        };

        replicator.addRawByteListener(rawByteListener);

        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                //if you are using socket replication, open following comment so that avoid very big ByteBuilder.
//                if (event instanceof AuxField) {
//                    // clear aux field
//                    tuple.setT2(ByteBuilder.allocate(128));
//                }
//                if (event instanceof KeyValuePair<?, ?>) {
//                    tuple.setT2(ByteBuilder.allocate(128));
//                }
//                if (event instanceof PostFullSyncEvent) {
//                    tuple.setT2(ByteBuilder.allocate(128));
//                }
                if (event instanceof Command) {
                    tuple.setV1(true);
                }
            }
        });

        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                for (OutputStream out : outs) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        replicator.open();
    }
}
