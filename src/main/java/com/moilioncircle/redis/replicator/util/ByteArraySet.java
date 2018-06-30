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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.moilioncircle.redis.replicator.util.ByteArrayMap.Element;

/**
 * @author Leon Chen
 * @since 3.0.0
 */
//@NonThreadSafe
public class ByteArraySet extends AbstractCollection<byte[]> implements Set<byte[]>, Serializable {
    private static final long serialVersionUID = 1L;
    
    private Set<Element> set;
    
    public ByteArraySet(Set<? extends byte[]> m) {
        this(true, m);
    }
    
    public ByteArraySet(boolean ordered, Set<? extends byte[]> m) {
        this(ordered, m == null ? 0 : m.size(), 0.75f);
        addAll(m);
    }
    
    public ByteArraySet() {
        this(true);
    }
    
    public ByteArraySet(boolean ordered) {
        this(ordered, 16);
    }
    
    public ByteArraySet(boolean ordered, int initialCapacity) {
        this(ordered, initialCapacity, 0.75f);
    }
    
    public ByteArraySet(boolean ordered, int initialCapacity, float loadFactor) {
        if (ordered) set = new LinkedHashSet<>(initialCapacity, loadFactor);
        else set = new HashSet<>(initialCapacity, loadFactor);
    }
    
    public Iterator<byte[]> iterator() {
        return new Iter();
    }
    
    public int size() {
        return set.size();
    }
    
    public boolean isEmpty() {
        return set.isEmpty();
    }
    
    public boolean contains(Object o) {
        return set.contains(new Element((byte[]) o));
    }
    
    public boolean add(byte[] e) {
        return set.add(new Element(e));
    }
    
    public boolean remove(Object o) {
        return set.remove(new Element((byte[]) o));
    }
    
    public void clear() {
        set.clear();
    }
    
    private final class Iter implements Iterator<byte[]> {
        
        private Iterator<Element> it = set.iterator();
        
        @Override
        public boolean hasNext() {
            return it.hasNext();
        }
        
        @Override
        public byte[] next() {
            return it.next().bytes;
        }
        
        @Override
        public void remove() {
            it.remove();
        }
    }
}
