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

import com.moilioncircle.examples.util.CRCOutputStream;
import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.UncheckedIOException;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.AuxFieldListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.AuxField;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.util.ByteBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

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
        rdb.filter("./src/test/resources/dumpV7.rdb", new Filter<byte[]>() {
            @Override
            public boolean test(byte[] key) {
                return !new String(key).startsWith("test");
            }
        }, "./src/test/resources/dump-new.rdb");
    }

    @SuppressWarnings("resource")
    private void filter(String source, final Filter<byte[]> filter, String target) throws IOException {
        final Replicator replicator = new RedisReplicator(new File(source), FileType.RDB, Configuration.defaultSetting());
        try (final CRCOutputStream out = new CRCOutputStream(new BufferedOutputStream(new FileOutputStream(new File(target))))) {
            //
            final AtomicBoolean header = new AtomicBoolean(false);
            final Tuple2<byte[], ByteBuilder> tuple = new Tuple2<>();
            tuple.setT2(ByteBuilder.allocate(128));
            final RawByteListener rawByteListener = new RawByteListener() {
                @Override
                public void handle(byte... rawBytes) {
                    if (tuple.getT1() != null) {
                        try {
                            byte[] ary = tuple.getT2().array();
                            if (ary.length > 9
                                    && header.compareAndSet(false, true)
                                    && Arrays.equals(REDIS_MAGIC.getBytes(), Arrays.copyOfRange(ary, 0, 5))) {
                                ary = Arrays.copyOfRange(ary, 9, ary.length);
                            }
                            if (filter.test(tuple.getT1())) out.write(ary);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }

                        tuple.setT1(null);
                        tuple.setT2(ByteBuilder.allocate(128));
                    }
                    for (byte b : rawBytes) tuple.getT2().put(b);
                }
            };

            replicator.addRawByteListener(rawByteListener);
            replicator.addAuxFieldListener(new AuxFieldListener() {
                @Override
                public void handle(Replicator replicator, AuxField auxField) {
                    // clear aux field
                    tuple.setT2(ByteBuilder.allocate(128));
                }
            });
            replicator.addRdbListener(new RdbListener.Adaptor() {

                @Override
                public void preFullSync(Replicator replicator) {
                    try {
                        out.write(REDIS_MAGIC.getBytes());
                        out.write(REDIS_VERSION.getBytes());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }

                @Override
                public void handle(Replicator replicator, KeyValuePair<?> kv) {
                    tuple.setT1(kv.getRawKey());
                }

                @Override
                public void postFullSync(Replicator replicator, long checksum) {
                    try {
                        out.write(RDB_OPCODE_EOF);
                        out.write(out.getCRC64());
                        out.close();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            });

            replicator.open();
        }
    }

    private interface Filter<T> {
        boolean test(T t);
    }

    private static class Tuple2<T1, T2> {
        private T1 t1;
        private T2 t2;

        private Tuple2() {
        }

        public T1 getT1() {
            return t1;
        }

        public void setT1(T1 t1) {
            this.t1 = t1;
        }

        public T2 getT2() {
            return t2;
        }

        public void setT2(T2 t2) {
            this.t2 = t2;
        }

        @Override
        public String toString() {
            return "<" + t1 + ", " + t2 + '>';
        }
    }
}
