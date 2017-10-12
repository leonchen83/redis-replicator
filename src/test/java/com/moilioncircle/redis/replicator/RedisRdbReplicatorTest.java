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

import com.moilioncircle.redis.replicator.io.RateLimitInputStream;
import com.moilioncircle.redis.replicator.rdb.AuxFieldListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.AuxField;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@SuppressWarnings("resource")
public class RedisRdbReplicatorTest {
    @Test
    public void testChecksumV7() throws IOException, InterruptedException {
        Replicator redisReplicator = new RedisReplicator(
                new RateLimitInputStream(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb")), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicLong atomicChecksum = new AtomicLong(0);
        redisReplicator.addRdbListener(new RdbListener.Adaptor() {
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
        redisReplicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testChecksumV7");
            }
        });
        redisReplicator.open();
        assertEquals(19, acc.get());
        assertEquals(6576517133597126869L, atomicChecksum.get());
    }

    @Test
    public void testChecksumV6() throws IOException, InterruptedException {
        Replicator redisReplicator = new RedisReplicator(
                new RateLimitInputStream(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV6.rdb"), 1000), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicLong atomicChecksum = new AtomicLong(0);
        redisReplicator.addRdbListener(new RdbListener.Adaptor() {
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
        redisReplicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testChecksumV6");
            }
        });
        redisReplicator.open();
        assertEquals(132, acc.get());
        assertEquals(-3409494954737929802L, atomicChecksum.get());
    }

    @Test
    public void testCloseListener1() throws IOException, InterruptedException {
        final AtomicInteger acc = new AtomicInteger(0);
        Replicator replicator = new RedisReplicator(
                new RateLimitInputStream(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV6.rdb"), 1000), FileType.RDB,
                Configuration.defaultSetting());
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testCloseListener1");
                acc.incrementAndGet();
            }
        });
        replicator.open();
        assertEquals(1, acc.get());
    }

    @Test
    public void testFileV7() throws IOException, InterruptedException {
        Replicator redisReplicator = new RedisReplicator(
                new RateLimitInputStream(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), 1000), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        final List<KeyValuePair<?>> list = new ArrayList<>();
        redisReplicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                list.add(kv);
                acc.incrementAndGet();
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                super.postFullSync(replicator, checksum);
            }
        });
        redisReplicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testFileV7");
            }
        });
        redisReplicator.open();
        assertEquals(19, acc.get());
        for (KeyValuePair<?> kv : list) {
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
    }

    @Test
    public void testFilter() throws IOException, InterruptedException {
        Replicator redisReplicator = new RedisReplicator(
                new RateLimitInputStream(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), 1000), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        redisReplicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void preFullSync(Replicator replicator) {
                super.preFullSync(replicator);
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (kv.getValueRdbType() == 0) {
                    acc.incrementAndGet();
                }
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                super.postFullSync(replicator, checksum);
            }
        });
        redisReplicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testFilter");
            }
        });
        redisReplicator.open();
        assertEquals(13, acc.get());
    }

    @Test
    public void testFileV6() throws IOException, InterruptedException {
        Replicator redisReplicator = new RedisReplicator(
                new RateLimitInputStream(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV6.rdb"), 1000), FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        redisReplicator.addRdbListener(new RdbListener.Adaptor() {

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                acc.incrementAndGet();
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                super.postFullSync(replicator, checksum);
            }
        });
        redisReplicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testFileV6");
            }
        });
        redisReplicator.open();
        assertEquals(132, acc.get());
    }

    @Test
    public void testFileV8() throws IOException, InterruptedException {
        Replicator redisReplicator = new RedisReplicator(
                new RateLimitInputStream(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV8.rdb"), 100 * 1024),
                FileType.RDB,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicInteger acc1 = new AtomicInteger(0);
        redisReplicator.addAuxFieldListener(new AuxFieldListener() {
            @Override
            public void handle(Replicator replicator, AuxField auxField) {
                System.out.println(auxField);
                acc1.incrementAndGet();
            }
        });
        redisReplicator.addRdbListener(new RdbListener.Adaptor() {

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                acc.incrementAndGet();
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                super.postFullSync(replicator, checksum);
            }
        });
        redisReplicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close testFileV8");
            }
        });
        redisReplicator.open();
        assertEquals(92499, acc.get());
        assertEquals(7, acc1.get());
    }

}