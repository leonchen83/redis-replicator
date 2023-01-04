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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.BaseRdbEncoder;
import com.moilioncircle.redis.replicator.rdb.BaseRdbParser;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class LzfTest {
    @Test
    public void decode() throws Exception {
        {
            String str = "abcdsklafjslfjfd;sfdklafjlsafjslfjasl;fkjdsalfjasfjlas;dkfjalsvlasfkal;sj";
            byte[] out = compress(str.getBytes());
            ByteArray in = Lzf.decode(new ByteArray(out), str.getBytes().length);
            assertEquals(new String(in.first()), str);
        }

        {
            InputStream in = LzfTest.class.getClassLoader().getResourceAsStream("low-comp-120k.txt");
            byte[] bytes = new byte[121444];
            int len = in.read(bytes);
            byte[] out = compress(bytes);
            ByteArray bin = Lzf.decode(new ByteArray(out), len);
            byte[] oin = bin.first();
            for (int i = 0; i < len; i++) {
                assertEquals(oin[i], bytes[i]);
            }
        }

        {
            InputStream in = LzfTest.class.getClassLoader().getResourceAsStream("appendonly6.aof");
            byte[] bytes = new byte[3949];
            int len = in.read(bytes);
            byte[] out = compress(bytes);
            ByteArray bin = Lzf.decode(new ByteArray(out), len);
            byte[] oin = bin.first();
            for (int i = 0; i < len; i++) {
                assertEquals(oin[i], bytes[i]);
            }
        }

    }
    
    @Test
    public void encode() throws Exception {
        
        {
            String str = "use lz4 compress if length large than 20 bytes";
            byte[] out = compress(str.getBytes());
            byte[] out1 = compress1(str.getBytes());
            assertEquals(out.length, out1.length);
            assertArrayEquals(out, out1);
        }
        
        {
            String str = "thisisalongstringthatcancompressbylzfthisisalongstringthatcancompressbylzf";
            byte[] out = compress(str.getBytes());
            byte[] out1 = compress1(str.getBytes());
            assertTrue(str.getBytes().length > out.length);
            assertEquals(out.length, out1.length);
            assertArrayEquals(out, out1);
        }
        
        {
            String str = "abcdsklafjslfjfd;sfdklafjlsafjslfjasl;fkjdsalfjasfjlas;dkfjalsvlasfkal;sj";
            byte[] out = compress(str.getBytes());
            byte[] out1 = compress1(str.getBytes());
            assertEquals(out.length, out1.length);
            assertArrayEquals(out, out1);
        }
    
        {
            InputStream in = LzfTest.class.getClassLoader().getResourceAsStream("low-comp-120k.txt");
            byte[] bytes = new byte[121444];
            in.read(bytes);
            byte[] out = compress(bytes);
            byte[] out1 = compress1(bytes);
            assertEquals(out.length, out1.length);
            assertArrayEquals(out, out1);
        }
    
        {
            InputStream in = LzfTest.class.getClassLoader().getResourceAsStream("appendonly6.aof");
            byte[] bytes = new byte[3949];
            in.read(bytes);
            byte[] out = compress(bytes);
            byte[] out1 = compress1(bytes);
            assertEquals(out.length, out1.length);
            assertArrayEquals(out, out1);
        }
    }
    
    @Test
    public void test1() {
        for (int i = 0; i < 1000; i++) {
            int length = ThreadLocalRandom.current().nextInt(50000) + 20;
            byte[] value = new byte[length];
            ThreadLocalRandom.current().nextBytes(value);
            byte[] out = compress(value);
            byte[] out1 = compress1(value);
            assertEquals(out.length, out1.length);
            assertArrayEquals(out, out1);
        }
    }
    
    @Test
    public void test2() throws Exception {
        BaseRdbEncoder encoder = new BaseRdbEncoder();
        for (int i = 0; i < 100; i++) {
            int length = ThreadLocalRandom.current().nextInt(10000000) + 20;
            byte[] value = new byte[length];
            ThreadLocalRandom.current().nextBytes(value);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.rdbGenericSaveStringObject(new ByteArray(value), out);
            byte[] encoded = out.toByteArray();
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(new ByteArray(encoded)));
            byte[] decoded = parser.rdbLoadEncodedStringObject().first();
            assertArrayEquals(decoded, value);
        }
    }

    private byte[] compress(byte[] in) {
        CompressLZF c = new CompressLZF();
        byte[] compressed = new byte[in.length -4];
        int idx = c.compress(in, in.length, compressed, 0);
        if (idx <= 0) {
            return in;
        } else {
            byte[] out = new byte[idx];
            System.arraycopy(compressed, 0, out, 0, out.length);
            return out;
        }
        
    }
    
    private byte[] compress1(byte[] in) {
        ByteArray out = Lzf.encode(new ByteArray(in));
        return out.first();
    }

}