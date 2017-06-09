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

package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueHash;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueList;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import junit.framework.TestCase;

import java.io.*;
import java.util.*;

/**
 * @author Leon Chen
 * @since 2.2.0
 */
public class RdbBinaryTest {
    @SuppressWarnings("resource")
    @org.junit.Test
    public void test() throws IOException {
        final List<KeyValuePair<?>> list = new ArrayList<>();
        Replicator r = new RedisReplicator(RdbBinaryTest.class.getClassLoader().getResourceAsStream("binarydump.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        r.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                list.add(kv);
            }
        });
        r.open();
        for (KeyValuePair<?> kv : list) {
            if (kv.getKey().equals("seri1")) {
                KeyStringValueString ksvs = (KeyStringValueString) kv;
                try {
                    Test obj = (Test) toObject(ksvs.getRawValue());
                    TestCase.assertEquals("中文测试wuqioewqoi jdklsajf jslaj djsldfjlsjqweajdslfdl3019fjdsf9034930", obj.getA());
                    TestCase.assertEquals(1000301032, obj.getB());
                    TestCase.assertEquals(440910321039102L, obj.getC());
                } catch (IOException | ClassNotFoundException e) {
                    TestCase.fail();
                }
            }
            if (kv.getKey().equals("seri2")) {
                KeyStringValueHash ksvs = (KeyStringValueHash) kv;
                try {
                    Test obj = (Test) toObject(ksvs.getRawValue(), "field2".getBytes());
                    TestCase.assertEquals("中文测试12131", obj.getA());
                    TestCase.assertEquals(1000301032, obj.getB());
                    TestCase.assertEquals(440910321039102L, obj.getC());
                } catch (IOException | ClassNotFoundException e) {
                    TestCase.fail();
                }
            }
            if (kv.getKey().equals("seri3")) {
                KeyStringValueList ksvs = (KeyStringValueList) kv;
                try {
                    Test obj = (Test) toObject(ksvs.getRawValue());
                    TestCase.assertEquals("中文测试jfskdfjslf", obj.getA());
                    TestCase.assertEquals(1000301032, obj.getB());
                    TestCase.assertEquals(440910321039102L, obj.getC());
                } catch (IOException | ClassNotFoundException e) {
                    TestCase.fail();
                }
            }
        }
    }

    private Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    private Object toObject(Map<byte[], byte[]> map, byte[] field) throws IOException, ClassNotFoundException {
        Map<String, byte[]> m = new HashMap<>();
        for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
            m.put(Arrays.toString(entry.getKey()), entry.getValue());
        }
        try (ByteArrayInputStream bis = new ByteArrayInputStream(m.get(Arrays.toString(field)));
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    private Object toObject(List<byte[]> list) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(list.get(0));
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    @SuppressWarnings("serial")
    static class Test implements Serializable {
        /**
         *
         */
        private String a;
        private int b;
        private long c;

        public Test() {

        }

        public Test(String a, int b, long c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public long getC() {
            return c;
        }

        public void setC(long c) {
            this.c = c;
        }
    }
}
