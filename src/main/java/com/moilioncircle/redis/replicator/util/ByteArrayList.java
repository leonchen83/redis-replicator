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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.moilioncircle.redis.replicator.util.ByteArrayMap.Element;

/**
 * @author Leon Chen
 * @since 3.0.0
 */
//@NonThreadSafe
public class ByteArrayList extends AbstractList<byte[]> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected final List<Element> list;
    
    public ByteArrayList() {
        this(16);
    }
    
    public ByteArrayList(int initialCapacity) {
        this.list = new ArrayList<>(initialCapacity);
    }
    
    public ByteArrayList(Collection<? extends byte[]> c) {
        this(c == null ? 0 : c.size());
        addAll(c);
    }
    
    @Override
    public int size() {
        return list.size();
    }
    
    @Override
    public byte[] get(int index) {
        return list.get(index).bytes;
    }
    
    @Override
    public Iterator<byte[]> iterator() {
        return new Iter();
    }
    
    @Override
    public ListIterator<byte[]> listIterator() {
        return new ListIter();
    }
    
    @Override
    public byte[] set(int index, byte[] bytes) {
        Element element = list.set(index, new Element(bytes));
        return element != null ? element.bytes : null;
    }
    
    @Override
    public void add(int index, byte[] bytes) {
        list.add(index, new Element(bytes));
    }
    
    @Override
    public byte[] remove(int index) {
        Element element = list.remove(index);
        return element != null ? element.bytes : null;
    }
    
    private final class Iter implements Iterator<byte[]> {
        
        private Iterator<Element> it = list.iterator();
        
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
    
    private final class ListIter implements ListIterator<byte[]> {
        
        private ListIterator<Element> it = list.listIterator();
        
        @Override
        public boolean hasNext() {
            return it.hasNext();
        }
        
        @Override
        public byte[] next() {
            return it.next().bytes;
        }
        
        @Override
        public boolean hasPrevious() {
            return it.hasPrevious();
        }
        
        @Override
        public byte[] previous() {
            return it.previous().bytes;
        }
        
        @Override
        public int nextIndex() {
            return it.nextIndex();
        }
        
        @Override
        public int previousIndex() {
            return it.previousIndex();
        }
        
        @Override
        public void remove() {
            it.remove();
        }
        
        @Override
        public void set(byte[] bytes) {
            it.set(new Element(bytes));
        }
        
        @Override
        public void add(byte[] bytes) {
            it.add(new Element(bytes));
        }
    }
}
