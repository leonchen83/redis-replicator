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

import com.moilioncircle.redis.replicator.io.RedisInputStream;

import java.io.IOException;

/**
 * @author Leon Chen
 * @since 3.1.0
 */
public abstract class ValueParser {

	public <T> T parseString(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseList(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseSet(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseZSet(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseZSet2(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseHash(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseHashZipMap(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseListZipList(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseSetIntSet(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseZSetZipList(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseHashZipList(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseListQuickList(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseModule(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseModule2(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}

	public <T> T parseStreamListPacks(RedisInputStream in, int version) throws IOException {
		throw new UnsupportedOperationException("must implement this method.");
	}
}
