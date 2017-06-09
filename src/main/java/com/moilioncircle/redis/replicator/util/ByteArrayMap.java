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

package com.moilioncircle.redis.replicator.util;

import java.util.*;
import java.util.Arrays;

/**
 * read only byte[] map
 *
 * @author Leon Chen
 * @since 2.2.0
 */
public class ByteArrayMap<V> implements Map<byte[], V> {

    private final Map<Key, V> map;

    public ByteArrayMap(Map<? extends byte[], ? extends V> m) {
        this(true, m);
    }

    public ByteArrayMap(boolean ordered, Map<? extends byte[], ? extends V> m) {
        this(ordered, m == null ? 0 : m.size(), 0.75f);
        internalPutAll(m);
    }

    private ByteArrayMap(boolean ordered, int initialCapacity, float loadFactor) {
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
        return map.containsKey(new Key((byte[]) key));
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (key != null && !(key instanceof byte[])) return null;
        return map.get(new Key((byte[]) key));
    }

    @Override
    public V put(byte[] key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends byte[], ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<byte[]> keySet() {
        return Collections.unmodifiableSet(new KeySet());
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(map.values());
    }

    @Override
    public Set<Entry<byte[], V>> entrySet() {
        return Collections.unmodifiableSet(new EntrySet());
    }

    private void internalPutAll(Map<? extends byte[], ? extends V> m) {
        if (m == null) return;
        for (Map.Entry<? extends byte[], ? extends V> entry : m.entrySet()) {
            map.put(new Key(entry.getKey()), entry.getValue());
        }
    }

    private static final class Key {

        private final byte[] bytes;

        private Key(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Arrays.equals(bytes, key.bytes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(bytes);
        }
    }

    private final class EntrySet extends AbstractSet<Entry<byte[], V>> {

        public final int size() {
            return ByteArrayMap.this.size();
        }

        @Override
        public final Iterator<Entry<byte[], V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            Object obj = e.getKey();
            if (obj != null && !(obj instanceof byte[])) return false;
            byte[] key = (byte[]) obj;
            if (!ByteArrayMap.this.containsKey(key)) return false;
            V v = ByteArrayMap.this.get(key);
            return v != null ? v.equals(e.getValue()) : e.getValue() == v;
        }
    }

    private final class KeySet extends AbstractSet<byte[]> {

        @Override
        public final int size() {
            return ByteArrayMap.this.size();
        }

        @Override
        public final Iterator<byte[]> iterator() {
            return new KeyIterator();
        }

        @Override
        public final boolean contains(Object o) {
            return ByteArrayMap.this.containsKey(o);
        }
    }

    private final class KeyIterator implements Iterator<byte[]> {

        private final Iterator<Key> iterator = map.keySet().iterator();

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
            throw new UnsupportedOperationException();
        }
    }

    private final class EntryIterator implements Iterator<Map.Entry<byte[], V>> {

        private final Iterator<Map.Entry<Key, V>> iterator = map.entrySet().iterator();

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<byte[], V> next() {
            Map.Entry<Key, V> v = iterator.next();
            return new Node(v.getKey().bytes, v.getValue());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private final class Node implements Map.Entry<byte[], V> {

        private final V v;
        private final byte[] bytes;

        private Node(byte[] bytes, V v) {
            this.bytes = bytes;
            this.v = v;
        }

        @Override
        public byte[] getKey() {
            return bytes;
        }

        @Override
        public V getValue() {
            return v;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            if (v != null ? !v.equals(node.v) : node.v != null) return false;
            return Arrays.equals(bytes, node.bytes);
        }

        @Override
        public int hashCode() {
            int result = v != null ? v.hashCode() : 0;
            result = 31 * result + Arrays.hashCode(bytes);
            return result;
        }
    }
}
