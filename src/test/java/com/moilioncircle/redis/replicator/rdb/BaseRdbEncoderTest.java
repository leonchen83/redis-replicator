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

package com.moilioncircle.redis.replicator.rdb;

import static com.moilioncircle.redis.replicator.Constants.RDB_LOAD_ENC;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.util.ByteArray;

/**
 * @author Leon Chen
 * @since 3.5.3
 */
public class BaseRdbEncoderTest {
    
    @Test
    public void testRdbGenericSaveStringObject() throws IOException {
        {
            String s = "less than 20 bytes";
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.rdbGenericSaveStringObject(new ByteArray(s.getBytes()), out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(in));
            byte[] bytes = parser.rdbGenericLoadStringObject(RDB_LOAD_ENC).first();
            assertEquals(s, new String(bytes));
        }
        
        {
            String s = "use lz4 compress if length large than 20 bytes";
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.rdbGenericSaveStringObject(new ByteArray(s.getBytes()), out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(in));
            byte[] bytes = parser.rdbGenericLoadStringObject(RDB_LOAD_ENC).first();
            assertEquals(s, new String(bytes));
        }
    }
    
    @Test
    public void testLzf() throws IOException {
        for (int i = 0; i < 1000; i++) {
            int length = ThreadLocalRandom.current().nextInt(50000) + 20;
            byte[] value = new byte[length];
            ThreadLocalRandom.current().nextBytes(value);
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.rdbGenericSaveStringObject(new ByteArray(value), out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(in));
            byte[] bytes = parser.rdbGenericLoadStringObject(RDB_LOAD_ENC).first();
            assertArrayEquals(value, bytes);
        }
    }
    
    @Test
    public void testRdbSaveDoubleValue() throws IOException {
        {
            double value = 231231231231231d;
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.rdbSaveDoubleValue(value, out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(in));
            double value1 = parser.rdbLoadDoubleValue();
            assertEquals(value, value1, 0);
        }
        
        {
            double value = 2312312.31231231d;
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.rdbSaveDoubleValue(value, out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(in));
            double value1 = parser.rdbLoadDoubleValue();
            assertEquals(value, value1, 0.00000001d);
        }
        
        {
            double value = NEGATIVE_INFINITY;
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.rdbSaveDoubleValue(value, out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(in));
            double value1 = parser.rdbLoadDoubleValue();
            assertTrue(value1 == NEGATIVE_INFINITY);
        }
        
        {
            double value = POSITIVE_INFINITY;
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.rdbSaveDoubleValue(value, out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(in));
            double value1 = parser.rdbLoadDoubleValue();
            assertTrue(value1 == POSITIVE_INFINITY);
        }
        
        {
            double value = Double.NaN;
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.rdbSaveDoubleValue(value, out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(in));
            double value1 = parser.rdbLoadDoubleValue();
            assertTrue(Double.isNaN(value1));
        }
    }
    
    @Test
    public void testRdbSaveLen() throws IOException {
        long[] lens = new long[]{10, 256, 123123213L, 12309129310231231L};
        for (long len : lens) {
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.rdbSaveLen(len, out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(in));
            long len1 = parser.rdbLoadLen().len;
            assertEquals(len, len1);
        }
        
        for (long len : lens) {
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            byte[] bytes = encoder.rdbSaveLen(len);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            BaseRdbParser parser = new BaseRdbParser(new RedisInputStream(in));
            long len1 = parser.rdbLoadLen().len;
            assertEquals(len, len1);
        }
    }
}