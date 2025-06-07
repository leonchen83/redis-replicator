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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

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
import com.moilioncircle.redis.replicator.rdb.datatype.Slot;

/**
 * @author Leon Chen
 * @since 3.9.0k
 */
public class ClusterSlotTest {
	
	@Test
	public void test() {
		Replicator r = new RedisReplicator(ClusterSlotTest.class.getClassLoader().getResourceAsStream("dump-slot.rdb"), FileType.RDB, Configuration.defaultSetting());
		List<Slot> list = new ArrayList<>();
		r.addEventListener(new EventListener() {
			@Override
			public void onEvent(Replicator replicator, Event event) {
				if (event instanceof KeyValuePair<?, ?>) {
					KeyValuePair<?, ?> kv = (KeyValuePair<?, ?>) event;
					list.add(kv.getSlot());
				}
			}
		});
		try {
			r.open();
		} catch (IOException e) {
			fail();
		}
		
		assertEquals(5, list.size());
		assertEquals(98, list.get(0).getSlotId());
		assertEquals(1, list.get(0).getSlotSize());
		assertEquals(0, list.get(0).getExpiresSlotSize());
		
		assertEquals(230, list.get(1).getSlotId());
		assertEquals(1, list.get(1).getSlotSize());
		assertEquals(0, list.get(1).getExpiresSlotSize());
		
		assertEquals(4163, list.get(2).getSlotId());
		assertEquals(1, list.get(2).getSlotSize());
		assertEquals(0, list.get(2).getExpiresSlotSize());
		
		assertEquals(4772, list.get(3).getSlotId());
		assertEquals(2, list.get(3).getSlotSize());
		assertEquals(0, list.get(3).getExpiresSlotSize());
		
		assertEquals(4772, list.get(4).getSlotId());
		assertEquals(2, list.get(4).getSlotSize());
		assertEquals(0, list.get(4).getExpiresSlotSize());
	}
}
