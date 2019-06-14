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

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.event.PreRdbSyncEvent;
import com.moilioncircle.redis.replicator.io.CRCOutputStream;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.datatype.AuxField;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.util.ByteBuilder;
import com.moilioncircle.redis.replicator.util.type.Tuple2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_EOF;

/**
 * @author Leon Chen
 * @since 2.4.6
 */
public class RdbFilterExample {

    private static final String REDIS_MAGIC = "REDIS";
    private static final String REDIS_VERSION = "0007";

    public static void main(String[] args) throws IOException {
        RdbFilterExample rdb = new RdbFilterExample();
        rdb.filter("./src/test/resources/dumpV7.rdb", new Predicate<byte[]>() {
            @Override
            public boolean test(byte[] key) {
                return !new String(key).startsWith("test");
            }
        }, "./src/test/resources/dump-new.rdb");
    }

    @SuppressWarnings("resource")
    private void filter(String source, final Predicate<byte[]> filter, String target) throws IOException {
        final Replicator replicator = new RedisReplicator(new File(source), FileType.RDB, Configuration.defaultSetting());
        try (final CRCOutputStream out = new CRCOutputStream(new BufferedOutputStream(new FileOutputStream(new File(target))))) {
            //
            final AtomicBoolean header = new AtomicBoolean(false);
            final Tuple2<byte[], ByteBuilder> tuple = new Tuple2<>();
            tuple.setV2(ByteBuilder.allocate(128));
            final RawByteListener rawByteListener = new RawByteListener() {
                @Override
                public void handle(byte... rawBytes) {
                    if (tuple.getV1() != null) {
                        try {
                            byte[] ary = tuple.getV2().array();
                            if (ary.length > 9
                                    && header.compareAndSet(false, true)
                                    && Arrays.equals(REDIS_MAGIC.getBytes(), Arrays.copyOfRange(ary, 0, 5))) {
                                ary = Arrays.copyOfRange(ary, 9, ary.length);
                            }
                            if (filter.test(tuple.getV1())) out.write(ary);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }

                        tuple.setV1(null);
                        tuple.setV2(ByteBuilder.allocate(128));
                    }
                    for (byte b : rawBytes) tuple.getV2().put(b);
                }
            };

            replicator.addRawByteListener(rawByteListener);

            replicator.addEventListener(new EventListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onEvent(Replicator replicator, Event event) {
                    if (event instanceof AuxField) {
                        // clear aux field
                        tuple.setV2(ByteBuilder.allocate(128));
                    }
                    if (event instanceof PreRdbSyncEvent) {
                        try {
                            out.write(REDIS_MAGIC.getBytes());
                            out.write(REDIS_VERSION.getBytes());
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                    if (event instanceof KeyValuePair<?, ?>) {
                        tuple.setV1(((KeyValuePair<byte[], ?>) event).getKey());
                    }
                    if (event instanceof PostRdbSyncEvent) {
                        try {
                            out.write(RDB_OPCODE_EOF);
                            out.write(out.getCRC64());
                            out.close();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                }
            });

            replicator.open();
        }
    }
}
