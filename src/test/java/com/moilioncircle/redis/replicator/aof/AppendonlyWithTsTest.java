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

package com.moilioncircle.redis.replicator.aof;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.TimestampEvent;
import com.moilioncircle.redis.replicator.cmd.TimestampEventListener;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;

/**
 * @author Leon Chen
 * @since 3.6.0
 */
public class AppendonlyWithTsTest {
    @Test
    public void test() throws IOException {
        Replicator replicator = new RedisReplicator(AppendonlyWithTsTest.class.getClassLoader().getResourceAsStream("appendonly-with-ts.aof"), FileType.AOF, Configuration.defaultSetting());
        AtomicInteger acc0 = new AtomicInteger();
        AtomicInteger acc1 = new AtomicInteger();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof TimestampEvent) {
                    acc0.incrementAndGet();
                } else if (event instanceof Command) {
                    acc1.incrementAndGet();
                }
            }
        });
        replicator.open();
        assertEquals(54, acc0.get());
        assertEquals(65, acc1.get());
    }
    
    @Test
    public void test1() throws IOException {
        AtomicInteger acc1 = new AtomicInteger();
        Replicator replicator = new RedisReplicator(AppendonlyWithTsTest.class.getClassLoader().getResourceAsStream("appendonly-with-ts.aof"), FileType.AOF, Configuration.defaultSetting());
        replicator.addEventListener(new TimestampEventListener(ts -> ts >= 1643689192000L && ts <= 1643691222000L, new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof Command) {
                    acc1.incrementAndGet();
                }
            }
        }));
        replicator.open();
        assertEquals(43, acc1.get());
    }
}
