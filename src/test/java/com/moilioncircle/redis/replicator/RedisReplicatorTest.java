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
import com.moilioncircle.redis.replicator.cmd.CommandFilter;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.impl.SetParser;
import com.moilioncircle.redis.replicator.cmd.impl.ZAddParser;
import com.moilioncircle.redis.replicator.cmd.impl.ZInterStoreParser;
import com.moilioncircle.redis.replicator.cmd.impl.ZUnionStoreParser;
import com.moilioncircle.redis.replicator.rdb.RdbFilter;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import junit.framework.TestCase;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.sortedset.ZAddParams;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by leon on 8/13/16.
 */
public class RedisReplicatorTest extends TestCase {

    @Test
    public void testSet() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        new TestTemplate() {
            @Override
            protected void test(RedisReplicator replicator) {
                replicator.addRdbListener(new RdbListener() {
                    @Override
                    public void preFullSync(Replicator replicator) {
                    }

                    @Override
                    public void handle(Replicator replicator, KeyValuePair<?> kv) {
                    }

                    @Override
                    public void postFullSync(Replicator replicator, long checksum) {
                        Jedis jedis = new Jedis("localhost",
                                6379);
                        jedis.del("abc");
                        jedis.set("abc", "bcd");
                        jedis.close();
                    }
                });
                replicator.addCommandFilter(new CommandFilter() {
                    @Override
                    public boolean accept(Command command) {
                        return command.name().equals(CommandName.name("SET"));
                    }
                });
                replicator.addCommandListener(new CommandListener() {
                    @Override
                    public void handle(Replicator replicator, Command command) {
                        SetParser.SetCommand setCommand = (SetParser.SetCommand) command;
                        assertEquals("abc", setCommand.key);
                        assertEquals("bcd", setCommand.value);
                        ref.compareAndSet(null, "ok");
                    }
                });
            }
        }.testSocket(
                "localhost",
                6379,
                Configuration.defaultSetting()
                        .setRetries(0)
                        .setVerbose(true),
                15000);
        assertEquals("ok", ref.get());
    }

    @Test
    public void testZInterStore() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        new TestTemplate() {
            @Override
            protected void test(RedisReplicator replicator) {
                replicator.addRdbListener(new RdbListener() {
                    @Override
                    public void preFullSync(Replicator replicator) {
                    }

                    @Override
                    public void handle(Replicator replicator, KeyValuePair<?> kv) {
                    }

                    @Override
                    public void postFullSync(Replicator replicator, long checksum) {
                        Jedis jedis = new Jedis("localhost",
                                6379);
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
                        System.out.println(jedis.zinterstore("out", zParams, "zset1", "zset2"));
                        jedis.close();
                    }
                });
                replicator.addCommandFilter(new CommandFilter() {
                    @Override
                    public boolean accept(Command command) {
                        return command.name().equals(CommandName.name("ZINTERSTORE"));
                    }
                });
                replicator.addCommandListener(new CommandListener() {
                    @Override
                    public void handle(Replicator replicator, Command command) {
                        ZInterStoreParser.ZInterStoreCommand zInterStoreCommand = (ZInterStoreParser.ZInterStoreCommand) command;
                        assertEquals("out", zInterStoreCommand.destination);
                        assertEquals(2, zInterStoreCommand.numkeys);
                        assertEquals("zset1", zInterStoreCommand.keys[0]);
                        assertEquals("zset2", zInterStoreCommand.keys[1]);
                        assertEquals(2.0, zInterStoreCommand.weights[0]);
                        assertEquals(3.0, zInterStoreCommand.weights[1]);
                        assertEquals(Boolean.TRUE, zInterStoreCommand.isAggregateMin);
                        ref.compareAndSet(null, "ok");
                    }
                });
            }
        }.testSocket(
                "localhost",
                6379,
                Configuration.defaultSetting()
                        .setRetries(0)
                        .setVerbose(true),
                15000);
        assertEquals("ok", ref.get());
    }

    @Test
    public void testZUnionStore() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        new TestTemplate() {
            @Override
            protected void test(RedisReplicator replicator) {
                replicator.addRdbListener(new RdbListener() {
                    @Override
                    public void preFullSync(Replicator replicator) {
                    }

                    @Override
                    public void handle(Replicator replicator, KeyValuePair<?> kv) {
                    }

                    @Override
                    public void postFullSync(Replicator replicator, long checksum) {
                        Jedis jedis = new Jedis("localhost",
                                6379);
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
                        System.out.println(jedis.zunionstore("out1", zParams, "zset3", "zset4"));
                        jedis.close();
                    }
                });
                replicator.addCommandFilter(new CommandFilter() {
                    @Override
                    public boolean accept(Command command) {
                        return command.name().equals(CommandName.name("ZUNIONSTORE"));
                    }
                });
                replicator.addCommandListener(new CommandListener() {
                    @Override
                    public void handle(Replicator replicator, Command command) {
                        ZUnionStoreParser.ZUnionStoreCommand zInterStoreCommand = (ZUnionStoreParser.ZUnionStoreCommand) command;
                        assertEquals("out1", zInterStoreCommand.destination);
                        assertEquals(2, zInterStoreCommand.numkeys);
                        assertEquals("zset3", zInterStoreCommand.keys[0]);
                        assertEquals("zset4", zInterStoreCommand.keys[1]);
                        assertEquals(2.0, zInterStoreCommand.weights[0]);
                        assertEquals(3.0, zInterStoreCommand.weights[1]);
                        assertEquals(Boolean.TRUE, zInterStoreCommand.isAggregateSum);
                        ref.compareAndSet(null, "ok");
                    }
                });
            }
        }.testSocket(
                "localhost",
                6379,
                Configuration.defaultSetting()
                        .setRetries(0)
                        .setVerbose(true),
                15000);
        assertEquals("ok", ref.get());
    }

    @Test
    public void testZAdd() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        new TestTemplate() {
            @Override
            protected void test(RedisReplicator replicator) {
                replicator.addRdbListener(new RdbListener() {
                    @Override
                    public void preFullSync(Replicator replicator) {
                    }

                    @Override
                    public void handle(Replicator replicator, KeyValuePair<?> kv) {
                    }

                    @Override
                    public void postFullSync(Replicator replicator, long checksum) {
                        Jedis jedis = new Jedis("localhost",
                                6379);
                        jedis.del("abc");
                        jedis.zrem("zzlist", "member");
                        jedis.set("abc", "bcd");
                        jedis.zadd("zzlist", 1.5, "member", ZAddParams.zAddParams().nx());
                        jedis.close();
                    }
                });
                replicator.addCommandFilter(new CommandFilter() {
                    @Override
                    public boolean accept(Command command) {
                        return command.name().equals(CommandName.name("SET"))
                                || command.name().equals(CommandName.name("ZADD"));
                    }
                });
                replicator.addCommandListener(new CommandListener() {
                    @Override
                    public void handle(Replicator replicator, Command command) {
                        if (command.name().equals(CommandName.name("SET"))) {
                            SetParser.SetCommand setCommand = (SetParser.SetCommand) command;
                            assertEquals("abc", setCommand.key);
                            assertEquals("bcd", setCommand.value);
                            ref.compareAndSet(null, "1");
                        } else if (command.name().equals(CommandName.name("ZADD"))) {
                            ZAddParser.ZAddCommand zaddCommand = (ZAddParser.ZAddCommand) command;
                            assertEquals("zzlist", zaddCommand.key);
                            assertEquals(1.5, zaddCommand.zEntries[0].score);
                            assertEquals("member", zaddCommand.zEntries[0].member);
                            assertEquals(Boolean.TRUE, zaddCommand.isNx);
                            ref.compareAndSet("1", "2");
                        }

                    }
                });
            }
        }.testSocket(
                "localhost",
                6379,
                Configuration.defaultSetting()
                        .setRetries(0)
                        .setVerbose(true),
                15000);
        assertEquals("2", ref.get());
    }

    @Test
    public void testFileV7() throws IOException, InterruptedException {
        RedisReplicator redisReplicator = new RedisReplicator(
                RedisReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"),
                Configuration.defaultSetting().setVerbose(true));
        final AtomicInteger acc = new AtomicInteger(0);
        redisReplicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
                acc.incrementAndGet();
                if (kv.getKey().equals("abcd")) {
                    KeyStringValueString ksvs = (KeyStringValueString) kv;
                    assertEquals("abcd", ksvs.getValue());
                }
                if (kv.getKey().equals("foo")) {
                    KeyStringValueString ksvs = (KeyStringValueString) kv;
                    assertEquals("bar", ksvs.getValue());
                }
                if (kv.getKey().equals("aaa")) {
                    KeyStringValueString ksvs = (KeyStringValueString) kv;
                    assertEquals("bbb", ksvs.getValue());
                }
            }
        });
        redisReplicator.open();
        Thread.sleep(2000);
        assertEquals(19, acc.get());
        redisReplicator.close();
    }

    public void testFilter() throws IOException, InterruptedException {
        RedisReplicator redisReplicator = new RedisReplicator(
                RedisReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"),
                Configuration.defaultSetting().setVerbose(true));
        final AtomicInteger acc = new AtomicInteger(0);
        redisReplicator.addRdbFilter(new RdbFilter() {
            @Override
            public boolean accept(KeyValuePair<?> kv) {
                return kv.getValueRdbType() == 0;
            }
        });
        redisReplicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
                assertEquals(0, acc.get());
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
                acc.incrementAndGet();
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                assertEquals(13, acc.get());
            }
        });
        redisReplicator.open();
        Thread.sleep(2000);
        assertEquals(13, acc.get());
        redisReplicator.close();
    }

    public void testFileV6() throws IOException, InterruptedException {
        RedisReplicator redisReplicator = new RedisReplicator(
                RedisReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV6.rdb"),
                Configuration.defaultSetting().setVerbose(true));
        final AtomicInteger acc = new AtomicInteger(0);
        redisReplicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
                acc.incrementAndGet();
            }
        });
        redisReplicator.open();
        Thread.sleep(2000);
        assertEquals(132, acc.get());
        redisReplicator.close();
    }

    @Test
    public void testV7() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        new TestTemplate() {
            @Override
            protected void test(RedisReplicator replicator) {
                replicator.addRdbListener(new RdbListener() {
                    @Override
                    public void preFullSync(Replicator replicator) {
                    }

                    @Override
                    public void handle(Replicator replicator, KeyValuePair<?> kv) {
                    }

                    @Override
                    public void postFullSync(Replicator replicator, long checksum) {
                        Jedis jedis = new Jedis("localhost",
                                6380);
                        jedis.auth("test");
                        jedis.del("abc");
                        jedis.set("abc", "bcd");
                        jedis.close();
                    }
                });
                replicator.addCommandFilter(new CommandFilter() {
                    @Override
                    public boolean accept(Command command) {
                        return command.name().equals(CommandName.name("SET"));
                    }
                });
                replicator.addCommandListener(new CommandListener() {
                    @Override
                    public void handle(Replicator replicator, Command command) {
                        SetParser.SetCommand setCommand = (SetParser.SetCommand) command;
                        assertEquals("abc", setCommand.key);
                        assertEquals("bcd", setCommand.value);
                        ref.compareAndSet(null, "ok");
                    }
                });
            }
        }.testSocket(
                "localhost",
                6380,
                Configuration.defaultSetting()
                        .setAuthPassword("test")
                        .setRetries(0)
                        .setVerbose(true),
                15000);
        assertEquals("ok", ref.get());
    }

    @Test
    public void testExpireV6() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        new TestTemplate() {
            @Override
            protected void test(RedisReplicator replicator) {
                Jedis jedis = new Jedis("localhost",
                        6379);
                jedis.del("abc");
                jedis.del("bbb");
                jedis.set("abc", "bcd");
                jedis.expire("abc", 500);
                jedis.set("bbb", "bcd");
                jedis.expireAt("bbb", System.currentTimeMillis() + 1000000);
                jedis.close();

                replicator.addRdbListener(new RdbListener() {
                    @Override
                    public void preFullSync(Replicator replicator) {
                    }

                    @Override
                    public void handle(Replicator replicator, KeyValuePair<?> kv) {
                        if (kv.getKey().equals("abc")) {
                            assertNotNull(kv.getExpiredMs());
                        } else if (kv.getKey().equals("bbb")) {
                            assertNotNull(kv.getExpiredMs());
                        }
                    }

                    @Override
                    public void postFullSync(Replicator replicator, long checksum) {

                    }
                });
            }
        }.testSocket(
                "localhost",
                6379,
                Configuration.defaultSetting()
                        .setRetries(0)
                        .setVerbose(true),
                15000);
    }

    @Test
    public void testCloseListener() throws IOException, InterruptedException {
        final AtomicInteger acc = new AtomicInteger(0);
        RedisReplicator replicator = new RedisReplicator("127.0.0.1", 6666, Configuration.defaultSetting());
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                acc.incrementAndGet();
            }
        });
        replicator.open();
        assertEquals(5, acc.get());
    }

    @Test
    public void testChecksumV6() throws IOException, InterruptedException {
        RedisReplicator redisReplicator = new RedisReplicator(
                RedisReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV6.rdb"),
                Configuration.defaultSetting().setVerbose(true));
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicLong atomicChecksum = new AtomicLong(0);
        redisReplicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
                acc.incrementAndGet();
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                atomicChecksum.compareAndSet(0, checksum);
            }
        });
        redisReplicator.open();
        Thread.sleep(2000);
        assertEquals(132, acc.get());
        assertEquals(-3409494954737929802L, atomicChecksum.get());
        redisReplicator.close();
    }

    @Test
    public void testChecksumV7() throws IOException, InterruptedException {
        RedisReplicator redisReplicator = new RedisReplicator(
                RedisReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"),
                Configuration.defaultSetting().setVerbose(true));
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicLong atomicChecksum = new AtomicLong(0);
        redisReplicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
                acc.incrementAndGet();
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                atomicChecksum.compareAndSet(0, checksum);
            }
        });
        redisReplicator.open();
        Thread.sleep(2000);
        assertEquals(19, acc.get());
        assertEquals(6576517133597126869L, atomicChecksum.get());
        redisReplicator.close();
    }
}
