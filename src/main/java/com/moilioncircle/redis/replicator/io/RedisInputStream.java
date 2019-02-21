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

package com.moilioncircle.redis.replicator.io;

import com.moilioncircle.redis.replicator.util.ByteArray;
import com.moilioncircle.redis.replicator.util.Strings;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class RedisInputStream extends InputStream {
    protected int head = 0;
    protected int tail = 0;
    protected long total = 0;
    protected long markLen = 0;
    protected final byte[] buf;
    protected boolean mark = false;
    protected final InputStream in;
    protected List<RawByteListener> rawByteListeners;

    public RedisInputStream(ByteArray array) {
        this(new ByteArrayInputStream(array));
    }

    public RedisInputStream(final InputStream in) {
        this(in, 8192);
    }

    public RedisInputStream(final InputStream in, int len) {
        this.in = in;
        this.buf = new byte[len];
    }

    /**
     * @param rawByteListeners raw byte listeners
     * @since 2.2.0
     */
    public synchronized void setRawByteListeners(List<RawByteListener> rawByteListeners) {
        this.rawByteListeners = rawByteListeners;
    }

    protected void notify(byte... bytes) {
        if (rawByteListeners == null || rawByteListeners.isEmpty()) return;
        for (RawByteListener listener : rawByteListeners) {
            listener.handle(bytes);
        }
    }

    public int head() {
        return head;
    }

    public int tail() {
        return tail;
    }

    public int bufSize() {
        return buf.length;
    }

    public boolean isMarked() {
        return mark;
    }

    public void mark(long len) {
        mark();
        markLen = len;
    }

    public void mark() {
        if (!mark) {
            mark = true;
            return;
        }
        throw new AssertionError("already marked");
    }

    public long unmark() {
        if (mark) {
            long rs = markLen;
            markLen = 0;
            mark = false;
            return rs;
        }
        throw new AssertionError("must mark first");
    }

    public long total() {
        return total;
    }

    public ByteArray readBytes(long len) throws IOException {
        ByteArray bytes = new ByteArray(len);
        this.read(bytes, 0, len);
        if (mark) markLen += len;
        return bytes;
    }

    public int readInt(int len) throws IOException {
        return readInt(len, true);
    }

    public long readLong(int len) throws IOException {
        return readLong(len, true);
    }

    public int readInt(int length, boolean littleEndian) throws IOException {
        int r = 0;
        for (int i = 0; i < length; ++i) {
            final int v = this.read();
            if (littleEndian) {
                r |= (v << (i << 3));
            } else {
                r = (r << 8) | v;
            }
        }
        int c;
        return r << (c = (4 - length << 3)) >> c;
    }

    public long readUInt(int length) throws IOException {
        return readUInt(length, true);
    }

    public long readUInt(int length, boolean littleEndian) throws IOException {
        return readInt(length, littleEndian) & 0xFFFFFFFFL;
    }

    public int readInt(byte[] bytes) {
        return readInt(bytes, true);
    }

    public int readInt(byte[] bytes, boolean littleEndian) {
        int r = 0;
        int length = bytes.length;
        for (int i = 0; i < length; ++i) {
            final int v = bytes[i] & 0xFF;
            if (littleEndian) {
                r |= (v << (i << 3));
            } else {
                r = (r << 8) | v;
            }
        }
        int c;
        return r << (c = (4 - length << 3)) >> c;
    }

    public long readLong(int length, boolean littleEndian) throws IOException {
        long r = 0;
        for (int i = 0; i < length; ++i) {
            final long v = this.read();
            if (littleEndian) {
                r |= (v << (i << 3));
            } else {
                r = (r << 8) | v;
            }
        }
        return r;
    }

    public String readString(int len) throws IOException {
        return Strings.toString(readBytes(len).first());
    }

    public String readString(int len, Charset charset) throws IOException {
        return Strings.toString(readBytes(len).first(), charset);
    }

    @Override
    public int read() throws IOException {
        if (head >= tail) fill();
        if (mark) markLen += 1;
        byte b = buf[head++];
        notify(b);
        return b & 0xff;
    }

    public long read(ByteArray bytes, long offset, long len) throws IOException {
        long total = len;
        long index = offset;
        while (total > 0) {
            int available = tail - head;
            if (available >= total) {
                ByteArray.arraycopy(new ByteArray(buf), head, bytes, index, total);
                head += total;
                break;
            } else {
                ByteArray.arraycopy(new ByteArray(buf), head, bytes, index, available);
                index += available;
                total -= available;
                fill();
            }
        }
        for (byte[] b : bytes) {
            notify(b);
        }
        return len;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return (int) read(new ByteArray(b), off, len);
    }

    @Override
    public int available() throws IOException {
        return tail - head + in.available();
    }

    public long skip(long len, boolean notify) throws IOException {
        long total = len;
        while (total > 0) {
            int available = tail - head;
            if (available >= total) {
                if (notify) notify(Arrays.copyOfRange(buf, head, head + (int) total));
                head += total;
                break;
            } else {
                if (notify) notify(Arrays.copyOfRange(buf, head, tail));
                total -= available;
                fill();
            }
        }
        return len;
    }

    @Override
    public long skip(long len) throws IOException {
        return skip(len, true);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    protected void fill() throws IOException {
        tail = in.read(buf, 0, buf.length);
        if (tail == -1) throw new EOFException("end of file or end of stream.");
        total += tail;
        head = 0;
    }
}
