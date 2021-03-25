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

import static com.moilioncircle.redis.replicator.Status.DISCONNECTED;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.moilioncircle.redis.replicator.CloseListener;
import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostCommandSyncEvent;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.event.PreCommandSyncEvent;
import com.moilioncircle.redis.replicator.io.RateLimitInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

/**
 * @author Leon Chen
 * @since 2.4.1
 */
public class CloseTest {
    @Test
    @SuppressWarnings("resource")
    public void testRdbClose() throws IOException {
        Replicator r = new RedisReplicator(
                new RateLimitInputStream(CloseTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb")),
                FileType.RDB, Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        r.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair<?, ?>) {
                    acc.incrementAndGet();
                    if (acc.get() == 10) {
                        try {
                            replicator.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        r.open();
        assertEquals(10, acc.get());
    }
    
    @Test
    @SuppressWarnings("resource")
    public void testAofClose() throws IOException {
        Replicator r = new RedisReplicator(CloseTest.class.getClassLoader().getResourceAsStream("appendonly5.aof"),
                FileType.AOF, Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        r.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof Command) {
                    acc.incrementAndGet();
                    if (acc.get() == 30) {
                        try {
                            replicator.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        r.open();
        assertEquals(30, acc.get());
    }
    
    @Test
    @SuppressWarnings("resource")
    public void testMixClose1() throws IOException {
        Replicator replicator = new RedisReplicator(
                CloseTest.class.getClassLoader().getResourceAsStream("appendonly4.aof"), FileType.MIXED,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicInteger acc1 = new AtomicInteger(0);
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair<?, ?>) {
                    acc.incrementAndGet();
                    if (acc.get() == 100) {
                        try {
                            replicator.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (event instanceof Command) {
                    acc1.incrementAndGet();
                }
            }
        });
        replicator.open();
        assertEquals(100, acc.get());
        assertEquals(0, acc1.get());
    }
    
    @Test
    @SuppressWarnings("resource")
    public void testMixClose2() throws IOException {
        Replicator replicator = new RedisReplicator(
                CloseTest.class.getClassLoader().getResourceAsStream("appendonly4.aof"), FileType.MIXED,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicInteger acc1 = new AtomicInteger(0);
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair<?, ?>) {
                    acc.incrementAndGet();
                }
                if (event instanceof Command) {
                    acc1.incrementAndGet();
                    if (acc1.get() == 100) {
                        try {
                            replicator.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        
        replicator.open();
        assertEquals(244653, acc.get());
        assertEquals(100, acc1.get());
    }
    
    @Test
    public void testMixClose3() throws IOException, InterruptedException {
        final Replicator replicator = new RedisReplicator(
                CloseTest.class.getClassLoader().getResourceAsStream("appendonly4.aof"), FileType.MIXED,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        new Thread() {
            @Override
            public void run() {
                replicator.addCloseListener(new CloseListener() {
                    @Override
                    public void handle(Replicator replicator) {
                        acc.incrementAndGet();
                    }
                });
                try {
                    replicator.open();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    
        Thread.sleep(2000);
        replicator.close();
        Thread.sleep(2000);
        assertEquals(1, acc.get());
        assertEquals(DISCONNECTED, replicator.getStatus());
    }
    
    @Test
    public void testMixClose4() throws IOException, InterruptedException {
        final Replicator replicator = new RedisReplicator(
                CloseTest.class.getClassLoader().getResourceAsStream("appendonly4.aof"), FileType.MIXED,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        new Thread() {
            @Override
            public void run() {
                replicator.addEventListener(new EventListener() {
                    @Override
                    public void onEvent(Replicator replicator, Event event) {
                        if (event instanceof KeyValuePair<?, ?>) {
                            if (replicator.getStatus() == DISCONNECTED) {
                                acc.incrementAndGet();
                            }
                        }
                    }
                });
                try {
                    replicator.open();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    
        Thread.sleep(2000);
        replicator.close();
        Thread.sleep(2000);
        assertEquals(0, acc.get());
        assertEquals(DISCONNECTED, replicator.getStatus());
    }
    
    @Test
    public void testMixClose5() throws IOException {
        @SuppressWarnings("resource") final Replicator replicator = new RedisReplicator(
                CloseTest.class.getClassLoader().getResourceAsStream("appendonly4.aof"), FileType.MIXED,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PreCommandSyncEvent) {
                    acc.incrementAndGet();
                }
                if (event instanceof PostCommandSyncEvent) {
                    acc.incrementAndGet();
                }
                if (event instanceof PostRdbSyncEvent) {
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.open();
        assertEquals(0, acc.get());
        assertEquals(DISCONNECTED, replicator.getStatus());
    }
    
    @SuppressWarnings("resource")
    @Test
    public void testMixClose6() throws IOException {
        final Replicator replicator = new RedisReplicator(
                CloseTest.class.getClassLoader().getResourceAsStream("appendonly4.aof"), FileType.MIXED,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PreCommandSyncEvent) {
                    acc.incrementAndGet();
                }
                if (event instanceof PostCommandSyncEvent) {
                    acc.incrementAndGet();
                }
            }
        });
        replicator.open();
        assertEquals(2, acc.get());
        assertEquals(DISCONNECTED, replicator.getStatus());
    }
    
    @SuppressWarnings("resource")
    @Test
    public void testMixClose7() throws IOException {
        final Replicator replicator = new RedisReplicator(
                CloseTest.class.getClassLoader().getResourceAsStream("appendonly4.aof"), FileType.MIXED,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PreCommandSyncEvent) {
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
                if (event instanceof PostCommandSyncEvent) {
                    acc.incrementAndGet();
                }
            }
        });
        replicator.open();
        assertEquals(0, acc.get());
        assertEquals(DISCONNECTED, replicator.getStatus());
    }
    
    @SuppressWarnings("resource")
    @Test
    public void testMixClose11() throws IOException {
        final Replicator replicator = new RedisReplicator(
                CloseTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"), FileType.AOF,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PreCommandSyncEvent) {
                    acc.incrementAndGet();
                }
                if (event instanceof PostCommandSyncEvent) {
                    acc.incrementAndGet();
                }
            }
        });
        replicator.open();
        assertEquals(2, acc.get());
        assertEquals(DISCONNECTED, replicator.getStatus());
    }
    
    @Test
    public void testMixClose12() throws IOException {
        @SuppressWarnings("resource") final Replicator replicator = new RedisReplicator(
                CloseTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"), FileType.AOF,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PreCommandSyncEvent) {
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
                if (event instanceof PostCommandSyncEvent) {
                    acc.incrementAndGet();
                }
            }
        });
        replicator.open();
        assertEquals(0, acc.get());
        assertEquals(DISCONNECTED, replicator.getStatus());
    }
}
