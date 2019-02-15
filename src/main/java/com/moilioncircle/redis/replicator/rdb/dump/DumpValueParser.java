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

package com.moilioncircle.redis.replicator.rdb.dump;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.DefaultValueParser;
import com.moilioncircle.redis.replicator.rdb.ValueParser;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePairs;
import com.moilioncircle.redis.replicator.rdb.dump.datatype.DumpKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.iterable.IterableValueParser;
import com.moilioncircle.redis.replicator.rdb.iterable.ValueIterableEventListener;
import com.moilioncircle.redis.replicator.util.ByteArray;

import java.io.IOException;

import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_ZIPLIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_ZIPMAP;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST_QUICKLIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST_ZIPLIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_MODULE;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_MODULE_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET_INTSET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_STREAM_LISTPACKS;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_STRING;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_ZIPLIST;

/**
 * @author Leon Chen
 * @since 3.1.0
 */
public class DumpValueParser {

	protected final int batchSize;
	protected final boolean order;
	protected final Replicator replicator;
	protected final ValueParser valueParser;

	public DumpValueParser(Replicator replicator) {
		this(replicator, new DefaultValueParser(replicator));
	}

	public DumpValueParser(Replicator replicator, ValueParser valueParser) {
		this(64, replicator, valueParser);
	}

	public DumpValueParser(int batchSize, Replicator replicator, ValueParser valueParser) {
		this(true, batchSize, replicator, valueParser);
	}

	public DumpValueParser(boolean order, int batchSize, Replicator replicator, ValueParser valueParser) {
		if (batchSize <= 0) throw new IllegalArgumentException(String.valueOf(batchSize));
		this.order = order;
		this.batchSize = batchSize;
		this.replicator = replicator;
		this.valueParser = valueParser;
	}

	public void parse(DumpKeyValuePair kv, EventListener listener) throws IOException {
		if (valueParser instanceof IterableValueParser) {
			new ValueIterableEventListener(order, batchSize, listener).onEvent(replicator, parse(kv));
		} else {
			listener.onEvent(replicator, parse(kv));
		}
	}

	public KeyValuePair<?, ?> parse(DumpKeyValuePair kv) throws IOException {
		int valueType = kv.getValueRdbType();
		try (RedisInputStream in = new RedisInputStream(new ByteArray(kv.getValue()))) {
			if (valueParser instanceof IterableValueParser) {
				switch (valueType) {
					case RDB_TYPE_STRING:
						return KeyValuePairs.string(kv, valueParser.parseString(in, 0));
					case RDB_TYPE_LIST:
						return KeyValuePairs.iterList(kv, valueParser.parseList(in, 0));
					case RDB_TYPE_SET:
						return KeyValuePairs.iterSet(kv, valueParser.parseSet(in, 0));
					case RDB_TYPE_ZSET:
						return KeyValuePairs.iterZset(kv, valueParser.parseZSet(in, 0));
					case RDB_TYPE_ZSET_2:
						return KeyValuePairs.iterZset(kv, valueParser.parseZSet2(in, 0));
					case RDB_TYPE_HASH:
						return KeyValuePairs.iterHash(kv, valueParser.parseHash(in, 0));
					case RDB_TYPE_HASH_ZIPMAP:
						return KeyValuePairs.iterHash(kv, valueParser.parseHashZipMap(in, 0));
					case RDB_TYPE_LIST_ZIPLIST:
						return KeyValuePairs.iterList(kv, valueParser.parseListZipList(in, 0));
					case RDB_TYPE_SET_INTSET:
						return KeyValuePairs.iterSet(kv, valueParser.parseSetIntSet(in, 0));
					case RDB_TYPE_ZSET_ZIPLIST:
						return KeyValuePairs.iterZset(kv, valueParser.parseZSetZipList(in, 0));
					case RDB_TYPE_HASH_ZIPLIST:
						return KeyValuePairs.iterHash(kv, valueParser.parseHashZipList(in, 0));
					case RDB_TYPE_LIST_QUICKLIST:
						return KeyValuePairs.iterList(kv, valueParser.parseListQuickList(in, 0));
					case RDB_TYPE_MODULE:
						return KeyValuePairs.module(kv, valueParser.parseModule(in, 0));
					case RDB_TYPE_MODULE_2:
						return KeyValuePairs.module(kv, valueParser.parseModule2(in, 0));
					case RDB_TYPE_STREAM_LISTPACKS:
						return KeyValuePairs.stream(kv, valueParser.parseStreamListPacks(in, 0));
					default:
						throw new AssertionError("unexpected value type:" + valueType);
				}
			} else {
				switch (valueType) {
					case RDB_TYPE_STRING:
						return KeyValuePairs.string(kv, valueParser.parseString(in, 0));
					case RDB_TYPE_LIST:
						return KeyValuePairs.list(kv, valueParser.parseList(in, 0));
					case RDB_TYPE_SET:
						return KeyValuePairs.set(kv, valueParser.parseSet(in, 0));
					case RDB_TYPE_ZSET:
						return KeyValuePairs.zset(kv, valueParser.parseZSet(in, 0));
					case RDB_TYPE_ZSET_2:
						return KeyValuePairs.zset(kv, valueParser.parseZSet2(in, 0));
					case RDB_TYPE_HASH:
						return KeyValuePairs.hash(kv, valueParser.parseHash(in, 0));
					case RDB_TYPE_HASH_ZIPMAP:
						return KeyValuePairs.hash(kv, valueParser.parseHashZipMap(in, 0));
					case RDB_TYPE_LIST_ZIPLIST:
						return KeyValuePairs.list(kv, valueParser.parseListZipList(in, 0));
					case RDB_TYPE_SET_INTSET:
						return KeyValuePairs.set(kv, valueParser.parseSetIntSet(in, 0));
					case RDB_TYPE_ZSET_ZIPLIST:
						return KeyValuePairs.zset(kv, valueParser.parseZSetZipList(in, 0));
					case RDB_TYPE_HASH_ZIPLIST:
						return KeyValuePairs.hash(kv, valueParser.parseHashZipList(in, 0));
					case RDB_TYPE_LIST_QUICKLIST:
						return KeyValuePairs.list(kv, valueParser.parseListQuickList(in, 0));
					case RDB_TYPE_MODULE:
						return KeyValuePairs.module(kv, valueParser.parseModule(in, 0));
					case RDB_TYPE_MODULE_2:
						return KeyValuePairs.module(kv, valueParser.parseModule2(in, 0));
					case RDB_TYPE_STREAM_LISTPACKS:
						return KeyValuePairs.stream(kv, valueParser.parseStreamListPacks(in, 0));
					default:
						throw new AssertionError("unexpected value type:" + valueType);
				}
			}
		}
	}
}
