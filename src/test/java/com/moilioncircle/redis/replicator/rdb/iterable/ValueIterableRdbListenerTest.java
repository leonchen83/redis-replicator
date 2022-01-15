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

package com.moilioncircle.redis.replicator.rdb.iterable;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueHash;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueList;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueSet;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueStream;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueZSet;

/**
 * @author Leon Chen
 */
public class ValueIterableRdbListenerTest {
    
    @Test
    public void test() {
        final AtomicInteger string = new AtomicInteger(0);
        final AtomicInteger map = new AtomicInteger(0);
        final AtomicInteger zset = new AtomicInteger(0);
        final AtomicInteger set = new AtomicInteger(0);
        final AtomicInteger list = new AtomicInteger(0);
        Replicator r = new RedisReplicator(ValueIterableRdbListenerTest.class.getClassLoader().getResourceAsStream("dump-huge-kv.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addEventListener(new ValueIterableEventListener(3, new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof BatchedKeyStringValueZSet) {
                    zset.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueString) {
                    string.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueList) {
                    list.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueSet) {
                    set.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueHash) {
                    map.incrementAndGet();
                }
            }
        }));
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }
        assertEquals(1, string.get());
        assertEquals(4, map.get());
        assertEquals(4, list.get());
        assertEquals(4, set.get());
        assertEquals(4, zset.get());
    }
    
    @Test
    public void test1() {
        final AtomicInteger string = new AtomicInteger(0);
        final AtomicInteger map = new AtomicInteger(0);
        final AtomicInteger zset = new AtomicInteger(0);
        final AtomicInteger set = new AtomicInteger(0);
        final AtomicInteger list = new AtomicInteger(0);
        Replicator r = new RedisReplicator(ValueIterableRdbListenerTest.class.getClassLoader().getResourceAsStream("dump-huge-kv.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addEventListener(new ValueIterableEventListener(2, new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof BatchedKeyStringValueZSet) {
                    zset.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueString) {
                    string.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueList) {
                    list.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueSet) {
                    set.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueHash) {
                    map.incrementAndGet();
                }
            }
        }));
        
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }
        assertEquals(1, string.get());
        assertEquals(5, map.get());
        assertEquals(5, list.get());
        assertEquals(5, set.get());
        assertEquals(5, zset.get());
    }
    
    @Test
    public void test2() {
        final AtomicInteger stream = new AtomicInteger(0);
        Replicator r = new RedisReplicator(ValueIterableRdbListenerTest.class.getClassLoader().getResourceAsStream("dump-stream.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addEventListener(new ValueIterableEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof BatchedKeyStringValueStream) {
                    stream.incrementAndGet();
                }
            }
        }));
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }
        assertEquals(5, stream.get());
    }
    
    @Test
    public void test3() {
        final AtomicInteger list = new AtomicInteger(0);
        Replicator r = new RedisReplicator(ValueIterableRdbListenerTest.class.getClassLoader().getResourceAsStream("dumpV10.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addEventListener(new ValueIterableEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof BatchedKeyStringValueList) {
                    list.incrementAndGet();
                }
            }
        }));
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }
        assertEquals(14, list.get());
    }
    
    @Test
    public void test4() {
        final AtomicInteger list = new AtomicInteger(0);
        Replicator r = new RedisReplicator(ValueIterableRdbListenerTest.class.getClassLoader().getResourceAsStream("dumpV10.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addEventListener(new ValueIterableEventListener(1024, new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof BatchedKeyStringValueList) {
                    list.incrementAndGet();
                }
            }
        }));
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }
        assertEquals(2, list.get());
    }
}