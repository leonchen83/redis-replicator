package com.moilioncircle.redis.replicator.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by Baoyi Chen on 2017/6/9.
 */
public class ByteArrayMapTest {
    @Test
    public void test() {
        Map<byte[], byte[]> m = new LinkedHashMap<>();
        m.put(new byte[]{1, 2, 3}, new byte[]{4, 5, 6});
        m.put(null, new byte[]{4});
        m.put(new byte[]{4, 5, 6}, null);
        ByteArrayMap<byte[]> bytes = new ByteArrayMap<>(m);
        assertEquals(3, bytes.size());
        assertEquals(true, Arrays.equals(new byte[]{4, 5, 6}, bytes.get(new byte[]{1, 2, 3})));
        assertEquals(true, Arrays.equals(new byte[]{4}, bytes.get(null)));
        assertEquals(null, bytes.get(new byte[]{4, 5, 6}));
        assertEquals(false, bytes.isEmpty());
        assertEquals(true, bytes.containsKey(new byte[]{1, 2, 3}));
        assertEquals(true, bytes.containsKey(null));
        assertEquals(false, bytes.containsKey(1));
        assertEquals(false, bytes.containsValue(new byte[]{4, 5, 6}));
        assertEquals(true, bytes.containsValue(null));

        Set<byte[]> s = bytes.keySet();

        Iterator<byte[]> it = s.iterator();
        while (it.hasNext()) {
            byte[] key = it.next();
            assertEquals(true, s.contains(key));
            assertEquals(true, bytes.containsKey(key));
        }

        for (byte[] b : bytes.keySet()) {
            assertEquals(true, bytes.containsKey(b));
        }

        for (byte[] b : bytes.values()) {
            assertEquals(true, bytes.containsValue(b));
        }

        for (Map.Entry<byte[], byte[]> entry : bytes.entrySet()) {
            assertEquals(true, bytes.containsKey(entry.getKey()));
            assertEquals(true, bytes.containsValue(entry.getValue()));
        }

        Set<Map.Entry<byte[], byte[]>> ss = bytes.entrySet();
        Iterator<Map.Entry<byte[], byte[]>> itr = ss.iterator();
        while (itr.hasNext()) {
            Map.Entry<byte[], byte[]> entry = itr.next();
            assertEquals(true, ss.contains(entry));
            assertEquals(true, ss.contains(new TestEntry(entry.getKey(), entry.getValue())));
            if (entry.getValue() != null) {
                assertEquals(false, ss.contains(new TestEntry(entry.getKey(), Arrays.copyOf(entry.getValue(), entry.getValue().length))));
            } else {
                assertEquals(true, ss.contains(new TestEntry(entry.getKey(), null)));
            }
            assertEquals(true, bytes.containsKey(entry.getKey()));
            assertEquals(true, bytes.containsValue(entry.getValue()));
        }

        bytes = new ByteArrayMap<>(null);
        assertEquals(0, bytes.size());
        assertEquals(true, bytes.isEmpty());

        bytes = new ByteArrayMap<>(new HashMap<byte[], byte[]>());
        assertEquals(0, bytes.size());
        assertEquals(true, bytes.isEmpty());
    }

    private final class TestEntry implements Map.Entry<byte[], byte[]> {

        private final byte[] key;
        private final byte[] value;

        private TestEntry(byte[] key, byte[] value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public byte[] getKey() {
            return this.key;
        }

        @Override
        public byte[] getValue() {
            return this.value;
        }

        @Override
        public byte[] setValue(byte[] value) {
            throw new UnsupportedOperationException();
        }
    }

}