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

package com.moilioncircle.redis.replicator.rdb.datatype;

import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueHash;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueList;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueModule;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueSet;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueStream;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueZSet;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.KeyStringValueByteArrayIterator;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.KeyStringValueMapEntryIterator;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.KeyStringValueZSetEntryIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Leon Chen
 * @since 3.1.0
 */
public class KeyValuePairs {

    /*
     * Base
     */
    public static KeyValuePair<byte[], byte[]> string(KeyValuePair<byte[], ?> raw, byte[] value) {
        KeyStringValueString kv = new KeyStringValueString();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    public static KeyValuePair<byte[], Module> module(KeyValuePair<byte[], ?> raw, Module value) {
        KeyStringValueModule kv = new KeyStringValueModule();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    public static KeyValuePair<byte[], Map<byte[], byte[]>> hash(KeyValuePair<byte[], ?> raw, Map<byte[], byte[]> value) {
        KeyStringValueHash kv = new KeyStringValueHash();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    public static KeyValuePair<byte[], List<byte[]>> list(KeyValuePair<byte[], ?> raw, List<byte[]> value) {
        KeyStringValueList kv = new KeyStringValueList();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    public static KeyValuePair<byte[], Set<byte[]>> set(KeyValuePair<byte[], ?> raw, Set<byte[]> value) {
        KeyStringValueSet kv = new KeyStringValueSet();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    public static KeyValuePair<byte[], Set<ZSetEntry>> zset(KeyValuePair<byte[], ?> raw, Set<ZSetEntry> value) {
        KeyStringValueZSet kv = new KeyStringValueZSet();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    public static KeyValuePair<byte[], Stream> stream(KeyValuePair<byte[], ?> raw, Stream value) {
        KeyStringValueStream kv = new KeyStringValueStream();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    /*
     * Iterator
     */
    public static KeyStringValueMapEntryIterator iterHash(KeyValuePair<byte[], ?> raw, Iterator<Map.Entry<byte[], byte[]>> value) {
        KeyStringValueMapEntryIterator kv = new KeyStringValueMapEntryIterator();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    public static KeyStringValueByteArrayIterator iterList(KeyValuePair<byte[], ?> raw, Iterator<byte[]> value) {
        KeyStringValueByteArrayIterator kv = new KeyStringValueByteArrayIterator();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    public static KeyStringValueByteArrayIterator iterSet(KeyValuePair<byte[], ?> raw, Iterator<byte[]> value) {
        KeyStringValueByteArrayIterator kv = new KeyStringValueByteArrayIterator();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    public static KeyStringValueZSetEntryIterator iterZset(KeyValuePair<byte[], ?> raw, Iterator<ZSetEntry> value) {
        KeyStringValueZSetEntryIterator kv = new KeyStringValueZSetEntryIterator();
        copy(raw, kv);
        kv.setValue(value);
        return kv;
    }

    /*
     * Batched
     */
    public static BatchedKeyStringValueString string(KeyValuePair<byte[], ?> raw, byte[] value, int batch, boolean last) {
        BatchedKeyStringValueString kv = new BatchedKeyStringValueString();
        copy(raw, kv, batch, last);
        kv.setValue(value);
        return kv;
    }

    public static BatchedKeyStringValueModule module(KeyValuePair<byte[], ?> raw, Module value, int batch, boolean last) {
        BatchedKeyStringValueModule kv = new BatchedKeyStringValueModule();
        copy(raw, kv, batch, last);
        kv.setValue(value);
        return kv;
    }

    public static BatchedKeyStringValueHash hash(KeyValuePair<byte[], ?> raw, Map<byte[], byte[]> value, int batch, boolean last) {
        BatchedKeyStringValueHash kv = new BatchedKeyStringValueHash();
        copy(raw, kv, batch, last);
        kv.setValue(value);
        return kv;
    }

    public static BatchedKeyStringValueList list(KeyValuePair<byte[], ?> raw, List<byte[]> value, int batch, boolean last) {
        BatchedKeyStringValueList kv = new BatchedKeyStringValueList();
        copy(raw, kv, batch, last);
        kv.setValue(value);
        return kv;
    }

    public static BatchedKeyStringValueSet set(KeyValuePair<byte[], ?> raw, Set<byte[]> value, int batch, boolean last) {
        BatchedKeyStringValueSet kv = new BatchedKeyStringValueSet();
        copy(raw, kv, batch, last);
        kv.setValue(value);
        return kv;
    }

    public static BatchedKeyStringValueZSet zset(KeyValuePair<byte[], ?> raw, Set<ZSetEntry> value, int batch, boolean last) {
        BatchedKeyStringValueZSet kv = new BatchedKeyStringValueZSet();
        copy(raw, kv, batch, last);
        kv.setValue(value);
        return kv;
    }

    public static BatchedKeyStringValueStream stream(KeyValuePair<byte[], ?> raw, Stream value, int batch, boolean last) {
        BatchedKeyStringValueStream kv = new BatchedKeyStringValueStream();
        copy(raw, kv, batch, last);
        kv.setValue(value);
        return kv;
    }

    /*
     * Helper
     */
    private static void copy(KeyValuePair<byte[], ?> source, KeyValuePair<byte[], ?> target) {
        target.setContext(source.getContext());
        target.setDb(source.getDb());
        target.setExpiredType(source.getExpiredType());
        target.setExpiredValue(source.getExpiredValue());
        target.setEvictType(source.getEvictType());
        target.setEvictValue(source.getEvictValue());
        target.setValueRdbType(source.getValueRdbType());
        target.setKey(source.getKey());
    }

    private static void copy(KeyValuePair<byte[], ?> source, BatchedKeyValuePair<byte[], ?> target, int batch, boolean last) {
        target.setContext(source.getContext());
        target.setDb(source.getDb());
        target.setExpiredType(source.getExpiredType());
        target.setExpiredValue(source.getExpiredValue());
        target.setEvictType(source.getEvictType());
        target.setEvictValue(source.getEvictValue());
        target.setValueRdbType(source.getValueRdbType());
        target.setKey(source.getKey());
        target.setBatch(batch);
        target.setLast(last);
    }
}
