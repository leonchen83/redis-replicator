/*
 * Copyright 2016-2019 Leon Chen
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

package com.moilioncircle.redis.replicator.offline;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.event.AbstractEvent;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostCommandSyncEvent;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.event.PreCommandSyncEvent;
import com.moilioncircle.redis.replicator.event.PreRdbSyncEvent;
import com.moilioncircle.redis.replicator.io.RateLimitInputStream;
import com.moilioncircle.redis.replicator.online.RedisSocketReplicatorTest;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.util.type.Tuple2;

/**
 * @author Leon Chen
 * @since 3.3.0
 */
public class OffsetTest {
    
    @Test
    public void test() throws IOException {
        Replicator r = new RedisReplicator(OffsetTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"), FileType.AOF, Configuration.defaultSetting());
        r.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                AbstractEvent ae = (AbstractEvent) event;
                Tuple2<Long, Long> offset = ae.getContext().getOffsets();
                if (ae instanceof PreCommandSyncEvent) {
                    assertEquals(0L, offset.getV1().longValue());
                    assertEquals(0L, offset.getV2().longValue());
                }
                if (ae instanceof SetCommand) {
                    assertEquals(23L, offset.getV1().longValue());
                    assertEquals(50L, offset.getV2().longValue());
                }
                if (ae instanceof PostCommandSyncEvent) {
                    assertEquals(135L, offset.getV1().longValue());
                    assertEquals(135L, offset.getV2().longValue());
                }
            }
        });
        r.open();
    }

    @Test
    public void test1() throws IOException {
        Replicator redisReplicator = new RedisReplicator(
                new RateLimitInputStream(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb")), FileType.RDB,
                Configuration.defaultSetting());
        redisReplicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                AbstractEvent ae = (AbstractEvent) event;
                Tuple2<Long, Long> offset = ae.getContext().getOffsets();
                
                if (ae instanceof KeyValuePair<?, ?>) {
                    KeyValuePair<?, ?> kv = (KeyValuePair<?, ?>)ae;
                    String key = new String((byte[])kv.getKey());
                    if (key.equals("hll3")) {
                        assertEquals(72L, offset.getV1().longValue());
                        assertEquals(256L, offset.getV2().longValue());
                    } else if (key.equals("aaa")) {
                        assertEquals(611L, offset.getV1().longValue());
                        assertEquals(620L, offset.getV2().longValue());
                    }
                }
                if (ae instanceof PreRdbSyncEvent) {
                    assertEquals(0L, offset.getV1().longValue());
                    assertEquals(0L, offset.getV2().longValue());
                }
                if (ae instanceof PostRdbSyncEvent) {
                    assertEquals(745L, offset.getV1().longValue());
                    assertEquals(754L, offset.getV2().longValue());
                }
            }
        });
        redisReplicator.open();
    }
}
