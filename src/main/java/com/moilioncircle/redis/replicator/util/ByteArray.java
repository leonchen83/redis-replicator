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

import java.util.Iterator;

/**
 * Created by leon on 1/29/17.
 */
public class ByteArray implements Iterable<byte[]> {
    public static final long MIN_VALUE = 0L;
    public static final long MAX_VALUE = 4611686014132420609L; //Integer.MAX_VALUE * Integer.MAX_VALUE

    private byte[][] bytes;
    private final long length;

    public ByteArray(byte[] bytes) {
        this.length = bytes.length;
        this.bytes = new byte[1][bytes.length];
        this.bytes[0] = bytes;
    }

    public ByteArray(long length) {
        this.length = length;
        if (length > MAX_VALUE) {
            throw new IllegalArgumentException(String.valueOf(length));
        } else {
            int x = (int) (length / Integer.MAX_VALUE);
            int y = (int) (length % Integer.MAX_VALUE);
            bytes = new byte[x + 1][];
            for (int i = 0; i < bytes.length; i++) {
                if (i == bytes.length - 1) {
                    bytes[i] = new byte[y];
                } else {
                    bytes[i] = new byte[Integer.MAX_VALUE];
                }
            }
        }
    }

    public void set(long idx, byte value) {
        int x = (int) (idx / Integer.MAX_VALUE);
        int y = (int) (idx % Integer.MAX_VALUE);
        bytes[x][y] = value;
    }

    public byte get(long idx) {
        int x = (int) (idx / Integer.MAX_VALUE);
        int y = (int) (idx % Integer.MAX_VALUE);
        return bytes[x][y];
    }

    public long length() {
        return this.length;
    }

    public byte[] first() {
        return this.iterator().next();
    }

    @Override
    public Iterator<byte[]> iterator() {
        return new Iter();
    }

    public static void arraycopy(ByteArray src, long srcPos, ByteArray dest, long destPos, long length) {
        if (srcPos + length > src.length || destPos + length > dest.length) {
            throw new IndexOutOfBoundsException();
        }
        while (length > 0) {
            int x1 = (int) (srcPos / Integer.MAX_VALUE);
            int y1 = (int) (srcPos % Integer.MAX_VALUE);
            int x2 = (int) (destPos / Integer.MAX_VALUE);
            int y2 = (int) (destPos % Integer.MAX_VALUE);
            int min = Math.min(Integer.MAX_VALUE - y1, Integer.MAX_VALUE - y2);
            if (length <= Integer.MAX_VALUE) min = Math.min(min, (int) length);
            System.arraycopy(src.bytes[x1], y1, dest.bytes[x2], y2, min);
            srcPos += min;
            destPos += min;
            length -= min;
        }
        assert length == 0;
    }

    private class Iter implements Iterator<byte[]> {
        int index = 0;

        @Override
        public boolean hasNext() {
            return index < bytes.length;
        }

        @Override
        public byte[] next() {
            return bytes[index++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
