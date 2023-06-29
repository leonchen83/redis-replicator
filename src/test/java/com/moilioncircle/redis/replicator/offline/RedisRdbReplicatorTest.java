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

package com.moilioncircle.redis.replicator.offline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.moilioncircle.redis.replicator.CloseListener;
import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.Constants;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.rdb.datatype.AuxField;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueSet;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@SuppressWarnings("resource")
public class RedisRdbReplicatorTest {
    @Test
    public void testChecksumV7() throws IOException {
        Replicator redisReplicator = new RedisReplicator(
                RedisRdbReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicLong atomicChecksum = new AtomicLong(0);
        redisReplicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair<?, ?>) {
                    acc.incrementAndGet();
                }
                if (event instanceof PostRdbSyncEvent) {
                    atomicChecksum.compareAndSet(0, ((PostRdbSyncEvent) event).getChecksum());
                }
            }
        });
        redisReplicator.open();
        assertEquals(19, acc.get());
        assertEquals(6576517133597126869L, atomicChecksum.get());
    }
    
    @Test
    public void testChecksumV6() throws IOException {
        Replicator redisReplicator = new RedisReplicator(
                RedisRdbReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV6.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicLong atomicChecksum = new AtomicLong(0);
        redisReplicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair<?, ?>) {
                    acc.incrementAndGet();
                }
                if (event instanceof PostRdbSyncEvent) {
                    atomicChecksum.compareAndSet(0, ((PostRdbSyncEvent) event).getChecksum());
                }
            }
        });
        redisReplicator.open();
        assertEquals(132, acc.get());
        assertEquals(-3409494954737929802L, atomicChecksum.get());
    }
    
    @Test
    public void testCloseListener1() throws IOException {
        final AtomicInteger acc = new AtomicInteger(0);
        Replicator replicator = new RedisReplicator(
                RedisRdbReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV6.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                acc.incrementAndGet();
            }
        });
        replicator.open();
        assertEquals(1, acc.get());
    }
    
    @Test
    public void testFileV7() throws IOException {
        Replicator redisReplicator = new RedisReplicator(
                RedisRdbReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        final List<KeyValuePair<?, ?>> list = new ArrayList<>();
        redisReplicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair<?, ?>) {
                    list.add((KeyValuePair<?, ?>) event);
                    acc.incrementAndGet();
                }
            }
        });
        redisReplicator.open();
        assertEquals(19, acc.get());
        for (KeyValuePair<?, ?> kv : list) {
            if (Strings.toString(kv.getKey()).equals("abcd")) {
                assertEquals("abcd", Strings.toString(kv.getValue()));
            }
            if (Strings.toString(kv.getKey()).equals("foo")) {
                assertEquals("bar", Strings.toString(kv.getValue()));
            }
            if (Strings.toString(kv.getKey()).equals("aaa")) {
                assertEquals("bbb", Strings.toString(kv.getValue()));
            }
        }
    }
    
    @Test
    public void testFilter() throws IOException {
        Replicator redisReplicator = new RedisReplicator(
                RedisRdbReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        redisReplicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair<?, ?>) {
                    if (((KeyValuePair<?, ?>) event).getValueRdbType() == 0) {
                        acc.incrementAndGet();
                    }
                }
            }
        });
        redisReplicator.open();
        assertEquals(13, acc.get());
    }
    
    @Test
    public void testFileV6() throws IOException {
        Replicator redisReplicator = new RedisReplicator(
                RedisRdbReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV6.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        redisReplicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair<?, ?>) {
                    acc.incrementAndGet();
                }
            }
        });
        redisReplicator.open();
        assertEquals(132, acc.get());
    }
    
    @Test
    public void testFileV8() throws IOException {
        Replicator redisReplicator = new RedisReplicator(
                RedisRdbReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV8.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicInteger acc1 = new AtomicInteger(0);
        redisReplicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof AuxField) {
                    acc1.incrementAndGet();
                }
                if (event instanceof KeyValuePair<?, ?>) {
                    acc.incrementAndGet();
                }
            }
        });
        redisReplicator.open();
        assertEquals(92499, acc.get());
        assertEquals(7, acc1.get());
    }
    
    @Test
    public void testFileV11() throws IOException {
        Replicator redisReplicator = new RedisReplicator(
                RedisRdbReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV11.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        List<byte[]> v = new ArrayList<>();
        redisReplicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueSet) {
                    KeyStringValueSet set = (KeyStringValueSet) event;
                    assertTrue(set.getValueRdbType() == Constants.RDB_TYPE_SET_LISTPACK);
                    Set<byte[]> value = set.getValue();
                    v.addAll(value);
                }
            }
        });
        redisReplicator.open();
        assertEquals(4, v.size());
    }
    
}