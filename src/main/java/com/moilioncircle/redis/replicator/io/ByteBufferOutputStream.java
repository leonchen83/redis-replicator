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

package com.moilioncircle.redis.replicator.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Leon Chen
 * @since 3.5.5
 */
public class ByteBufferOutputStream extends OutputStream {
    
    protected byte[] buf;
    protected int count;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    public ByteBufferOutputStream() {
        this(32);
    }
    
    public ByteBufferOutputStream(int size) {
        buf = new byte[size];
    }
    
    @Override
    public void write(int b) {
        ensureCapacity(count + 1);
        buf[count] = (byte) b;
        count += 1;
    }
    
    @Override
    public void write(byte[] b, int off, int len) {
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    public void writeBytes(byte b[]) {
        write(b, 0, b.length);
    }
    
    public void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }
    
    public void reset() {
        count = 0;
    }
    
    public int size() {
        return count;
    }
    
    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(buf, 0, count);
    }
    
    private void ensureCapacity(int capacity) {
        if (capacity - buf.length > 0) grow(capacity);
    }
    
    private void grow(int minCapacity) {
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        buf = Arrays.copyOf(buf, newCapacity);
    }
    
    private static int hugeCapacity(int capacity) {
        if (capacity < 0) throw new OutOfMemoryError();
        return (capacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }
}
