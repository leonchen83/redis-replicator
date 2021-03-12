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
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.util.ByteArray;

/**
 * @author Leon Chen
 * @since 3.6.0
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