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

import com.moilioncircle.redis.replicator.rdb.datatype.ExpirableValue;

/**
 * @author Leon Chen
 * @since 2.2.0
 */
//@NonThreadSafe
public class ByteArrayExpirableMap implements Map<byte[], ExpirableValue>, Serializable {
    private static final long serialVersionUID = 1L;
    
    protected final Map<Element, ExpirableValue> map;
    
    public ByteArrayExpirableMap(Map<? extends byte[], ? extends ExpirableValue> m) {
        this(true, m);
    }
    
    public ByteArrayExpirableMap(boolean ordered, Map<? extends byte[], ? extends ExpirableValue> m) {
        this(ordered, m == null ? 0 : m.size(), 0.75f);
        putAll(m);
    }
    
    public ByteArrayExpirableMap() {
        this(true);
    }
    
    public ByteArrayExpirableMap(boolean ordered) {
        this(ordered, 16);
    }
    
    public ByteArrayExpirableMap(boolean ordered, int initialCapacity) {
        this(ordered, initialCapacity, 0.75f);
    }
    
    public ByteArrayExpirableMap(boolean ordered, int initialCapacity, float loadFactor) {
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
        if (value instanceof ExpirableValue) {
            ExpirableValue ev = (ExpirableValue) value;
            return map.containsValue(ev);
        }
        if (value instanceof byte[]) {
            return map.containsValue(new ExpirableValue(0L, (byte[]) value));
        }
        return false;
    }
    
    @Override
    public ExpirableValue get(Object key) {
        if (key != null && !(key instanceof byte[])) return null;
        return map.get(new Element((byte[]) key));
    }
    
    @Override
    public ExpirableValue put(byte[] key, ExpirableValue value) {
        return map.put(new Element(key), value);
    }
    
    @Override
    public void putAll(Map<? extends byte[], ? extends ExpirableValue> m) {
        if (m == null) return;
        for (Entry<? extends byte[], ? extends ExpirableValue> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public ExpirableValue remove(Object key) {
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
    public Collection<ExpirableValue> values() {
        return new Values();
    }
    
    @Override
    public Set<Entry<byte[], ExpirableValue>> entrySet() {
        return new EntrySet();
    }
    
    private static final class Element implements Serializable {
        private static final long serialVersionUID = 1L;
    
        final byte[] bytes;
    
        Element(byte[] bytes) {
            this.bytes = bytes;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Element key = (Element) o;
            return Arrays.equals(bytes, key.bytes);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(bytes);
        }
    }
    
    private final class EntrySet extends AbstractSet<Entry<byte[], ExpirableValue>> {
        
        @Override
        public final int size() {
            return ByteArrayExpirableMap.this.size();
        }
        
        @Override
        public final void clear() {
            ByteArrayExpirableMap.this.clear();
        }
        
        @Override
        public final Iterator<Entry<byte[], ExpirableValue>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Entry<?, ?> e = (Entry<?, ?>) o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (k != null && !(k instanceof byte[])) return false;
            if (v != null && !(v instanceof ExpirableValue)) return false;
            byte[] key = (byte[]) k;
            ExpirableValue value = (ExpirableValue) v;
            if (!ByteArrayExpirableMap.this.containsKey(key)) return false;
            ExpirableValue val = ByteArrayExpirableMap.this.get(key);
            return Objects.equals(val, value);
        }
        
        @Override
        public final boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Entry<?, ?> e = (Entry<?, ?>) o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (k != null && !(k instanceof byte[])) return false;
            if (v != null && !(v instanceof ExpirableValue)) return false;
            byte[] key = (byte[]) k;
            ExpirableValue value = (ExpirableValue) v;
            if (!ByteArrayExpirableMap.this.containsKey(key)) return false;
            ExpirableValue val = ByteArrayExpirableMap.this.get(key);
            if (Objects.equals(val, value))
                return ByteArrayExpirableMap.this.remove(key) != null;
            return false;
        }
    }
    
    private final class KeySet extends AbstractSet<byte[]> {
        
        @Override
        public final int size() {
            return ByteArrayExpirableMap.this.size();
        }
        
        @Override
        public final void clear() {
            ByteArrayExpirableMap.this.clear();
        }
        
        @Override
        public final Iterator<byte[]> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public final boolean contains(Object o) {
            return ByteArrayExpirableMap.this.containsKey(o);
        }
        
        @Override
        public final boolean remove(Object key) {
            return ByteArrayExpirableMap.this.remove(key) != null;
        }
    }
    
    private final class Values extends AbstractCollection<ExpirableValue> {
        
        @Override
        public final int size() {
            return ByteArrayExpirableMap.this.size();
        }
        
        @Override
        public final void clear() {
            ByteArrayExpirableMap.this.clear();
        }
        
        @Override
        public final Iterator<ExpirableValue> iterator() {
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
    
    private final class ValueIterator implements Iterator<ExpirableValue> {
        
        private final Iterator<ExpirableValue> iterator = map.values().iterator();
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        @Override
        public ExpirableValue next() {
            return iterator.next();
        }
        
        @Override
        public void remove() {
            iterator.remove();
        }
    }
    
    private final class EntryIterator implements Iterator<Entry<byte[], ExpirableValue>> {
        
        private final Iterator<Entry<Element, ExpirableValue>> iterator = map.entrySet().iterator();
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        @Override
        public Entry<byte[], ExpirableValue> next() {
            Entry<Element, ExpirableValue> v = iterator.next();
            return new Node(v.getKey().bytes, v.getValue());
        }
        
        @Override
        public void remove() {
            iterator.remove();
        }
    }
    
    public static final class Node implements Entry<byte[], ExpirableValue>, Serializable {
        private static final long serialVersionUID = 1L;
        
        private final byte[] key;
        private ExpirableValue value;
        
        private Node(byte[] key, ExpirableValue value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public byte[] getKey() {
            return key;
        }
        
        @Override
        public ExpirableValue getValue() {
            return this.value;
        }
        
        @Override
        public ExpirableValue setValue(ExpirableValue value) {
            ExpirableValue oldValue = this.value;
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
