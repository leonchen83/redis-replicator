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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

/**
 * @author Leon Chen
 */
public class ListPackBugTest {
	@Test
	public void test() throws IOException {
		@SuppressWarnings("resource")
		Replicator replicator = new RedisReplicator(FunctionTest.class.getClassLoader().getResourceAsStream("listpack-bug.rdb"), FileType.RDB,
				Configuration.defaultSetting());
		List<KeyValuePair<?, ?>> kvs = new ArrayList<>();
		replicator.addEventListener(new EventListener() {
			@Override
			public void onEvent(Replicator replicator, Event event) {
				if (event instanceof KeyValuePair<?, ?>) {
					kvs.add((KeyValuePair<?, ?>) event);
				}
			}
		});
		replicator.open();
		
		assertEquals(237, kvs.size());
	}
}
