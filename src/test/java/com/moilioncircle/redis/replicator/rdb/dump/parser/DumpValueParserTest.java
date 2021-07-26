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

package com.moilioncircle.redis.replicator.rdb.dump.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;
import com.moilioncircle.redis.replicator.rdb.dump.DumpRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.dump.DumpRdbVisitorTest;
import com.moilioncircle.redis.replicator.rdb.dump.datatype.DumpKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueHash;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueList;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueSet;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueZSet;
import com.moilioncircle.redis.replicator.util.Strings;

import junit.framework.TestCase;

/**
 * @author Leon Chen
 */
@SuppressWarnings("unchecked")
public class DumpValueParserTest {

    @Test
    public void test0() throws UnsupportedEncodingException {
        List<KeyValuePair<?, ?>> kvs = new ArrayList<>();
        Replicator r = new RedisReplicator(DumpRdbVisitorTest.class.getClassLoader().getResourceAsStream("dump-huge-kv.rdb"), FileType.RDB, Configuration.defaultSetting());

        r.setRdbVisitor(new DumpRdbVisitor(r));
        r.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof DumpKeyValuePair) {
                    DumpKeyValuePair dkv = (DumpKeyValuePair) event;
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(dkv, this);
                } else if (event instanceof KeyValuePair) {
                    kvs.add((KeyValuePair<?, ?>) event);
                }
            }
        });
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }

        for (KeyValuePair<?, ?> dkv : kvs) {
            if (Strings.toString(dkv.getKey()).equals("k10")) {
                Map<byte[], byte[]> map = (Map<byte[], byte[]>) dkv.getValue();
                assertEquals(10, map.size());
            } else if (Strings.toString(dkv.getKey()).equals("list10")) {
                List<byte[]> list = (List<byte[]>) dkv.getValue();
                assertEquals(10, list.size());
            } else if (Strings.toString(dkv.getKey()).equals("zset")) {
                Set<ZSetEntry> zset = (Set<ZSetEntry>) dkv.getValue();
                assertEquals(10, zset.size());
            } else if (Strings.toString(dkv.getKey()).equals("set")) {
                Set<byte[]> set = (Set<byte[]>) dkv.getValue();
                assertEquals(10, set.size());
            } else if (Strings.toString(dkv.getKey()).equals("s")) {
                assertEquals("中国银行全球门户网站", new String((byte[]) dkv.getValue(), "UTF-8"));
            }
        }
    }

    @Test
    public void test1() {
        final AtomicInteger string = new AtomicInteger(0);
        final AtomicInteger map = new AtomicInteger(0);
        final AtomicInteger zset = new AtomicInteger(0);
        final AtomicInteger set = new AtomicInteger(0);
        final AtomicInteger list = new AtomicInteger(0);
        Replicator r = new RedisReplicator(DumpRdbVisitorTest.class.getClassLoader().getResourceAsStream("dump-huge-kv.rdb"), FileType.RDB, Configuration.defaultSetting());

        r.setRdbVisitor(new DumpRdbVisitor(r));
        r.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof DumpKeyValuePair) {
                    DumpKeyValuePair dkv = (DumpKeyValuePair) event;
                    DumpValueParser parser = new IterableDumpValueParser(2, replicator);
                    parser.parse(dkv, this);
                } else if (event instanceof KeyValuePair) {
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
            }
        });
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }
        TestCase.assertEquals(1, string.get());
        TestCase.assertEquals(5, map.get());
        TestCase.assertEquals(5, list.get());
        TestCase.assertEquals(5, set.get());
        TestCase.assertEquals(5, zset.get());
    }
}