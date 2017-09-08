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
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class RedisURITest {
    public static void main(String[] args) throws URISyntaxException, IOException {
        URL url = RedisURITest.class.getClassLoader().getResource("dumpV7.rdb");
        URI uri = url.toURI();
        URI redisURI = new URI("redis", uri.getRawAuthority(), uri.getRawPath(), uri.getRawQuery(), uri.getRawFragment());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicLong atomicChecksum = new AtomicLong(0);
        Replicator r = new RedisReplicator(redisURI.toString());
        r.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                acc.incrementAndGet();
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                super.postFullSync(replicator, checksum);
                atomicChecksum.compareAndSet(0, checksum);
            }
        });
        r.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testChecksumV7");
            }
        });
        r.open();
        assertEquals(19, acc.get());
        assertEquals(6576517133597126869L, atomicChecksum.get());
    }

    @Test
    public void testSet() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        Replicator replicator = new RedisReplicator("redis://localhost?retries=0");
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
                jedis.del("abca");
                jedis.set("abca", "bcd");
                jedis.close();
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof SetCommand) {
                    SetCommand setCommand = (SetCommand) command;
                    assertEquals("abca", setCommand.getKey());
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
                System.out.println("close RedisURI testSet");
            }
        });
        replicator.open();
        assertEquals("ok", ref.get());
    }

    @Test
    public void testOpen1() throws IOException, URISyntaxException {
        URL url = RedisURITest.class.getClassLoader().getResource("appendonly1.aof");
        URI uri = url.toURI();
        URI redisURI = new URI("redis", uri.getRawAuthority(), uri.getRawPath(), uri.getRawQuery(), uri.getRawFragment());
        Replicator replicator = new RedisReplicator(redisURI.toString());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicInteger acc1 = new AtomicInteger(0);
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                acc.incrementAndGet();
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                acc1.incrementAndGet();
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close RedisURI testOpen1");
            }
        });
        replicator.open();
        assertEquals(0, acc.get());
        assertEquals(4, acc1.get());
    }
}
