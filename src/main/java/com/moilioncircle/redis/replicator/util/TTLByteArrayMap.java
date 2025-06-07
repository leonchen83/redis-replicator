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

package com.moilioncircle.redis.replicator.util;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.moilioncircle.redis.replicator.rdb.datatype.TTLValue;
import static com.moilioncircle.redis.replicator.util.ByteArrayMap.Element;

/**
 * @author Leon Chen
 * @since 3.9.0
 */
//@NonThreadSafe
public class TTLByteArrayMap implements Map<byte[], TTLValue>, Serializable {
    private static final long serialVersionUID = 1L;
    
    protected final Map<Element, TTLValue> map;
    
    public TTLByteArrayMap(Map<? extends byte[], ? extends TTLValue> m) {
        this(true, m);
    }
    
    public TTLByteArrayMap(boolean ordered, Map<? extends byte[], ? extends TTLValue> m) {
        this(ordered, m == null ? 0 : m.size(), 0.75f);
        putAll(m);
    }
    
    public TTLByteArrayMap() {
        this(true);
    }
    
    public TTLByteArrayMap(boolean ordered) {
        this(ordered, 16);
    }
    
    public TTLByteArrayMap(boolean ordered, int initialCapacity) {
        this(ordered, initialCapacity, 0.75f);
    }
    
    public TTLByteArrayMap(boolean ordered, int initialCapacity, float loadFactor) {
        if (ordered) map = new LinkedHashMap<>(initialCapacity, loadFactor);
        else map = new HashMap<>(initialCapacity, loadFactor);
    }
    
    @Override
    public int size() {
        return map.size();
    }
    
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    @Override
    public boolean containsKey(Object key) {
        if (key != null && !(key instanceof byte[])) return false;
        return map.containsKey(new Element((byte[]) key));
    }
    
    @Override
    public boolean containsValue(Object value) {
        if (value == null) return false;
        if (value instanceof TTLValue) {
            TTLValue ev = (TTLValue) value;
            return map.containsValue(ev);
        }
        if (value instanceof byte[]) {
            return map.containsValue(new TTLValue((byte[]) value));
        }
        return false;
    }
    
    @Override
    public TTLValue get(Object key) {
        if (key != null && !(key instanceof byte[])) return null;
        return map.get(new Element((byte[]) key));
    }
    
    @Override
    public TTLValue put(byte[] key, TTLValue value) {
        return map.put(new Element(key), value);
    }
    
    @Override
    public void putAll(Map<? extends byte[], ? extends TTLValue> m) {
        if (m == null) return;
        for (Entry<? extends byte[], ? extends TTLValue> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public TTLValue remove(Object key) {
        if (key != null && !(key instanceof byte[])) return null;
        return map.remove(new Element((byte[]) key));
    }
    
    @Override
    public void clear() {
        map.clear();
    }
    
    @Override
    public Set<byte[]> keySet() {
        return new KeySet();
    }
    
    @Override
    public Collection<TTLValue> values() {
        return new Values();
    }
    
    @Override
    public Set<Entry<byte[], TTLValue>> entrySet() {
        return new EntrySet();
    }
    
    private final class EntrySet extends AbstractSet<Entry<byte[], TTLValue>> {
        
        @Override
        public final int size() {
            return TTLByteArrayMap.this.size();
        }
        
        @Override
        public final void clear() {
            TTLByteArrayMap.this.clear();
        }
        
        @Override
        public final Iterator<Entry<byte[], TTLValue>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Entry<?, ?> e = (Entry<?, ?>) o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (k != null && !(k instanceof byte[])) return false;
            if (v != null && !(v instanceof TTLValue)) return false;
            byte[] key = (byte[]) k;
            TTLValue value = (TTLValue) v;
            if (!TTLByteArrayMap.this.containsKey(key)) return false;
            TTLValue val = TTLByteArrayMap.this.get(key);
            return Objects.equals(val, value);
        }
        
        @Override
        public final boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Entry<?, ?> e = (Entry<?, ?>) o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (k != null && !(k instanceof byte[])) return false;
            if (v != null && !(v instanceof TTLValue)) return false;
            byte[] key = (byte[]) k;
            TTLValue value = (TTLValue) v;
            if (!TTLByteArrayMap.this.containsKey(key)) return false;
            TTLValue val = TTLByteArrayMap.this.get(key);
            if (Objects.equals(val, value))
                return TTLByteArrayMap.this.remove(key) != null;
            return false;
        }
    }
    
    private final class KeySet extends AbstractSet<byte[]> {
        
        @Override
        public final int size() {
            return TTLByteArrayMap.this.size();
        }
        
        @Override
        public final void clear() {
            TTLByteArrayMap.this.clear();
        }
        
        @Override
        public final Iterator<byte[]> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public final boolean contains(Object o) {
            return TTLByteArrayMap.this.containsKey(o);
        }
        
        @Override
        public final boolean remove(Object key) {
            return TTLByteArrayMap.this.remove(key) != null;
        }
    }
    
    private final class Values extends AbstractCollection<TTLValue> {
        
        @Override
        public final int size() {
            return TTLByteArrayMap.this.size();
        }
        
        @Override
        public final void clear() {
            TTLByteArrayMap.this.clear();
        }
        
        @Override
        public final Iterator<TTLValue> iterator() {
            return new ValueIterator();
        }
        
        @Override
        public final boolean contains(Object o) {
            return containsValue(o);
        }
        
    }
    
    private final class KeyIterator implements Iterator<byte[]> {
    
        private final Iterator<Element> iterator = map.keySet().iterator();
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
    
        @Override
        public byte[] next() {
            return iterator.next().bytes;
        }
    
        @Override
        public void remove() {
            iterator.remove();
        }
    }
    
    private final class ValueIterator implements Iterator<TTLValue> {
        
        private final Iterator<TTLValue> iterator = map.values().iterator();
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        @Override
        public TTLValue next() {
            return iterator.next();
        }
        
        @Override
        public void remove() {
            iterator.remove();
        }
    }
    
    private final class EntryIterator implements Iterator<Entry<byte[], TTLValue>> {
        
        private final Iterator<Entry<Element, TTLValue>> iterator = map.entrySet().iterator();
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        @Override
        public Entry<byte[], TTLValue> next() {
            Entry<Element, TTLValue> v = iterator.next();
            return new Node(v.getKey().bytes, v.getValue());
        }
        
        @Override
        public void remove() {
            iterator.remove();
        }
    }
    
    public static final class Node implements Entry<byte[], TTLValue>, Serializable {
        private static final long serialVersionUID = 1L;
        
        private final byte[] key;
        private TTLValue value;
        
        private Node(byte[] key, TTLValue value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public byte[] getKey() {
            return key;
        }
        
        @Override
        public TTLValue getValue() {
            return this.value;
        }
        
        @Override
        public TTLValue setValue(TTLValue value) {
            TTLValue oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(value, node.value) && Arrays.equals(key, node.key);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(value, Arrays.hashCode(key));
        }
    }
}
