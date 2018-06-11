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

package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.impl.HMSetCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SAddCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueHash;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueList;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Leon Chen
 * @since 2.2.0
 */
public class RdbBinaryTest {

    @org.junit.Test
    @SuppressWarnings("resource")
    public void testRdb() throws IOException {
        final List<KeyValuePair<?>> list = new ArrayList<>();
        Replicator r = new RedisReplicator(RdbBinaryTest.class.getClassLoader().getResourceAsStream("binarydump.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        r.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            
            }
    
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                list.add(kv);
            }
        
            @Override
            public void postFullSync(Replicator replicator, long checksum) {
            
            }
        });
        r.open();
        for (KeyValuePair<?> kv : list) {
            if (kv.getKey().equals("seri1")) {
                KeyStringValueString ksvs = (KeyStringValueString) kv;
                try {
                    Test obj = (Test) toObject(ksvs.getRawValue());
                    assertEquals("中文测试wuqioewqoi jdklsajf jslaj djsldfjlsjqweajdslfdl3019fjdsf9034930", obj.getA());
                    assertEquals(1000301032, obj.getB());
                    assertEquals(440910321039102L, obj.getC());
                } catch (IOException | ClassNotFoundException e) {
                    fail();
                }
            }
            if (kv.getKey().equals("seri2")) {
                KeyStringValueHash ksvs = (KeyStringValueHash) kv;
                try {
                    Test obj = (Test) toObject(ksvs.getRawValue(), "field2".getBytes());
                    assertEquals("中文测试12131", obj.getA());
                    assertEquals(1000301032, obj.getB());
                    assertEquals(440910321039102L, obj.getC());
                } catch (IOException | ClassNotFoundException e) {
                    fail();
                }
            }
            if (kv.getKey().equals("seri3")) {
                KeyStringValueList ksvs = (KeyStringValueList) kv;
                try {
                    Test obj = (Test) toObject(ksvs.getRawValue());
                    assertEquals("中文测试jfskdfjslf", obj.getA());
                    assertEquals(1000301032, obj.getB());
                    assertEquals(440910321039102L, obj.getC());
                } catch (IOException | ClassNotFoundException e) {
                    fail();
                }
            }
        }
    }

    @org.junit.Test
    @SuppressWarnings("resource")
    public void testAof() throws IOException {
        final List<Command> list = new ArrayList<>();
        Replicator r = new RedisReplicator(RdbBinaryTest.class.getClassLoader().getResourceAsStream("appendonly7.aof"), FileType.AOF,
                Configuration.defaultSetting());
        r.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                list.add(command);
            }
        });
        r.open();
        for (Command cmd : list) {
            if (cmd instanceof SetCommand) {
                SetCommand s = (SetCommand) cmd;
                try {
                    Test obj = (Test) toObject(s.getRawValue());
                    assertEquals("中文测试wuqioewqoi jdklsajf jslaj djsldfjlsjqweajdslfdl3019fjdsf9034930", obj.getA());
                    assertEquals(1000301032, obj.getB());
                    assertEquals(440910321039102L, obj.getC());
                } catch (IOException | ClassNotFoundException e) {
                    fail();
                }
            }
            if (cmd instanceof HMSetCommand) {
                HMSetCommand h = (HMSetCommand) cmd;
                try {
                    Test obj = (Test) toObject(h.getRawFields(), "field2".getBytes());
                    assertEquals("中文测试12131", obj.getA());
                    assertEquals(1000301032, obj.getB());
                    assertEquals(440910321039102L, obj.getC());
                } catch (IOException | ClassNotFoundException e) {
                    fail();
                }
            }
            if (cmd instanceof SAddCommand) {
                SAddCommand s = (SAddCommand) cmd;
                try {
                    Test obj = (Test) toObject(s.getRawMembers()[0]);
                    assertEquals("中文测试jfskdfjslf", obj.getA());
                    assertEquals(1000301032, obj.getB());
                    assertEquals(440910321039102L, obj.getC());
                } catch (IOException | ClassNotFoundException e) {
                    fail();
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
        try (ByteArrayInputStream bis = new ByteArrayInputStream(map.get(field));
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
