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

import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_EOF;

/**
 * @author Leon Chen
 * @since 2.3.2
 */
@SuppressWarnings("resource")
public class SplitRdbExample {

    private static final String REDIS_MAGIC = "REDIS";
    private static final String REDIS_VERSION = "0007";

    public static void main(final String[] args) throws IOException {
        final Replicator replicator = new RedisReplicator(
                new File("./src/test/resources/dumpV7.rdb"), FileType.RDB,
                Configuration.defaultSetting());

        final int len = 4;
        final CRCOutputStream[] outs = new CRCOutputStream[len];
        final AtomicBoolean[] headers = new AtomicBoolean[len];
        final AtomicBoolean header = new AtomicBoolean(false);
        for (int i = 0; i < len; i++) {
            headers[i] = new AtomicBoolean(false);
            outs[i] = new CRCOutputStream(new BufferedOutputStream(new FileOutputStream(new File("./src/test/resources/dump-split-" + i + ".rdb"))));
        }
        final Tuple2<byte[], ByteBuilder> tuple = new Tuple2<>();
        tuple.setV2(ByteBuilder.allocate(128));

        final RawByteListener rawByteListener = new RawByteListener() {
            @Override
            public void handle(byte... rawBytes) {
                if (tuple.getV1() != null) {
                    try {
                        // write file by key hashcode sharding
                        int idx = tuple.getV1().hashCode() & (outs.length - 1);
                        if (headers[idx].compareAndSet(false, true)) {
                            // you know your redis version. so you know your rdb version.
                            outs[idx].write(REDIS_MAGIC.getBytes());
                            outs[idx].write(REDIS_VERSION.getBytes());
                        }
                        byte[] ary = tuple.getV2().array();
                        if (ary.length > 9
                                && header.compareAndSet(false, true)
                                && Arrays.equals(REDIS_MAGIC.getBytes(), Arrays.copyOfRange(ary, 0, 5))) {
                            outs[idx].write(ary, 9, ary.length - 9);
                        } else {
                            outs[idx].write(ary);
                        }
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
                if (event instanceof KeyValuePair<?, ?>) {
                    tuple.setV1(((KeyValuePair<byte[], ?>) event).getKey());
                }
                if (event instanceof PostRdbSyncEvent) {
                    for (int i = 0; i < len; i++) {
                        try {
                            CRCOutputStream out = outs[i];
                            out.write(RDB_OPCODE_EOF);
                            out.write(out.getCRC64());
                            out.close();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                }
            }
        });

        replicator.open();
    }
}
