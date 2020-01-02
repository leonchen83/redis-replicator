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

package com.moilioncircle.redis.replicator;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.util.Strings;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class RedisURITest {

    @Test
    @SuppressWarnings("resource")
    public void testFile() throws URISyntaxException, IOException {
        URL url = RedisURITest.class.getClassLoader().getResource("dumpV7.rdb");
        URI uri = url.toURI();
        URI redisURI = new URI("redis", uri.getRawAuthority(), uri.getRawPath(), uri.getRawQuery(), uri.getRawFragment());
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicLong atomicChecksum = new AtomicLong(0);
        Replicator r = new RedisReplicator(redisURI.toString());
        r.addEventListener(new EventListener() {
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
        r.open();
        assertEquals(19, acc.get());
        assertEquals(6576517133597126869L, atomicChecksum.get());
    }

    @Test
    @SuppressWarnings("resource")
    public void testSet() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>(null);
        Replicator replicator = new RedisReplicator("redis://localhost?retries=0");
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    Jedis jedis = new Jedis("localhost", 6379);
                    jedis.del("abca");
                    jedis.set("abca", "bcd");
                    jedis.close();
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

    @SuppressWarnings("resource")
    @Test
    public void testOpen1() throws IOException, URISyntaxException {
        URL url = RedisURITest.class.getClassLoader().getResource("appendonly1.aof");
        URI uri = url.toURI();
        URI redisURI = new URI("redis", uri.getRawAuthority(), uri.getRawPath(), uri.getRawQuery(), uri.getRawFragment());
        Replicator replicator = new RedisReplicator(redisURI.toString());
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
                }
            }
        });
        replicator.open();
        assertEquals(0, acc.get());
        assertEquals(4, acc1.get());
    }

    @Test
    public void testURI() throws URISyntaxException, UnsupportedEncodingException {
        String str = "redis:///test?" + URLEncoder.encode("新建文件夹", "UTF-8") + "=dump.rdb";
        RedisURI uri = new RedisURI(str);
        assertEquals(str, uri.toASCIIString());
        assertEquals("dump.rdb", uri.parameters.get("新建文件夹"));
        str = "redis:///test?" + URLEncoder.encode("新建文件夹", "UTF-8") + "=" + URLEncoder.encode("新建文件夹", "UTF-8");
        uri = new RedisURI(str);
        assertEquals(str, uri.toASCIIString());
        assertEquals("新建文件夹", uri.parameters.get("新建文件夹"));
        str = "redis:///test?key=value";
        uri = new RedisURI(str);
        assertEquals(str, uri.toASCIIString());
        assertEquals("value", uri.parameters.get("key"));
        str = "redis:///test?key=%20";
        uri = new RedisURI(str);
        assertEquals(str, uri.toASCIIString());
        assertEquals(" ", uri.parameters.get("key"));

        str = "rediss://user:pass@host:6379";
        uri = new RedisURI(str);

        assertEquals("user", uri.getUser());
        assertEquals("pass", uri.getPassword());
        assertEquals(true, uri.isSsl());

        str = "rediss://:pass@host:6379";
        uri = new RedisURI(str);

        assertEquals(null, uri.getUser());
        assertEquals("pass", uri.getPassword());
        assertEquals(true, uri.isSsl());

        str = "rediss://user@host:6379";
        uri = new RedisURI(str);

        assertEquals("user", uri.getUser());
        assertEquals(null, uri.getPassword());
        assertEquals(true, uri.isSsl());

        str = "rediss://user:@host:6379";
        uri = new RedisURI(str);

        assertEquals("user", uri.getUser());
        assertEquals(null, uri.getPassword());
        assertEquals(true, uri.isSsl());

        str = "rediss://user:pa:ss@host:6379";
        uri = new RedisURI(str);

        assertEquals("user", uri.getUser());
        assertEquals("pa:ss", uri.getPassword());
        assertEquals(true, uri.isSsl());
    }
}
