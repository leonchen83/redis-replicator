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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.moilioncircle.redis.replicator.rdb.datatype.TTLValue;

/**
 * @author Leon Chen
 * @since 2.2.0
 */
public class TTLByteArrayMapTest {
    
    @Test
    public void test() {
        Map<byte[], TTLValue> m = new LinkedHashMap<>();
        m.put(new byte[]{1, 2, 3}, new TTLValue(System.currentTimeMillis(), new byte[]{4, 5, 6}));
        m.put(null, new TTLValue(new byte[]{4}));
        m.put(new byte[]{4, 5, 6}, null);
        TTLByteArrayMap bytes = new TTLByteArrayMap(m);
        assertEquals(3, bytes.size());
        assertEquals(true, Arrays.equals(new byte[]{4, 5, 6}, bytes.get(new byte[]{1, 2, 3}).getValue()));
        assertEquals(true, Arrays.equals(new byte[]{4}, bytes.get(null).getValue()));
        assertEquals(null, bytes.get(new byte[]{4, 5, 6}));
        assertEquals(false, bytes.isEmpty());
        assertEquals(true, bytes.containsKey(new byte[]{1, 2, 3}));
        assertEquals(true, bytes.containsKey(null));
        assertEquals(true, bytes.containsValue(new TTLValue(new byte[]{4, 5, 6})));
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

        for (TTLValue b : bytes.values()) {
            assertEquals(true, bytes.containsValue(b));
        }

        for (Map.Entry<byte[], TTLValue> entry : bytes.entrySet()) {
            assertEquals(true, bytes.containsKey(entry.getKey()));
            assertEquals(true, bytes.containsValue(entry.getValue()));
        }

        Set<Map.Entry<byte[], TTLValue>> ss = bytes.entrySet();
        Iterator<Map.Entry<byte[], TTLValue>> itr = ss.iterator();
        while (itr.hasNext()) {
            Map.Entry<byte[], TTLValue> entry = itr.next();
            assertEquals(true, ss.contains(entry));
            assertEquals(true, ss.contains(new TestEntry(entry.getKey(), entry.getValue())));
            if (entry.getValue() != null) {
                assertEquals(true, ss.contains(new TestEntry(entry.getKey(), new TTLValue(Arrays.copyOf(entry.getValue().getValue(), entry.getValue().getValue().length)))));
            } else {
                assertEquals(true, ss.contains(new TestEntry(entry.getKey(), null)));
            }
            assertEquals(true, bytes.containsKey(entry.getKey()));
            assertEquals(true, bytes.containsValue(entry.getValue()));
        }
    
        bytes = new TTLByteArrayMap(null);
        assertEquals(0, bytes.size());
        assertEquals(true, bytes.isEmpty());
    
        bytes = new TTLByteArrayMap(new HashMap<byte[], TTLValue>());
        assertEquals(0, bytes.size());
        assertEquals(true, bytes.isEmpty());
    
        bytes = new TTLByteArrayMap(m);
        bytes.put(new byte[]{1, 2, 3}, new TTLValue(new byte[]{4, 5, 6}));
        bytes.put(null, new TTLValue(new byte[]{4}));
        bytes.put(new byte[]{4, 5, 6}, null);
        s = bytes.keySet();
        s.remove(new byte[]{1, 2, 3});
        s.remove(null);
        s.remove(new byte[]{4, 5, 6});
        assertEquals(0, s.size());
        assertEquals(0, bytes.size());
    
        bytes = new TTLByteArrayMap(m);
        bytes.put(new byte[]{1, 2, 3}, new TTLValue(new byte[]{4, 5, 6}));
        bytes.put(null, new TTLValue(new byte[]{4}));
        bytes.put(new byte[]{4, 5, 6}, null);
        ss = bytes.entrySet();
        List<Map.Entry<byte[], TTLValue>> list = new ArrayList<>();
        for (Map.Entry<byte[], TTLValue> entry : ss) {
            list.add(new TestEntry(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<byte[], TTLValue> entry : list) {
            ss.remove(entry);
        }
        assertEquals(0, ss.size());
        assertEquals(0, bytes.size());
    
        bytes = new TTLByteArrayMap(m);
        bytes.put(new byte[]{1, 2, 3}, new TTLValue(new byte[]{4, 5, 6}));
        bytes.put(null, new TTLValue(new byte[]{4}));
        bytes.put(new byte[]{4, 5, 6}, null);
        Iterator<byte[]> a = bytes.keySet().iterator();
        while (a.hasNext()) {
            a.next();
            a.remove();
        }
        assertEquals(0, bytes.size());
    
        bytes = new TTLByteArrayMap(m);
        bytes.put(new byte[]{1, 2, 3}, new TTLValue(new byte[]{4, 5, 6}));
        bytes.put(null, new TTLValue(new byte[]{4}));
        bytes.put(new byte[]{4, 5, 6}, null);
        Iterator<Map.Entry<byte[], TTLValue>> aa = bytes.entrySet().iterator();
        while (aa.hasNext()) {
            aa.next();
            aa.remove();
        }
        assertEquals(0, bytes.size());
    }
    
    @Test
    public void test1() {
        Map<byte[], TTLValue> m = new LinkedHashMap<>();
        m.put(new byte[]{1, 2, 3}, new TTLValue(new byte[]{4, 5, 6}));
        m.put(null, new TTLValue(new byte[]{4}));
        m.put(new byte[]{4, 5, 6}, null);
        TTLByteArrayMap bytes = new TTLByteArrayMap(m);
        assertEquals(3, bytes.size());
        Iterator<TTLValue> it = bytes.values().iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        assertEquals(0, bytes.size());
    }
    
    @Test
    public void test2() {
        TTLByteArrayMap map = new TTLByteArrayMap();
        assertNull(map.get("a".getBytes()));
    }
    
    @Test
    public void testSerialize() throws IOException, ClassNotFoundException {
        Map<byte[], TTLValue> m = new LinkedHashMap<>();
        m.put(new byte[]{1, 2, 3}, new TTLValue(new byte[]{4, 5, 6}));
        m.put(null, new TTLValue(new byte[]{4}));
        m.put(new byte[]{4, 5, 6}, null);
        File file = new File("./test.txt");
        TTLByteArrayMap bytes = new TTLByteArrayMap(m);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(bytes);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        TTLByteArrayMap deseri = (TTLByteArrayMap) in.readObject();
        in.close();
        assertEquals(3, deseri.size());
        assertEquals(new TTLValue(new byte[]{4, 5, 6}), deseri.get(new byte[]{1, 2, 3}));
        assertEquals(new TTLValue(new byte[]{4}), deseri.get(null));
        assertEquals(null, deseri.get(new byte[]{4, 5, 6}));
        assertEquals(false, deseri.isEmpty());
        assertEquals(true, deseri.containsKey(new byte[]{1, 2, 3}));
        assertEquals(true, deseri.containsKey(null));
        assertEquals(true, deseri.containsValue(new TTLValue(new byte[]{4, 5, 6})));
        assertEquals(true, deseri.containsValue(null));
    }

    private final class TestEntry implements Map.Entry<byte[], TTLValue> {

        private TTLValue value;
        private final byte[] key;

        private TestEntry(byte[] key, TTLValue value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public byte[] getKey() {
            return this.key;
        }

        @Override
        public TTLValue getValue() {
            return this.value;
        }

        @Override
        public TTLValue setValue(TTLValue value) {
            TTLValue oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

}