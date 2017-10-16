/*
 * Copyright 2016 leon chen
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

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueModule;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;
import com.moilioncircle.redis.replicator.rdb.iterable.ValueIterableRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.KeyStringValueByteArrayIterator;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.KeyStringValueMapEntryIterator;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.KeyStringValueZSetEntryIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Leon Chen
 * @since 2.5.0
 */
@SuppressWarnings("resource")
public class HugeKVExample {
    private static final int BATCH_SIZE = 100;

    public static void main(String[] args) throws IOException {
        Replicator r = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {

                /*
                 * Note that:
                 * 1. Every Iterator MUST be consumed.
                 * 2. Before every it.next() MUST check precondition it.hasNext()
                 */
                if (kv instanceof KeyStringValueString) {
                    KeyStringValueString ksvs = (KeyStringValueString) kv;
                    // your business code
                } else if (kv instanceof KeyStringValueByteArrayIterator) {
                    // handle huge KV
                    byte[] key = kv.getRawKey();
                    Iterator<byte[]> it = ((KeyStringValueByteArrayIterator) kv).getValue();
                    List<byte[]> list = new ArrayList<>();
                    while (it.hasNext()) {
                        try {
                            byte[] v = it.next();
                            list.add(v);
                            if (list.size() == BATCH_SIZE) {
                                // your business code goes here.

                                // rebuild list.
                                list = new ArrayList<>();
                            }
                        } catch (IllegalStateException e) {
                            // do nothing is OK.
                            // see ValueIterableRdbVisitor.QuickListIter.next().
                        }
                    }
                    // last batch.
                    if (!list.isEmpty()) {
                        // your business code goes here.
                    }
                } else if (kv instanceof KeyStringValueMapEntryIterator) {
                    // handle huge KV
                    byte[] key = kv.getRawKey();
                    Iterator<Map.Entry<byte[], byte[]>> it = ((KeyStringValueMapEntryIterator) kv).getValue();
                    List<Map.Entry<byte[], byte[]>> list = new ArrayList<>();
                    while (it.hasNext()) {
                        Map.Entry<byte[], byte[]> v = it.next();
                        list.add(v);
                        if (list.size() == BATCH_SIZE) {
                            // your business code goes here.

                            // rebuild list.
                            list = new ArrayList<>();
                        }
                    }
                    // last batch.
                    if (!list.isEmpty()) {
                        // your business code goes here.
                    }
                } else if (kv instanceof KeyStringValueZSetEntryIterator) {
                    // handle huge KV
                    byte[] key = kv.getRawKey();
                    Iterator<ZSetEntry> it = ((KeyStringValueZSetEntryIterator) kv).getValue();
                    List<ZSetEntry> list = new ArrayList<>();
                    while (it.hasNext()) {
                        ZSetEntry v = it.next();
                        list.add(v);
                        if (list.size() == BATCH_SIZE) {
                            // your business code goes here.

                            // rebuild list.
                            list = new ArrayList<>();
                        }
                    }
                    // last batch.
                    if (!list.isEmpty()) {
                        // your business code goes here.
                    }
                } else if (kv instanceof KeyStringValueModule) {
                    KeyStringValueModule ksvs = (KeyStringValueModule) kv;
                    // your business code
                }
            }
        });
        r.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                System.out.println(command);
            }
        });
        r.open();
    }
}
