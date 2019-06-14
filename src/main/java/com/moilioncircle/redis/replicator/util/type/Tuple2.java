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
public class Tuple2<T1, T2> implements Iterable<Object>, Serializable {
    private static final long serialVersionUID = 1L;
    private T1 v1;
    private T2 v2;

    public Tuple2() {
    }

    public Tuple2(T1 v1, T2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public Tuple2(Tuple2<T1, T2> rhs) {
        this.v1 = rhs.getV1();
        this.v2 = rhs.getV2();
    }

    public T1 getV1() {
        return v1;
    }

    public T2 getV2() {
        return v2;
    }

    public void setV1(T1 v1) {
        this.v1 = v1;
    }

    public void setV2(T2 v2) {
        this.v2 = v2;
    }

    public <V1, V2> Tuple2<V1, V2> map(Function<Tuple2<T1, T2>, Tuple2<V1, V2>> function) {
        return function.apply(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

        if (v1 != null ? !v1.equals(tuple2.v1) : tuple2.v1 != null) return false;
        return v2 != null ? v2.equals(tuple2.v2) : tuple2.v2 == null;
    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        return result;
    }

    @Override
    public Iterator<Object> iterator() {
        return Iterators.iterator(getV1(), getV2());
    }

    @Override
    public String toString() {
        return "[" + v1 + ", " + v2 + "]";
    }

    public static <V> Tuple2<V, V> from(V... ary) {
        if (ary == null || ary.length != 2) throw new IllegalArgumentException();
        return new Tuple2<>(ary[0], ary[1]);
    }

    public static <V> Tuple2<V, V> from(Iterator<V> iterator) {
        List<V> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return from(list.toArray((V[]) new Object[list.size()]));
    }

    public static <V> Tuple2<V, V> from(Iterable<V> iterable) {
        return from(iterable.iterator());
    }

    public static <V> Tuple2<V, V> from(Collection<V> collection) {
        return from((Iterable<V>) collection);
    }

    public Object[] toArray() {
        return new Object[]{getV1(), getV2()};
    }

    public <T> T[] toArray(Class<T> clazz) {
        T[] ary = (T[]) Array.newInstance(clazz, 5);
        if (!clazz.isInstance(getV1())) throw new UnsupportedOperationException();
        ary[0] = (T) getV1();
        if (!clazz.isInstance(getV2())) throw new UnsupportedOperationException();
        ary[1] = (T) getV2();
        return ary;
    }

    public <T> T toObject(Function<Tuple2<T1, T2>, T> func) {
        return func.apply(this);
    }
}
