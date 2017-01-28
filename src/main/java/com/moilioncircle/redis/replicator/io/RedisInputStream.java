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

package com.moilioncircle.redis.replicator.io;

import com.moilioncircle.redis.replicator.Constants;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by leon on 8/9/16.
 */
public class RedisInputStream extends InputStream {
    private int head = 0;
    private int tail = 0;
    private long total = 0;
    private long markLen = 0;
    private boolean mark = false;
    private final InputStream in;
    private final List<RawByteListener> listeners = new CopyOnWriteArrayList<>();

    private final byte[] buf;

    public RedisInputStream(final InputStream in) {
        this(in, 8192);
    }

    public RedisInputStream(final InputStream in, int len) {
        this.in = in;
        this.buf = new byte[len];
    }

    public void addRawByteListener(RawByteListener listener) {
        this.listeners.add(listener);
    }

    public void removeRawByteListener(RawByteListener listener) {
        this.listeners.remove(listener);
    }

    private void notify(byte... bytes) {
        for (RawByteListener listener : listeners) {
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

    public byte[] readBytes(int len) throws IOException {
        byte[] bytes = new byte[len];
        read(bytes, 0, len);
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
            final int v = bytes[i] & 0xff;
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
        return readString(len, Constants.CHARSET);
    }

    public String readString(int len, Charset charset) throws IOException {
        byte[] original = readBytes(len);
        return new String(original, charset);
    }

    @Override
    public int read() throws IOException {
        if (head >= tail) fill();
        if (mark) markLen += 1;
        byte b = buf[head++];
        notify(b);
        return b & 0xff;
    }

    @Override
    public int read(byte[] bytes, int offset, int len) throws IOException {
        int total = len;
        int index = offset;
        while (total > 0) {
            int available = tail - head;
            if (available >= total) {
                System.arraycopy(buf, head, bytes, index, total);
                head += total;
                break;
            } else {
                System.arraycopy(buf, head, bytes, index, available);
                index += available;
                total -= available;
                fill();
            }
        }
        notify(bytes);
        return len;
    }

    @Override
    public int available() throws IOException {
        return tail - head + in.available();
    }

    @Override
    public long skip(long len) throws IOException {
        long total = len;
        while (total > 0) {
            int available = tail - head;
            if (available >= total) {
                head += total;
                break;
            } else {
                total -= available;
                fill();
            }
        }
        return len;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    private void fill() throws IOException {
        tail = in.read(buf, 0, buf.length);
        if (tail == -1) throw new EOFException("end of file.");
        total += tail;
        head = 0;
    }
}
