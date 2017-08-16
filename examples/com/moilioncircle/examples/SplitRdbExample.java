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

package com.moilioncircle.examples;

import com.moilioncircle.redis.replicator.*;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.AuxFieldListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.AuxField;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.util.ByteBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Leon Chen
 * @since 2.3.2
 */
@SuppressWarnings("resource")
public class SplitRdbExample {

    public static void main(final String[] args) throws IOException {
        final Replicator replicator = new RedisReplicator(
                new File("./src/test/resources/dumpV7.rdb"), FileType.RDB,
                Configuration.defaultSetting());

        final FileOutputStream[] outs = new FileOutputStream[4];
        final AtomicBoolean[] heads = new AtomicBoolean[outs.length];
        for (int i = 0; i < outs.length; i++) {
            heads[i] = new AtomicBoolean(true);
            outs[i] = new FileOutputStream(new File("./src/test/resources/dump-split-" + i + ".rdb"));
        }
        final Tuple2<String, ByteBuilder> tuple = new Tuple2<>();
        tuple.setT2(ByteBuilder.allocate(128));

        final RawByteListener rawByteListener = new RawByteListener() {
            @Override
            public void handle(byte... rawBytes) {
                if (tuple.getT1() != null) {
                    try {
                        // write file by key hashcode sharding
                        int idx = tuple.getT1().hashCode() & (outs.length - 1);
                        if (heads[idx].compareAndSet(true, false)) {
                            // you know your redis version. so you know your rdb version.
                            byte[] head = "REDIS0007".getBytes();
                            outs[idx].write(head);
                        }
                        byte[] ary = tuple.getT2().array();
                        byte[] head = Arrays.copyOfRange(ary, 0, 9);
                        if (Arrays.equals("REDIS0007".getBytes(), head)) {
                            outs[idx].write(ary, 9, ary.length - 9);
                        } else {
                            outs[idx].write(ary);
                        }
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
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                tuple.setT1(kv.getKey());
            }

            public void postFullSync(Replicator replicator, long checksum) {
                for (FileOutputStream out : outs) {
                    try {
                        out.write(Constants.RDB_OPCODE_EOF);
                        // if you want to load data from split rdb file which we generated.
                        // You MUST close rdbchecksum in redis.conf.
                        // Because this checksum is not correct.
                        out.write(longToByteArray(checksum));
                        out.close();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
        });

        replicator.open();
    }

    private static byte[] longToByteArray(long value) {
        return new byte[]{
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24),
                (byte) (value >> 32),
                (byte) (value >> 40),
                (byte) (value >> 48),
                (byte) (value >> 56),
        };
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
