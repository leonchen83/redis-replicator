/*
 * Copyright 2018-2019 Leon Chen
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

package com.moilioncircle.redis.replicator.util.type;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import com.moilioncircle.redis.replicator.util.Iterators;

/**
 * @author Leon Chen
 */
@SuppressWarnings("unchecked")
public class Tuple1<T1> implements Iterable<T1>, Serializable {
    private static final long serialVersionUID = 1L;
    private T1 v1;

    public Tuple1() {
    }

    public Tuple1(T1 v1) {
        this.v1 = v1;
    }

    public Tuple1(Tuple1<T1> rhs) {
        this.v1 = rhs.getV1();
    }

    public T1 getV1() {
        return v1;
    }

    public void setV1(T1 v1) {
        this.v1 = v1;
    }

    public <V1> Tuple1<V1> map(Function<Tuple1<T1>, Tuple1<V1>> function) {
        return function.apply(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple1<?> tuple1 = (Tuple1<?>) o;

        return v1 != null ? v1.equals(tuple1.v1) : tuple1.v1 == null;
    }

    @Override
    public int hashCode() {
        return v1 != null ? v1.hashCode() : 0;
    }

    @Override
    public Iterator<T1> iterator() {
        return Iterators.iterator(getV1());
    }

    @Override
    public String toString() {
        return "[" + v1 + "]";
    }

    public static <V> Tuple1<V> from(V... ary) {
        if (ary == null || ary.length != 1) throw new IllegalArgumentException();
        return new Tuple1<>(ary[0]);
    }

    public static <V> Tuple1<V> from(Iterator<V> iterator) {
        List<V> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return from(list.toArray((V[]) new Object[list.size()]));
    }

    public static <V> Tuple1<V> from(Iterable<V> iterable) {
        return from(iterable.iterator());
    }

    public static <V> Tuple1<V> from(Collection<V> collection) {
        return from((Iterable<V>) collection);
    }

    public Object[] toArray() {
        return new Object[]{getV1()};
    }

    public <T> T[] toArray(Class<T> clazz) {
        T[] ary = (T[]) Array.newInstance(clazz, 5);
        if (!clazz.isInstance(getV1())) throw new UnsupportedOperationException();
        ary[0] = (T) getV1();
        return ary;
    }

    public <T> T toObject(Function<Tuple1<T1>, T> func) {
        return func.apply(this);
    }
}