/*
 * Copyright 2016 leon chen
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

package com.moilioncircle.redis.replicator;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.impl.*;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import junit.framework.TestCase;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.sortedset.ZAddParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@SuppressWarnings("resource")
public class RedisSocketReplicatorTest extends TestCase {

    @Test
    public void testSet() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        Replicator replicator = new RedisReplicator("localhost", 6379, Configuration.defaultSetting().setRetries(0));
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                Jedis jedis = new Jedis("localhost", 6379);
                jedis.del("abc");
                jedis.set("abc", "bcd");
                jedis.close();
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof SetCommand) {
                    SetCommand setCommand = (SetCommand) command;
                    assertEquals("abc", setCommand.getKey());
                    assertEquals("bcd", setCommand.getValue());
                    ref.compareAndSet(null, "ok");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testSet");
            }
        });
        replicator.open();
        assertEquals("ok", ref.get());
    }

    @Test
    public void testZInterStore() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        final Replicator replicator = new RedisReplicator("localhost", 6379, Configuration.defaultSetting().setRetries(0));
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                Jedis jedis = new Jedis("localhost", 6379);
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
                zParams.weightsByDouble(2, 3);
                zParams.aggregate(ZParams.Aggregate.MIN);
                jedis.zinterstore("out", zParams, "zset1", "zset2");
                jedis.close();
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof ZInterStoreCommand) {
                    ZInterStoreCommand zInterStoreCommand = (ZInterStoreCommand) command;
                    assertEquals("out", zInterStoreCommand.getDestination());
                    assertEquals(2, zInterStoreCommand.getNumkeys());
                    assertEquals("zset1", zInterStoreCommand.getKeys()[0]);
                    assertEquals("zset2", zInterStoreCommand.getKeys()[1]);
                    assertEquals(2.0, zInterStoreCommand.getWeights()[0]);
                    assertEquals(3.0, zInterStoreCommand.getWeights()[1]);
                    assertEquals(AggregateType.MIN, zInterStoreCommand.getAggregateType());
                    ref.compareAndSet(null, "ok");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testZInterStore");
            }
        });
        replicator.open();
        assertEquals("ok", ref.get());
    }

    @Test
    public void testZUnionStore() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        final Replicator replicator = new RedisReplicator("localhost", 6379, Configuration.defaultSetting().setRetries(0));
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                Jedis jedis = new Jedis("localhost", 6379);
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
                zParams.weightsByDouble(2, 3);
                zParams.aggregate(ZParams.Aggregate.SUM);
                jedis.zunionstore("out1", zParams, "zset3", "zset4");
                jedis.close();
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof ZUnionStoreCommand) {
                    ZUnionStoreCommand zInterStoreCommand = (ZUnionStoreCommand) command;
                    assertEquals("out1", zInterStoreCommand.getDestination());
                    assertEquals(2, zInterStoreCommand.getNumkeys());
                    assertEquals("zset3", zInterStoreCommand.getKeys()[0]);
                    assertEquals("zset4", zInterStoreCommand.getKeys()[1]);
                    assertEquals(2.0, zInterStoreCommand.getWeights()[0]);
                    assertEquals(3.0, zInterStoreCommand.getWeights()[1]);
                    assertEquals(AggregateType.SUM, zInterStoreCommand.getAggregateType());
                    ref.compareAndSet(null, "ok");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testZUnionStore");
            }
        });
        replicator.open();
        assertEquals("ok", ref.get());
    }

    @Test
    public void testCloseListener() throws IOException, InterruptedException {
        final AtomicInteger acc = new AtomicInteger(0);
        Replicator replicator = new RedisReplicator("127.0.0.1", 6666, Configuration.defaultSetting().setUseDefaultExceptionListener(false));
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                acc.incrementAndGet();
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testCloseListener");
            }
        });
        replicator.open();
        assertEquals(1, acc.get());
    }

    @Test
    public void testZAdd() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        final Replicator replicator = new RedisReplicator("localhost", 6379, Configuration.defaultSetting().setRetries(0));
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                Jedis jedis = new Jedis("localhost", 6379);
                jedis.del("abc");
                jedis.zrem("zzlist", "member");
                jedis.set("abc", "bcd");
                jedis.zadd("zzlist", 1.5, "member", ZAddParams.zAddParams().nx());
                jedis.close();
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof SetCommand) {
                    SetCommand setCommand = (SetCommand) command;
                    assertEquals("abc", setCommand.getKey());
                    assertEquals("bcd", setCommand.getValue());
                    ref.compareAndSet(null, "1");
                } else if (command instanceof ZAddCommand) {
                    ZAddCommand zaddCommand = (ZAddCommand) command;
                    assertEquals("zzlist", zaddCommand.getKey());
                    assertEquals(1.5, zaddCommand.getZSetEntries()[0].getScore());
                    assertEquals("member", zaddCommand.getZSetEntries()[0].getElement());
                    assertEquals(ExistType.NX, zaddCommand.getExistType());
                    ref.compareAndSet("1", "2");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }

            }
        });

        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testZAdd");
            }
        });
        replicator.open();
        assertEquals("2", ref.get());
    }

    @Test
    public void testV7() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        final Replicator replicator = new RedisReplicator("localhost", 6380, Configuration.defaultSetting().setAuthPassword("test").setRetries(0));
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                Jedis jedis = new Jedis("localhost", 6380);
                jedis.auth("test");
                jedis.del("abc");
                jedis.set("abc", "bcd");
                jedis.close();
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof SetCommand) {
                    SetCommand setCommand = (SetCommand) command;
                    assertEquals("abc", setCommand.getKey());
                    assertEquals("bcd", setCommand.getValue());
                    ref.compareAndSet(null, "ok");
                    try {
                        replicator.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testV7");
            }
        });
        replicator.open();
        assertEquals("ok", ref.get());
    }

    @Test
    public void testExpireV6() throws Exception {
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.del("abc");
        jedis.del("bbb");
        jedis.set("abc", "bcd");
        jedis.expire("abc", 500);
        jedis.set("bbb", "bcd");
        jedis.expireAt("bbb", System.currentTimeMillis() + 1000000);
        jedis.close();

        Replicator replicator = new RedisReplicator("localhost", 6379, Configuration.defaultSetting().setRetries(0));
        final List<KeyValuePair<?>> list = new ArrayList<>();
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                list.add(kv);
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                try {
                    replicator.close();
                } catch (IOException e) {
                }
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testExpireV6");
            }
        });
        replicator.open();
        for (KeyValuePair<?> kv : list) {
            if (kv.getKey().equals("abc")) {
                assertNotNull(kv.getExpiredMs());
            } else if (kv.getKey().equals("bbb")) {
                assertNotNull(kv.getExpiredMs());
            }
        }
    }

    @Test
    public void testCount() throws IOException, InterruptedException {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        for (int i = 0; i < 8000; i++) {
            jedis.del("test_" + i);
            jedis.set("test_" + i, "value_" + i);
        }
        jedis.close();

        Replicator redisReplicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        redisReplicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (kv.getKey().startsWith("test_")) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                    acc.incrementAndGet();
                }
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                try {
                    replicator.close();
                } catch (IOException e) {
                }
            }
        });
        redisReplicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testCount");
            }
        });
        redisReplicator.open();
        assertEquals(8000, acc.get());
    }
}
