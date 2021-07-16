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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.moilioncircle.redis.replicator.CloseListener;
import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.RedisSocketReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.CommandName;
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
import com.moilioncircle.redis.replicator.rdb.datatype.AuxField;
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
                Thread.sleep(2000);
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
        CompletableFuture<Void> future = new CompletableFuture<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PreCommandSyncEvent) {
                    acc.incrementAndGet();
                    future.complete(null);
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
    
        new Thread(() -> {
            try {
                future.get();
                replicator.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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
    
    @Test
    public void testPsync() throws IOException {
        
        final Configuration configuration = Configuration.defaultSetting().
                setAuthPassword("test").
                setConnectionTimeout(3000).
                setReadTimeout(3000).
                setBufferSize(64).
                setAsyncCachedBytes(0).
                setHeartbeatPeriod(200).
                setReceiveBufferSize(0).
                setSendBufferSize(0).
                setRetryTimeInterval(1000).
                setUseDefaultExceptionListener(false);
        @SuppressWarnings("resource")
        Replicator replicator = new TestRedisSocketReplicator("127.0.0.1", 6380, configuration);
        final AtomicBoolean flag = new AtomicBoolean(false);
        final Set<AuxField> set = new LinkedHashSet<>();
        final AtomicInteger acc = new AtomicInteger();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof AuxField) {
                    set.add((AuxField) event);
                }
                if (event instanceof PostRdbSyncEvent) {
                    if (flag.compareAndSet(false, true)) {
                        Thread thread = new Thread(new JRun());
                        thread.setDaemon(true);
                        thread.start();
                        replicator.removeCommandParser(CommandName.name("PING"));
                    }
                }
                if (event instanceof SetCommand && Strings.toString(((SetCommand) event).getKey()).startsWith("psync")) {
                    acc.incrementAndGet();
                    if (acc.get() == 500) {
                        //close current process port;
                        //that will auto trigger psync command
                        close(replicator);
                    }
                    
                    if (acc.get() == 1010) {
                        //close current process port;
                        //that will auto trigger psync command
                        close(replicator);
                    }
                    if (acc.get() == 1480) {
                        configuration.setVerbose(true);
                    }
                    if (acc.get() == 1500) {
                        try {
                            replicator.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        });
        replicator.open();
        assertEquals(1500, acc.get());
    }
    
    private static void close(Replicator replicator) {
        try {
            ((TestRedisSocketReplicator) replicator).getOutputStream().close();
        } catch (IOException e) {
        }
        try {
            ((TestRedisSocketReplicator) replicator).getInputStream().close();
        } catch (IOException e) {
        }
        try {
            ((TestRedisSocketReplicator) replicator).getSocket().close();
        } catch (IOException e) {
        }
    }
    
    private static class JRun implements Runnable {
        
        @Override
        public void run() {
            try (Jedis jedis = new Jedis("127.0.0.1", 6380)) {
                jedis.auth("test");
                Pipeline pipeline = jedis.pipelined();
                for (int i = 0; i < 1500; i++) {
                    pipeline.set("psync " + i, "psync" + i);
                }
                pipeline.sync();
            }
        }
    }
    
    private static class TestRedisSocketReplicator extends RedisSocketReplicator {
        
        public TestRedisSocketReplicator(String host, int port, Configuration configuration) {
            super(host, port, configuration);
        }
        
        public Socket getSocket() {
            return super.socket;
        }
        
        public InputStream getInputStream() {
            return super.inputStream;
        }
        
        public OutputStream getOutputStream() {
            return super.outputStream;
        }
    }
}
