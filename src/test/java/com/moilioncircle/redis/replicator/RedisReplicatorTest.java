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
import com.moilioncircle.redis.replicator.rdb.RdbFilter;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import junit.framework.TestCase;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
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
                    public void postFullSync(Replicator replicator) {
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
                    public void postFullSync(Replicator replicator) {
                        Jedis jedis = new Jedis("localhost",
                                6379);
                        jedis.del("abc");
                        jedis.zrem("zzlist", "member");
                        jedis.set("abc", "bcd");
                        jedis.zadd("zzlist", 1.5, "member");
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

    public void testFile() throws IOException, InterruptedException {
        RedisReplicator redisReplicator = new RedisReplicator(
                RedisReplicatorTest.class.getClassLoader().getResourceAsStream("dump.rdb"),
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
        assertEquals(16, acc.get());
        redisReplicator.close();
    }

    public void testFilter() throws IOException, InterruptedException {
        RedisReplicator redisReplicator = new RedisReplicator(
                RedisReplicatorTest.class.getClassLoader().getResourceAsStream("dump.rdb"),
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
            public void postFullSync(Replicator replicator) {
                assertEquals(13, acc.get());
            }
        });
        redisReplicator.open();
        Thread.sleep(2000);
        assertEquals(13, acc.get());
        redisReplicator.close();
    }

}
