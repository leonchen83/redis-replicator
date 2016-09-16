package com.moilioncircle.redis.replicator.util;

import com.moilioncircle.redis.replicator.Constants;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leon on 8/14/16.
 */
//NonThreadSafe
public class ByteBuilder {

    private final ByteBuffer buffer;
    private final List<byte[]> list = new ArrayList<>();
    private int total = 0;

    private ByteBuilder(int cap) {
        this.buffer = ByteBuffer.allocate(cap);
    }

    public static ByteBuilder allocate(int cap) {
        return new ByteBuilder(cap);
    }

    public void put(byte b) {
        total++;
        if (buffer.hasRemaining()) {
            buffer.put(b);
        } else {
            byte[] temp = new byte[buffer.capacity()];
            System.arraycopy(buffer.array(), 0, temp, 0, buffer.capacity());
            list.add(temp);
            buffer.clear();
            buffer.put(b);
        }
    }

    public int length() {
        return total;
    }

    public byte[] array() {
        int len = total;
        if (len < buffer.capacity()) return buffer.array();
        int offset = 0;
        byte[] ary = new byte[len];
        for (byte[] ba : list) {
            System.arraycopy(ba, 0, ary, offset, ba.length);
            offset += ba.length;
            len -= ba.length;
        }
        if (len > 0) System.arraycopy(buffer.array(), 0, ary, offset, len);
        return ary;
    }

    @Override
    public String toString() {
        return new String(array(), 0, total, Constants.CHARSET);
    }
}
