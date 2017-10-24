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

package com.moilioncircle.examples.huge;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueModule;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.KeyStringValueByteArrayIterator;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.KeyStringValueMapEntryIterator;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.KeyStringValueZSetEntryIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Leon Chen
 * @since 2.4.4
 */
public abstract class HugeKVRdbListener extends RdbListener.Adaptor {

    private int batchSize = 100;

    public HugeKVRdbListener() {
    }

    public HugeKVRdbListener(int batchSize) {
        this.batchSize = batchSize;
    }

    public abstract void handleString(boolean last, byte[] key, byte[] value, int type);

    public abstract void handleModule(boolean last, byte[] key, Module value, int type);

    public abstract void handleList(boolean last, byte[] key, List<byte[]> list, int type);

    public abstract void handleZSetEntry(boolean last, byte[] key, List<ZSetEntry> list, int type);

    public abstract void handleMap(boolean last, byte[] key, List<Map.Entry<byte[], byte[]>> list, int type);

    @Override
    public void handle(Replicator replicator, KeyValuePair<?> kv) {
        /*
         * Note that:
         * 1. Every Iterator MUST be consumed.
         * 2. Before every it.next() MUST check precondition it.hasNext()
         */
        if (kv instanceof KeyStringValueString) {
            KeyStringValueString ksvs = (KeyStringValueString) kv;
            handleString(true, ksvs.getRawKey(), ksvs.getRawValue(), ksvs.getValueRdbType());
        } else if (kv instanceof KeyStringValueByteArrayIterator) {
            byte[] key = kv.getRawKey();
            Iterator<byte[]> it = ((KeyStringValueByteArrayIterator) kv).getValue();
            List<byte[]> list = new ArrayList<>();
            while (it.hasNext()) {
                try {
                    byte[] v = it.next();
                    list.add(v);
                    if (list.size() == batchSize) {
                        handleList(false, key, list, kv.getValueRdbType());
                        list = new ArrayList<>();
                    }
                } catch (IllegalStateException e) {
                    // do nothing is OK.
                    // see ValueIterableRdbVisitor.QuickListIter.next().
                }
            }
            // last batch.
            if (!list.isEmpty()) {
                handleList(true, key, list, kv.getValueRdbType());
            }
        } else if (kv instanceof KeyStringValueMapEntryIterator) {
            byte[] key = kv.getRawKey();
            Iterator<Map.Entry<byte[], byte[]>> it = ((KeyStringValueMapEntryIterator) kv).getValue();
            List<Map.Entry<byte[], byte[]>> list = new ArrayList<>();
            while (it.hasNext()) {
                Map.Entry<byte[], byte[]> v = it.next();
                list.add(v);
                if (list.size() == batchSize) {
                    handleMap(false, key, list, kv.getValueRdbType());
                    list = new ArrayList<>();
                }
            }
            // last batch.
            if (!list.isEmpty()) {
                handleMap(true, key, list, kv.getValueRdbType());
            }
        } else if (kv instanceof KeyStringValueZSetEntryIterator) {
            byte[] key = kv.getRawKey();
            Iterator<ZSetEntry> it = ((KeyStringValueZSetEntryIterator) kv).getValue();
            List<ZSetEntry> list = new ArrayList<>();
            while (it.hasNext()) {
                ZSetEntry v = it.next();
                list.add(v);
                if (list.size() == batchSize) {
                    handleZSetEntry(false, key, list, kv.getValueRdbType());
                    list = new ArrayList<>();
                }
            }
            // last batch.
            if (!list.isEmpty()) {
                handleZSetEntry(true, key, list, kv.getValueRdbType());
            }
        } else if (kv instanceof KeyStringValueModule) {
            KeyStringValueModule ksvs = (KeyStringValueModule) kv;
            handleModule(true, ksvs.getRawKey(), ksvs.getValue(), kv.getValueRdbType());
        }
    }
}
