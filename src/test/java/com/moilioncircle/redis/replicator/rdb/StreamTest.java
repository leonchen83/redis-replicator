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

package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueStream;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.Stream;
import com.moilioncircle.redis.replicator.rdb.dump.DumpRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.skip.SkipRdbVisitor;
import com.moilioncircle.redis.replicator.util.Strings;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class StreamTest {

    @Test
    public void test() throws IOException {
        @SuppressWarnings("resource") final Replicator replicator = new RedisReplicator(StreamTest.class.getClassLoader().getResourceAsStream("dump-stream.rdb"), FileType.RDB, Configuration.defaultSetting());
        final Map<String, Stream> kvs = new HashMap<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueStream) {
                    KeyStringValueStream kv = (KeyStringValueStream) event;
                    kvs.put(Strings.toString(kv.getKey()), kv.getValue());
                }
            }
        });
        replicator.open();
        for (Map.Entry<String, Stream> e : kvs.entrySet()) {
            String key = e.getKey();
            Stream stream = e.getValue();

            if (key.equals("listpack")) {
                NavigableMap<Stream.ID, Stream.Entry> map = stream.getEntries();
                {
                    int i = 0;
                    for (Stream.Entry entry : map.values()) {
                        assertTrue(entry.getFields().containsKey(("field" + i).getBytes()));
                        i++;
                    }
                }

                assertEquals(4, stream.getGroups().size());

                for (Stream.Group group : stream.getGroups()) {
                    if (Strings.toString(group.getName()).equals("g1")) {
                        assertEquals(4, group.getPendingEntries().size());
                        assertEquals(2, group.getConsumers().size());
                        assertEquals(2, group.getConsumers().get(0).getPendingEntries().size());
                        assertEquals(2, group.getConsumers().get(1).getPendingEntries().size());
                    } else if (Strings.toString(group.getName()).equals("g2")) {
                        assertEquals(1, group.getPendingEntries().size());
                        assertEquals(1, group.getConsumers().size());
                        assertEquals(1, group.getConsumers().get(0).getPendingEntries().size());
                    } else if (Strings.toString(group.getName()).equals("g3")) {
                        assertEquals(2, group.getPendingEntries().size());
                        assertEquals(2, group.getConsumers().size());
                        assertEquals(2, group.getConsumers().get(0).getPendingEntries().size());
                    } else if (Strings.toString(group.getName()).equals("g4")) {
                        assertEquals(0, group.getPendingEntries().size());
                        assertEquals(0, group.getConsumers().size());
                    }
                }
            } else if (key.equals("trim")) {
                NavigableMap<Stream.ID, Stream.Entry> map = stream.getEntries();
                {
                    int i = 0;
                    for (Stream.Entry entry : map.values()) {
                        if (i < 20) {
                            assertTrue(entry.isDeleted());
                        } else {
                            if (entry.getId().equals(Stream.ID.valueOf("1528512149341-0"))) {
                                assertTrue(entry.isDeleted());
                            } else if (entry.getId().equals(Stream.ID.valueOf("1528512149742-0"))) {
                                assertTrue(entry.isDeleted());
                            } else {
                                assertFalse(entry.isDeleted());
                            }
                        }
                        i++;
                    }
                }
            } else if (key.equals("nums")) {
                List<Stream.Entry> list = new ArrayList<>(stream.getEntries().values());

                assertTrue(list.get(0).getFields().containsKey(String.valueOf(-2).getBytes()));

                assertTrue(list.get(1).getFields().containsKey(String.valueOf(-2000).getBytes()));

                assertTrue(list.get(2).getFields().containsKey(String.valueOf(-20000).getBytes()));

                assertTrue(list.get(3).getFields().containsKey(String.valueOf(-200000).getBytes()));

                assertTrue(list.get(4).getFields().containsKey(String.valueOf(-20000000).getBytes()));

                assertTrue(list.get(5).getFields().containsKey(String.valueOf(-2000000000).getBytes()));

                assertTrue(list.get(6).getFields().containsKey(String.valueOf(-200000000000L).getBytes()));

                assertTrue(list.get(7).getFields().containsKey(String.valueOf(-20000000000000L).getBytes()));

                assertTrue(list.get(8).getFields().containsKey(String.valueOf(-2).getBytes()));

                assertTrue(list.get(9).getFields().containsKey(String.valueOf(-2000).getBytes()));

                assertTrue(list.get(10).getFields().containsKey(String.valueOf(-20000).getBytes()));

                assertTrue(list.get(11).getFields().containsKey(String.valueOf(-200000).getBytes()));

                assertTrue(list.get(12).getFields().containsKey(String.valueOf(-20000000).getBytes()));

                assertTrue(list.get(13).getFields().containsKey(String.valueOf(-2000000000).getBytes()));

                assertTrue(list.get(14).getFields().containsKey(String.valueOf(-200000000000L).getBytes()));

                assertTrue(list.get(15).getFields().containsKey(String.valueOf(-20000000000000L).getBytes()));

                assertTrue(list.get(16).getFields().containsKey(String.valueOf(-20).getBytes()));

                assertTrue(list.get(17).getFields().containsKey(String.valueOf(-200).getBytes()));
            }
        }
    }

    @Test
    public void testStream1() throws IOException {
        @SuppressWarnings("resource") final Replicator replicator = new RedisReplicator(StreamTest.class.getClassLoader().getResourceAsStream("dump-stream1.rdb"), FileType.RDB, Configuration.defaultSetting());
        final Map<String, Stream> kvs = new HashMap<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueStream) {
                    KeyStringValueStream kv = (KeyStringValueStream) event;
                    kvs.put(Strings.toString(kv.getKey()), kv.getValue());
                }
            }
        });
        replicator.open();
        for (Map.Entry<String, Stream> e : kvs.entrySet()) {
            String key = e.getKey();
            Stream stream = e.getValue();

            if (key.equals("trim")) {
                assertEquals(120L, stream.getLength());
                NavigableMap<Stream.ID, Stream.Entry> map = stream.getEntries();
                {
                    int i = 0;
                    for (Stream.Entry entry : map.values()) {
                        if (i < 30) {
                            assertTrue(entry.isDeleted());
                        } else {
                            if (entry.getId().equals(Stream.ID.valueOf("1528512149341-0"))) {
                                assertTrue(entry.isDeleted());
                            } else if (entry.getId().equals(Stream.ID.valueOf("1528512149742-0"))) {
                                assertTrue(entry.isDeleted());
                            } else {
                                assertFalse(entry.isDeleted());
                            }
                        }
                        i++;
                    }
                }
            }
        }
    }

    @Test
    public void testSkip() {
        final Replicator replicator = new RedisReplicator(StreamTest.class.getClassLoader().getResourceAsStream("dump-stream.rdb"), FileType.RDB, Configuration.defaultSetting());
        replicator.setRdbVisitor(new SkipRdbVisitor(replicator));
        try {
            replicator.open();
        } catch (Throwable e) {
            fail();
        }
    }

    @Test
    public void testDump() {
        final Replicator replicator = new RedisReplicator(StreamTest.class.getClassLoader().getResourceAsStream("dump-stream.rdb"), FileType.RDB, Configuration.defaultSetting());
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator));
        final Map<byte[], byte[]> map = new HashMap<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair) {
                    KeyValuePair<byte[], byte[]> dkv = (KeyValuePair<byte[], byte[]>) event;
                    map.put(dkv.getKey(), dkv.getValue());
                }
            }
        });
        try {
            replicator.open();
        } catch (Throwable e) {
            fail();
        }

        TestCase.assertEquals(5, map.size());
        for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
            assertNotNull(String.valueOf(entry.getValue()));
        }
    }
}
