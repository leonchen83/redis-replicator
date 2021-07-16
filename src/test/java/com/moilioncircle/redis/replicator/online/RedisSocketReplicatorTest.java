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

package com.moilioncircle.redis.replicator.online;

import static com.moilioncircle.redis.replicator.Status.DISCONNECTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.moilioncircle.redis.replicator.CloseListener;
import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.RedisSocketReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.impl.AggregateType;
import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.PingCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ZAddCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ZInterStoreCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ZUnionStoreCommand;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostCommandSyncEvent;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.event.PreCommandSyncEvent;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.util.Concurrents;
import com.moilioncircle.redis.replicator.util.Strings;
import com.moilioncircle.redis.replicator.util.XScheduledExecutorService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.ZAddParams;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@SuppressWarnings("resource")
public class RedisSocketReplicatorTest {

    @Test
    public void testSet() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting().setRetries(0));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    try (Jedis jedis = new Jedis("127.0.0.1", 6379)) {
                        jedis.del("abc");
                        jedis.set("abc", "bcd");
                    }
                }
                if (event instanceof SetCommand) {
                    SetCommand setCommand = (SetCommand) event;
                    assertEquals("abc", Strings.toString(setCommand.getKey()));
                    assertEquals("bcd", Strings.toString(setCommand.getValue()));
                    ref.compareAndSet(null, "ok");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.open();
        assertEquals("ok", ref.get());
    }

    @Test
    public void testZInterStore() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        final Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting().setRetries(0));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    try (Jedis jedis = new Jedis("127.0.0.1", 6379)) {
                        jedis.del("zset1");
                        jedis.del("zset2");
                        jedis.del("out");
                        jedis.zadd("zset1", 1, "one");
                        jedis.zadd("zset1", 2, "two");
                        jedis.zadd("zset2", 1, "one");
                        jedis.zadd("zset2", 2, "two");
                        jedis.zadd("zset2", 3, "three");
                        //ZINTERSTORE out 2 zset1 zset2 WEIGHTS 2 3
                        ZParams zParams = new ZParams();
                        zParams.weights(2, 3);
                        zParams.aggregate(ZParams.Aggregate.MIN);
                        jedis.zinterstore("out", zParams, "zset1", "zset2");
                    }
                }
                if (event instanceof ZInterStoreCommand) {
                    ZInterStoreCommand zInterStoreCommand = (ZInterStoreCommand) event;
                    assertEquals("out", Strings.toString(zInterStoreCommand.getDestination()));
                    assertEquals(2, zInterStoreCommand.getNumkeys());
                    assertEquals("zset1", Strings.toString(zInterStoreCommand.getKeys()[0]));
                    assertEquals("zset2", Strings.toString(zInterStoreCommand.getKeys()[1]));
                    assertEquals(2.0, zInterStoreCommand.getWeights()[0], 0.0001);
                    assertEquals(3.0, zInterStoreCommand.getWeights()[1], 0.0001);
                    assertEquals(AggregateType.MIN, zInterStoreCommand.getAggregateType());
                    ref.compareAndSet(null, "ok");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.open();
        assertEquals("ok", ref.get());
    }

    @Test
    public void testZUnionStore() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        final Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting().setRetries(0));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    try (Jedis jedis = new Jedis("127.0.0.1", 6379)) {
                        jedis.del("zset3");
                        jedis.del("zset4");
                        jedis.del("out1");
                        jedis.zadd("zset3", 1, "one");
                        jedis.zadd("zset3", 2, "two");
                        jedis.zadd("zset4", 1, "one");
                        jedis.zadd("zset4", 2, "two");
                        jedis.zadd("zset4", 3, "three");
                        //ZINTERSTORE out 2 zset1 zset2 WEIGHTS 2 3
                        ZParams zParams = new ZParams();
                        zParams.weights(2, 3);
                        zParams.aggregate(ZParams.Aggregate.SUM);
                        jedis.zunionstore("out1", zParams, "zset3", "zset4");
                    }
                }
                if (event instanceof ZUnionStoreCommand) {
                    ZUnionStoreCommand zInterStoreCommand = (ZUnionStoreCommand) event;
                    assertEquals("out1", Strings.toString(zInterStoreCommand.getDestination()));
                    assertEquals(2, zInterStoreCommand.getNumkeys());
                    assertEquals("zset3", Strings.toString(zInterStoreCommand.getKeys()[0]));
                    assertEquals("zset4", Strings.toString(zInterStoreCommand.getKeys()[1]));
                    assertEquals(2.0, zInterStoreCommand.getWeights()[0], 0.0001);
                    assertEquals(3.0, zInterStoreCommand.getWeights()[1], 0.0001);
                    assertEquals(AggregateType.SUM, zInterStoreCommand.getAggregateType());
                    ref.compareAndSet(null, "ok");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.open();
        assertEquals("ok", ref.get());
    }

    @Test
    public void testCloseListener() {
        final AtomicInteger acc = new AtomicInteger(0);
        Replicator replicator = new RedisReplicator("127.0.0.1", 6666, Configuration.defaultSetting().setUseDefaultExceptionListener(false));
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                acc.incrementAndGet();
            }
        });
        try {
            replicator.open();
            fail();
        } catch (IOException e) {
        }

        assertEquals(1, acc.get());
    }

    @Test
    public void testZAdd() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        final Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting().setRetries(0));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    try (Jedis jedis = new Jedis("127.0.0.1", 6379)) {
                        jedis.del("abc");
                        jedis.zrem("zzlist", "member");
                        jedis.set("abc", "bcd");
                        jedis.zadd("zzlist", 1.5, "member", ZAddParams.zAddParams().nx());
                    }
                }
                if (event instanceof SetCommand) {
                    SetCommand setCommand = (SetCommand) event;
                    assertEquals("abc", Strings.toString(setCommand.getKey()));
                    assertEquals("bcd", Strings.toString(setCommand.getValue()));
                    ref.compareAndSet(null, "1");
                } else if (event instanceof ZAddCommand) {
                    ZAddCommand zaddCommand = (ZAddCommand) event;
                    assertEquals("zzlist", Strings.toString(zaddCommand.getKey()));
                    assertEquals(1.5, zaddCommand.getZSetEntries()[0].getScore(), 0.0001);
                    assertEquals("member", Strings.toString(zaddCommand.getZSetEntries()[0].getElement()));
                    assertEquals(ExistType.NX, zaddCommand.getExistType());
                    ref.compareAndSet("1", "2");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.open();
        assertEquals("2", ref.get());
    }

    @Test
    public void testV7() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        final Replicator replicator = new RedisReplicator("127.0.0.1", 6380, Configuration.defaultSetting().setAuthPassword("test").setRetries(0));

        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    try (Jedis jedis = new Jedis("127.0.0.1", 6380)) {
                        jedis.auth("test");
                        jedis.del("abc");
                        jedis.set("abc", "bcd");
                    }
                }
                if (event instanceof SetCommand) {
                    SetCommand setCommand = (SetCommand) event;
                    assertEquals("abc", Strings.toString(setCommand.getKey()));
                    assertEquals("bcd", Strings.toString(setCommand.getValue()));
                    ref.compareAndSet(null, "ok");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.open();
        assertEquals("ok", ref.get());
    }

    @Test
    public void testExpireV6() throws Exception {
        try (Jedis jedis = new Jedis("127.0.0.1", 6379)) {
            jedis.del("abc");
            jedis.del("bbb");
            jedis.set("abc", "bcd");
            jedis.expire("abc", 500L);
            jedis.set("bbb", "bcd");
            jedis.expireAt("bbb", System.currentTimeMillis() + 1000000);
        }

        Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting().setRetries(0));
        final List<KeyValuePair<?, ?>> list = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair) {
                    list.add((KeyValuePair<?, ?>) event);
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
        for (KeyValuePair<?, ?> kv : list) {
            if (Strings.toString(kv.getKey()).equals("abc")) {
                assertNotNull(kv.getExpiredMs());
            } else if (Strings.toString(kv.getKey()).equals("bbb")) {
                assertNotNull(kv.getExpiredMs());
            }
        }
    }

    @Test
    public void testCount() throws IOException {
        try (Jedis jedis = new Jedis("127.0.0.1", 6379)) {
            Pipeline pipeline = jedis.pipelined();
            for (int i = 0; i < 8000; i++) {
                pipeline.del("test_" + i);
                pipeline.set("test_" + i, "value_" + i);
            }
            pipeline.sync();
        }

        Replicator redisReplicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        redisReplicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair) {
                    KeyValuePair<?, ?> kv = (KeyValuePair<?, ?>) event;
                    if (Strings.toString(kv.getKey()).startsWith("test_")) {
                        acc.incrementAndGet();
                    }
                }
                if (event instanceof PostRdbSyncEvent) {
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        redisReplicator.open();
        assertEquals(8000, acc.get());
    }
    
    @Test
    public void testExecutor1() throws IOException {
        Configuration configuration = Configuration.defaultSetting();
        configuration.setScheduledExecutor(Executors.newScheduledThreadPool(4));
        RedisSocketReplicator replicator = new RedisSocketReplicator("127.0.0.1", 6379, configuration);
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PingCommand) {
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.open();
        assertFalse(configuration.getScheduledExecutor().isShutdown());
        assertFalse(configuration.getScheduledExecutor().isTerminated());
        Concurrents.terminateQuietly(configuration.getScheduledExecutor(), 30, TimeUnit.SECONDS);
    }
    
    @Test
    public void testExecutor2() throws Exception {
        RedisSocketReplicator replicator = new RedisSocketReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PingCommand) {
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.open();
        Field field = RedisSocketReplicator.class.getDeclaredField("executor");
        field.setAccessible(true);
        XScheduledExecutorService executor = (XScheduledExecutorService)field.get(replicator);
        assertTrue(executor.isShutdown());
        assertTrue(executor.isTerminated());
    }
    
    @Test
    @SuppressWarnings("resource")
    public void testSetUrl() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        Replicator replicator = new RedisReplicator("redis://127.0.0.1?retries=0");
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    try (Jedis jedis = new Jedis("127.0.0.1", 6379)) {
                        jedis.del("abca");
                        jedis.set("abca", "bcd");
                    }
                }
                if (event instanceof SetCommand) {
                    SetCommand setCommand = (SetCommand) event;
                    assertEquals("abca", Strings.toString(setCommand.getKey()));
                    assertEquals("bcd", Strings.toString(setCommand.getValue()));
                    ref.compareAndSet(null, "ok");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        
        replicator.open();
        assertEquals("ok", ref.get());
    }
    
    @Test
    public void testMixClose13() throws IOException, URISyntaxException, InterruptedException {
        final Replicator replicator = new RedisReplicator("redis://127.0.0.1:7777?retries=-1");
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
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                assertEquals(0, acc.get());
            }
        });
    
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                replicator.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        replicator.open();
    }
    
    @Test
    public void testMixClose10() throws IOException, URISyntaxException, InterruptedException {
        final Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
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
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                assertEquals(1, acc.get());
            }
        });
        replicator.open();
        
    }
    
    @SuppressWarnings("resource")
    @Test
    public void testMixClose8() throws IOException, URISyntaxException {
        final Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
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
    public void testMixClose9() throws IOException, URISyntaxException {
        final Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
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
}
