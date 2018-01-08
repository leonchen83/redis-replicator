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
import com.moilioncircle.redis.replicator.util.ByteArrayMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET_INTSET;

/**
 * @author Leon Chen
 * @since 2.4.4
 */
public abstract class HugeKVRdbListener extends RdbListener.Adaptor {

    private int batchSize = 64;
    private boolean order = true;

    public HugeKVRdbListener() {
    }

    public HugeKVRdbListener(int batchSize) {
        this(true, batchSize);
    }

    public HugeKVRdbListener(boolean order, int batchSize) {
        if (batchSize <= 0) throw new IllegalArgumentException(String.valueOf(batchSize));
        this.order = order;
        this.batchSize = batchSize;
    }

    public void handleString(boolean last, byte[] key, byte[] value, int type) {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public void handleList(boolean last, byte[] key, List<byte[]> list, int type) {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public void handleSet(boolean last, byte[] key, Set<byte[]> set, int type) {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public void handleMap(boolean last, byte[] key, Map<byte[], byte[]> map, int type) {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public void handleZSetEntry(boolean last, byte[] key, Set<ZSetEntry> set, int type) {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public void handleModule(boolean last, byte[] key, Module value, int type) {
        throw new UnsupportedOperationException("must implement this method.");
    }

    @Override
    public void handle(Replicator replicator, KeyValuePair<?> kv) {
        // Note that:
        // Every Iterator MUST be consumed.
        // Before every it.next() MUST check precondition it.hasNext()
        final byte[] key = kv.getRawKey();
        final int type = kv.getValueRdbType();
        if (kv instanceof KeyStringValueString) {
            KeyStringValueString ksvs = (KeyStringValueString) kv;
            handleString(true, key, ksvs.getRawValue(), type);
        } else if (kv instanceof KeyStringValueByteArrayIterator) {
            if (type == RDB_TYPE_SET || type == RDB_TYPE_SET_INTSET) {
                Iterator<byte[]> it = ((KeyStringValueByteArrayIterator) kv).getValue();
                Set<byte[]> prev = null, next = create(order, batchSize);
                while (it.hasNext()) {
                    next.add(it.next());
                    if (next.size() == batchSize) {
                        if (prev != null)
                            handleSet(false, key, prev, type);
                        prev = next;
                        next = create(order, batchSize);
                    }
                }
                final boolean last = next.isEmpty();
                handleSet(last, key, prev, type);
                if (!last) handleSet(true, key, next, type);
            } else {
                Iterator<byte[]> it = ((KeyStringValueByteArrayIterator) kv).getValue();
                List<byte[]> prev = null, next = new ArrayList<>(batchSize);
                while (it.hasNext()) {
                    try {
                        next.add(it.next());
                        if (next.size() == batchSize) {
                            if (prev != null)
                                handleList(false, key, prev, type);
                            prev = next;
                            next = new ArrayList<>(batchSize);
                        }
                    } catch (IllegalStateException e) {
                        // see ValueIterableRdbVisitor.QuickListIter.next().
                    }
                }
                final boolean last = next.isEmpty();
                handleList(last, key, prev, type);
                if (!last) handleList(true, key, next, type);
            }
        } else if (kv instanceof KeyStringValueMapEntryIterator) {
            Iterator<Map.Entry<byte[], byte[]>> it = ((KeyStringValueMapEntryIterator) kv).getValue();
            Map<byte[], byte[]> prev = null, next = new ByteArrayMap<>(order, batchSize);
            while (it.hasNext()) {
                Map.Entry<byte[], byte[]> entry = it.next();
                next.put(entry.getKey(), entry.getValue());
                if (next.size() == batchSize) {
                    if (prev != null)
                        handleMap(false, key, prev, type);
                    prev = next;
                    next = new ByteArrayMap<>(order, batchSize);
                }
            }
            final boolean last = next.isEmpty();
            handleMap(last, key, prev, type);
            if (!last) handleMap(true, key, next, type);
        } else if (kv instanceof KeyStringValueZSetEntryIterator) {
            Iterator<ZSetEntry> it = ((KeyStringValueZSetEntryIterator) kv).getValue();
            Set<ZSetEntry> prev = null, next = create(order, batchSize);
            while (it.hasNext()) {
                next.add(it.next());
                if (next.size() == batchSize) {
                    if (prev != null)
                        handleZSetEntry(false, key, prev, type);
                    prev = next;
                    next = create(order, batchSize);
                }
            }
            final boolean last = next.isEmpty();
            handleZSetEntry(last, key, prev, type);
            if (!last) handleZSetEntry(true, key, next, type);
        } else if (kv instanceof KeyStringValueModule) {
            KeyStringValueModule ksvs = (KeyStringValueModule) kv;
            handleModule(true, key, ksvs.getValue(), type);
        }
    }

    private <T> Set<T> create(boolean order, int batchSize) {
        return order ? new LinkedHashSet<T>(batchSize) : new HashSet<T>(batchSize);
    }
}
