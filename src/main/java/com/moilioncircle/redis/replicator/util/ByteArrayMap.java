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
import java.util.Set;

/**
 * @author Leon Chen
 * @since 2.2.0
 */
//@NonThreadSafe
public class ByteArrayMap implements Map<byte[], byte[]>, Serializable {
    private static final long serialVersionUID = 1L;
    
    protected final Map<Element, Element> map;
    
    public ByteArrayMap(Map<? extends byte[], ? extends byte[]> m) {
        this(true, m);
    }
    
    public ByteArrayMap(boolean ordered, Map<? extends byte[], ? extends byte[]> m) {
        this(ordered, m == null ? 0 : m.size(), 0.75f);
        putAll(m);
    }
    
    public ByteArrayMap() {
        this(true);
    }
    
    public ByteArrayMap(boolean ordered) {
        this(ordered, 16);
    }
    
    public ByteArrayMap(boolean ordered, int initialCapacity) {
        this(ordered, initialCapacity, 0.75f);
    }
    
    public ByteArrayMap(boolean ordered, int initialCapacity, float loadFactor) {
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
        if (value != null && !(value instanceof byte[])) return false;
        return map.containsValue(new Element((byte[]) value));
    }
    
    @Override
    public byte[] get(Object key) {
        if (key != null && !(key instanceof byte[])) return null;
        return map.get(new Element((byte[]) key)).bytes;
    }
    
    @Override
    public byte[] put(byte[] key, byte[] value) {
        Element element = map.put(new Element(key), new Element(value));
        return element != null ? element.bytes : null;
    }
    
    @Override
    public void putAll(Map<? extends byte[], ? extends byte[]> m) {
        if (m == null) return;
        for (Map.Entry<? extends byte[], ? extends byte[]> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public byte[] remove(Object key) {
        if (key != null && !(key instanceof byte[])) return null;
        return map.remove(new Element((byte[]) key)).bytes;
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
    public Collection<byte[]> values() {
        return new Values();
    }
    
    @Override
    public Set<Entry<byte[], byte[]>> entrySet() {
        return new EntrySet();
    }
    
    public static final class Element implements Serializable {
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
    
    private final class EntrySet extends AbstractSet<Entry<byte[], byte[]>> {
        
        @Override
        public final int size() {
            return ByteArrayMap.this.size();
        }
        
        @Override
        public final void clear() {
            ByteArrayMap.this.clear();
        }
        
        @Override
        public final Iterator<Entry<byte[], byte[]>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (k != null && !(k instanceof byte[])) return false;
            if (v != null && !(v instanceof byte[])) return false;
            byte[] key = (byte[]) k;
            byte[] value = (byte[]) v;
            if (!ByteArrayMap.this.containsKey(key)) return false;
            byte[] val = ByteArrayMap.this.get(key);
            return Arrays.equals(val, value);
        }
        
        @Override
        public final boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (k != null && !(k instanceof byte[])) return false;
            if (v != null && !(v instanceof byte[])) return false;
            byte[] key = (byte[]) k;
            byte[] value = (byte[]) v;
            if (!ByteArrayMap.this.containsKey(key)) return false;
            byte[] val = ByteArrayMap.this.get(key);
            if (Arrays.equals(val, value))
                return ByteArrayMap.this.remove(key) != null;
            return false;
        }
    }
    
    private final class KeySet extends AbstractSet<byte[]> {
        
        @Override
        public final int size() {
            return ByteArrayMap.this.size();
        }
        
        @Override
        public final void clear() {
            ByteArrayMap.this.clear();
        }
        
        @Override
        public final Iterator<byte[]> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public final boolean contains(Object o) {
            return ByteArrayMap.this.containsKey(o);
        }
        
        @Override
        public final boolean remove(Object key) {
            return ByteArrayMap.this.remove(key) != null;
        }
    }
    
    private final class Values extends AbstractCollection<byte[]> {
        
        @Override
        public final int size() {
            return ByteArrayMap.this.size();
        }
        
        @Override
        public final void clear() {
            ByteArrayMap.this.clear();
        }
        
        @Override
        public final Iterator<byte[]> iterator() {
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
    
    private final class ValueIterator implements Iterator<byte[]> {
        
        private final Iterator<Element> iterator = map.values().iterator();
        
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
    
    private final class EntryIterator implements Iterator<Map.Entry<byte[], byte[]>> {
        
        private final Iterator<Map.Entry<Element, Element>> iterator = map.entrySet().iterator();
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        @Override
        public Entry<byte[], byte[]> next() {
            Map.Entry<Element, Element> v = iterator.next();
            return new Node(v.getKey().bytes, v.getValue().bytes);
        }
        
        @Override
        public void remove() {
            iterator.remove();
        }
    }
    
    public static final class Node implements Map.Entry<byte[], byte[]>, Serializable {
        private static final long serialVersionUID = 1L;
        
        private byte[] value;
        private final byte[] key;
        
        private Node(byte[] key, byte[] value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public byte[] getKey() {
            return key;
        }
        
        @Override
        public byte[] getValue() {
            return this.value;
        }
        
        @Override
        public byte[] setValue(byte[] value) {
            byte[] oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Arrays.equals(value, node.value) &&
                    Arrays.equals(key, node.key);
        }
        
        @Override
        public int hashCode() {
            int result = Arrays.hashCode(value);
            result = 31 * result + Arrays.hashCode(key);
            return result;
        }
    }
}
