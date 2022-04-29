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

package com.moilioncircle.redis.replicator.rdb.skip;

import static com.moilioncircle.redis.replicator.Constants.MODULE_SET;

import java.io.IOException;
import java.util.NoSuchElementException;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.DefaultRdbValueVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;

/**
 * @author Baoyi Chen
 * @since 3.6.3
 */
public class SkipRdbValueVisitor extends DefaultRdbValueVisitor {
	
	public SkipRdbValueVisitor(Replicator replicator) {
		super(replicator);
	}
	
	@Override
	public <T> T applyFunction(RedisInputStream in, int version) throws IOException {
		SkipRdbParser parser = new SkipRdbParser(in);
		parser.rdbGenericLoadStringObject(); // name
		parser.rdbGenericLoadStringObject(); // engine name
		long hasDesc = parser.rdbLoadLen().len;
		if (hasDesc == 1) {
			parser.rdbGenericLoadStringObject(); // description
		}
		parser.rdbGenericLoadStringObject(); // code
		return null;
	}
	
	@Override
	public <T> T applyFunction2(RedisInputStream in, int version) throws IOException {
		SkipRdbParser parser = new SkipRdbParser(in);
		parser.rdbGenericLoadStringObject(); // code
		return null;
	}
	
	@Override
	public <T> T applyString(RedisInputStream in, int version) throws IOException {
		SkipRdbParser parser = new SkipRdbParser(in);
		parser.rdbLoadEncodedStringObject();
		return null;
	}
	
	@Override
	public <T> T applyList(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		long len = skip.rdbLoadLen().len;
		while (len > 0) {
			skip.rdbLoadEncodedStringObject();
			len--;
		}
		return null;
	}
	
	@Override
	public <T> T applySet(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		long len = skip.rdbLoadLen().len;
		while (len > 0) {
			skip.rdbLoadEncodedStringObject();
			len--;
		}
		return null;
	}
	
	@Override
	public <T> T applyZSet(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		long len = skip.rdbLoadLen().len;
		while (len > 0) {
			skip.rdbLoadEncodedStringObject();
			skip.rdbLoadDoubleValue();
			len--;
		}
		return null;
	}
	
	@Override
	public <T> T applyZSet2(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		long len = skip.rdbLoadLen().len;
		while (len > 0) {
			skip.rdbLoadEncodedStringObject();
			skip.rdbLoadBinaryDoubleValue();
			len--;
		}
		return null;
	}
	
	@Override
	public <T> T applyHash(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		long len = skip.rdbLoadLen().len;
		while (len > 0) {
			skip.rdbLoadEncodedStringObject();
			skip.rdbLoadEncodedStringObject();
			len--;
		}
		return null;
	}
	
	@Override
	public <T> T applyHashZipMap(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		skip.rdbLoadPlainStringObject();
		return null;
	}
	
	@Override
	public <T> T applyListZipList(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		skip.rdbLoadPlainStringObject();
		return null;
	}
	
	@Override
	public <T> T applySetIntSet(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		skip.rdbLoadPlainStringObject();
		return null;
	}
	
	@Override
	public <T> T applyZSetZipList(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		skip.rdbLoadPlainStringObject();
		return null;
	}
	
	@Override
	public <T> T applyZSetListPack(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		skip.rdbLoadPlainStringObject();
		return null;
	}
	
	@Override
	public <T> T applyHashZipList(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		skip.rdbLoadPlainStringObject();
		return null;
	}
	
	@Override
	public <T> T applyHashListPack(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		skip.rdbLoadPlainStringObject();
		return null;
	}
	
	@Override
	public <T> T applyListQuickList(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		long len = skip.rdbLoadLen().len;
		for (long i = 0; i < len; i++) {
			skip.rdbGenericLoadStringObject();
		}
		return null;
	}
	
	@Override
	public <T> T applyListQuickList2(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		long len = skip.rdbLoadLen().len;
		for (long i = 0; i < len; i++) {
			skip.rdbLoadLen();
			skip.rdbGenericLoadStringObject();
		}
		return null;
	}
	
	@Override
	public <T> T applyModule(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		char[] c = new char[9];
		long moduleid = skip.rdbLoadLen().len;
		for (int i = 0; i < c.length; i++) {
			c[i] = MODULE_SET[(int) (moduleid >>> (10 + (c.length - 1 - i) * 6) & 63)];
		}
		String moduleName = new String(c);
		int moduleVersion = (int) (moduleid & 1023);
		ModuleParser<? extends Module> moduleParser = replicator.getModuleParser(moduleName, moduleVersion);
		if (moduleParser == null) {
			throw new NoSuchElementException("module parser[" + moduleName + ", " + moduleVersion + "] not register. rdb type: [RDB_TYPE_MODULE]");
		}
		moduleParser.parse(in, 1);
		return null;
	}
	
	@Override
	public <T> T applyModule2(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		skip.rdbLoadLen();
		SkipRdbParser skipRdbParser = new SkipRdbParser(in);
		skipRdbParser.rdbLoadCheckModuleValue();
		return null;
	}
	
	@Override
	public <T> T applyStreamListPacks(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		long listPacks = skip.rdbLoadLen().len;
		while (listPacks-- > 0) {
			skip.rdbLoadPlainStringObject();
			skip.rdbLoadPlainStringObject();
		}
		skip.rdbLoadLen();
		skip.rdbLoadLen();
		skip.rdbLoadLen();
		long groupCount = skip.rdbLoadLen().len;
		while (groupCount-- > 0) {
			skip.rdbLoadPlainStringObject();
			skip.rdbLoadLen();
			skip.rdbLoadLen();
			long groupPel = skip.rdbLoadLen().len;
			while (groupPel-- > 0) {
				in.skip(16);
				skip.rdbLoadMillisecondTime();
				skip.rdbLoadLen();
			}
			long consumerCount = skip.rdbLoadLen().len;
			while (consumerCount-- > 0) {
				skip.rdbLoadPlainStringObject();
				skip.rdbLoadMillisecondTime();
				long consumerPel = skip.rdbLoadLen().len;
				while (consumerPel-- > 0) {
					in.skip(16);
				}
			}
		}
		return null;
	}
	
	@Override
	public <T> T applyStreamListPacks2(RedisInputStream in, int version) throws IOException {
		SkipRdbParser skip = new SkipRdbParser(in);
		long listPacks = skip.rdbLoadLen().len;
		while (listPacks-- > 0) {
			skip.rdbLoadPlainStringObject();
			skip.rdbLoadPlainStringObject();
		}
		skip.rdbLoadLen();
		skip.rdbLoadLen();
		skip.rdbLoadLen();
		skip.rdbLoadLen();
		skip.rdbLoadLen();
		skip.rdbLoadLen();
		skip.rdbLoadLen();
		skip.rdbLoadLen();
		long groupCount = skip.rdbLoadLen().len;
		while (groupCount-- > 0) {
			skip.rdbLoadPlainStringObject();
			skip.rdbLoadLen();
			skip.rdbLoadLen();
			skip.rdbLoadLen();
			long groupPel = skip.rdbLoadLen().len;
			while (groupPel-- > 0) {
				in.skip(16);
				skip.rdbLoadMillisecondTime();
				skip.rdbLoadLen();
			}
			long consumerCount = skip.rdbLoadLen().len;
			while (consumerCount-- > 0) {
				skip.rdbLoadPlainStringObject();
				skip.rdbLoadMillisecondTime();
				long consumerPel = skip.rdbLoadLen().len;
				while (consumerPel-- > 0) {
					in.skip(16);
				}
			}
		}
		return null;
	}
}
