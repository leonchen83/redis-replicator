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

package com.moilioncircle.redis.replicator.rdb.dump;

import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_LISTPACK;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST_QUICKLIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST_QUICKLIST_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET_LISTPACK;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_LISTPACK;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueHash;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueList;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueSet;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueZSet;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;
import com.moilioncircle.redis.replicator.rdb.dump.datatype.DumpKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.dump.parser.DefaultDumpValueParser;
import com.moilioncircle.redis.replicator.rdb.dump.parser.DumpValueParser;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * @author Leon Chen
 */
public class DumpRdbVisitorTest {
    @Test
    public void test1() {
        final byte[] string = ByteParser.toBytes("\\x00\\x1e\\xe4\\xb8\\xad\\xe5\\x9b\\xbd\\xe9\\x93\\xb6\\xe8\\xa1\\x8c\\xe5\\x85\\xa8\\xe7\\x90\\x83\\xe9\\x97\\xa8\\xe6\\x88\\xb7\\xe7\\xbd\\x91\\xe7\\xab\\x99\\b\\x00k\\xe3\\x19'\\x94\\xe6\\x8a9");
        byte[] set = ByteParser.toBytes("\\x0b\\x1c\\x02\\x00\\x00\\x00\\n\\x00\\x00\\x00\\x01\\x00\\x02\\x00\\x03\\x00\\x04\\x00\\x05\\x00\\x06\\x00\\a\\x00\\b\\x00\\t\\x00\\n\\x00\\b\\x00w\\xa2\\x0fZ~\\xf5\\xb8\\x80");
        byte[] list = ByteParser.toBytes("\\x0e\\x01\\x1f\\x1f\\x00\\x00\\x00\\x1c\\x00\\x00\\x00\\n\\x00\\x00\\xfb\\x02\\xfa\\x02\\xf9\\x02\\xf8\\x02\\xf7\\x02\\xf6\\x02\\xf5\\x02\\xf4\\x02\\xf3\\x02\\xf2\\xff\\b\\x00\\xd5T\\xbeK\\xcdf\\x0f\\x1b");
        byte[] map = ByteParser.toBytes("\\r33\\x00\\x00\\x000\\x00\\x00\\x00\\x14\\x00\\x00\\xf2\\x02\\xf2\\x02\\xf3\\x02\\xf3\\x02\\xf4\\x02\\xf4\\x02\\xf5\\x02\\xf5\\x02\\xf6\\x02\\xf6\\x02\\xf7\\x02\\xf7\\x02\\xf8\\x02\\xf8\\x02\\xf9\\x02\\xf9\\x02\\xfa\\x02\\xfa\\x02\\xfb\\x02\\xfb\\xff\\b\\x00\\xee2\\x87;\\xceN\\x93P");
        byte[] zset = ByteParser.toBytes("\\x0c33\\x00\\x00\\x000\\x00\\x00\\x00\\x14\\x00\\x00\\xf2\\x02\\xf2\\x02\\xf3\\x02\\xf3\\x02\\xf4\\x02\\xf4\\x02\\xf5\\x02\\xf5\\x02\\xf6\\x02\\xf6\\x02\\xf7\\x02\\xf7\\x02\\xf8\\x02\\xf8\\x02\\xf9\\x02\\xf9\\x02\\xfa\\x02\\xfa\\x02\\xfb\\x02\\xfb\\xff\\b\\x00\\x87\\xb7\\x16\\xf8\\xc9^\\xaf\\\\");
        final AtomicReference<byte[]> astring = new AtomicReference<>();
        final AtomicReference<byte[]> amap = new AtomicReference<>();
        final AtomicReference<byte[]> alist = new AtomicReference<>();
        final AtomicReference<byte[]> azset = new AtomicReference<>();
        final AtomicReference<byte[]> aset = new AtomicReference<>();
        Replicator r = new RedisReplicator(DumpRdbVisitorTest.class.getClassLoader().getResourceAsStream("dump-huge-kv.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new DumpRdbVisitor(r));
        r.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof DumpKeyValuePair) {
                    DumpKeyValuePair dkv = (DumpKeyValuePair) event;
                    if (Strings.toString(dkv.getKey()).equals("k10")) {
                        amap.set(dkv.getValue());
                    } else if (Strings.toString(dkv.getKey()).equals("list10")) {
                        alist.set(dkv.getValue());
                    } else if (Strings.toString(dkv.getKey()).equals("zset")) {
                        azset.set(dkv.getValue());
                    } else if (Strings.toString(dkv.getKey()).equals("set")) {
                        aset.set(dkv.getValue());
                    } else if (Strings.toString(dkv.getKey()).equals("s")) {
                        astring.set(dkv.getValue());
                    }
                }
            }
        });
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }
        assertArrayEquals(string, astring.get());
        assertArrayEquals(set, aset.get());
        assertArrayEquals(zset, azset.get());
        assertArrayEquals(list, alist.get());
        assertArrayEquals(map, amap.get());
    }
    
    @Test
    public void test2() {
        String[] resources = new String[]{"dictionary.rdb",
                "easily_compressible_string_key.rdb", "empty_database.rdb",
                "hash_as_ziplist.rdb", "integer_keys.rdb", "intset_16.rdb",
                "intset_32.rdb", "intset_64.rdb", "keys_with_expiry.rdb",
                "linkedlist.rdb", "multiple_databases.rdb",
                "parser_filters.rdb", "rdb_version_5_with_checksum.rdb", "regular_set.rdb",
                "regular_sorted_set.rdb", "sorted_set_as_ziplist.rdb", "uncompressible_string_keys.rdb",
                "ziplist_that_compresses_easily.rdb", "ziplist_that_doesnt_compress.rdb",
                "ziplist_with_integers.rdb", "zipmap_that_compresses_easily.rdb",
                "zipmap_that_doesnt_compress.rdb", "zipmap_with_big_values.rdb",
                "rdb_version_8_with_64b_length_and_scores.rdb", "non_ascii_values.rdb", "dump-stream.rdb", "dump-module-2.rdb", "dumpV10.rdb", "dumpV11.rdb", "dump-ttlhash.rdb", "dump-slot.rdb"};
        for (String resource : resources) {
            template(resource);
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test3() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV7.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueList) {
                    KeyStringValueList kv = (KeyStringValueList) event;
                    if (kv.getValueRdbType() == RDB_TYPE_LIST_QUICKLIST) {
                        for (byte[] element : kv.getValue()) {
                            expected.add(element);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV7.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator, 6));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("mylist") && kv.getValueRdbType() == RDB_TYPE_LIST) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueList kv = (KeyStringValueList) event;
                            for (byte[] element : kv.getValue()) {
                                actual.add(element);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test4() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV8.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<ZSetEntry> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueZSet) {
                    KeyStringValueZSet kv = (KeyStringValueZSet) event;
                    String key = new String(kv.getKey());
                    if (key.equals("zadd") && kv.getValueRdbType() == RDB_TYPE_ZSET_2) {
                        for (ZSetEntry element : kv.getValue()) {
                            expected.add(element);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV8.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<ZSetEntry> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator, 6));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("zadd") && kv.getValueRdbType() == RDB_TYPE_ZSET) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueZSet kv = (KeyStringValueZSet) event;
                            for (ZSetEntry element : kv.getValue()) {
                                actual.add(element);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
    
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i).getElement(), actual.get(i).getElement());
            assertEquals(expected.get(i).getScore(), actual.get(i).getScore(), 0.000000001);
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test5() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV9.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueList) {
                    KeyStringValueList kv = (KeyStringValueList) event;
                    if (kv.getValueRdbType() == RDB_TYPE_LIST_QUICKLIST) {
                        for (byte[] element : kv.getValue()) {
                            expected.add(element);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV9.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator, 6));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("list1") && kv.getValueRdbType() == RDB_TYPE_LIST) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueList kv = (KeyStringValueList) event;
                            for (byte[] element : kv.getValue()) {
                                actual.add(element);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }
    }
    
    public void template(String filename) {
        try {
            @SuppressWarnings("resource")
            Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                    getClassLoader().getResourceAsStream(filename)
                    , FileType.RDB, Configuration.defaultSetting());
            replicator.setRdbVisitor(new DumpRdbVisitor(replicator));
            replicator.open();
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test6() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueList) {
                    KeyStringValueList kv = (KeyStringValueList) event;
                    String key = new String(kv.getKey());
                    if (key.equals("quicklist_listpack_plain") && kv.getValueRdbType() == RDB_TYPE_LIST_QUICKLIST_2) {
                        for (byte[] element : kv.getValue()) {
                            expected.add(element);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator, 6));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("quicklist_listpack_plain") && kv.getValueRdbType() == RDB_TYPE_LIST) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueList kv = (KeyStringValueList) event;
                            for (byte[] element : kv.getValue()) {
                                actual.add(element);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test7() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueList) {
                    KeyStringValueList kv = (KeyStringValueList) event;
                    String key = new String(kv.getKey());
                    if (key.equals("quicklist_listpack_pack") && kv.getValueRdbType() == RDB_TYPE_LIST_QUICKLIST_2) {
                        for (byte[] element : kv.getValue()) {
                            expected.add(element);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator, 6));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("quicklist_listpack_pack") && kv.getValueRdbType() == RDB_TYPE_LIST) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueList kv = (KeyStringValueList) event;
                            for (byte[] element : kv.getValue()) {
                                actual.add(element);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test8() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<ZSetEntry> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueZSet) {
                    KeyStringValueZSet kv = (KeyStringValueZSet) event;
                    String key = new String(kv.getKey());
                    if (key.equals("zset_listpack") && kv.getValueRdbType() == RDB_TYPE_ZSET_LISTPACK) {
                        for (ZSetEntry zset : kv.getValue()) {
                            expected.add(zset);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<ZSetEntry> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator, 6));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("zset_listpack") && kv.getValueRdbType() == RDB_TYPE_ZSET) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueZSet kv = (KeyStringValueZSet) event;
                            for (ZSetEntry zset : kv.getValue()) {
                                actual.add(zset);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i).getElement(), actual.get(i).getElement());
            assertEquals(expected.get(i).getScore(), actual.get(i).getScore(), 0.00001d);
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test9() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<Map.Entry<byte[], byte[]>> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueHash) {
                    KeyStringValueHash kv = (KeyStringValueHash) event;
                    String key = new String(kv.getKey());
                    if (key.equals("hash_listpack") && kv.getValueRdbType() == RDB_TYPE_HASH_LISTPACK) {
                        for (Map.Entry<byte[], byte[]> entry: kv.getValue().entrySet()) {
                            expected.add(entry);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<Map.Entry<byte[], byte[]>> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator, 6));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("hash_listpack") && kv.getValueRdbType() == RDB_TYPE_HASH) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueHash kv = (KeyStringValueHash) event;
                            for (Map.Entry<byte[], byte[]> entry: kv.getValue().entrySet()) {
                                actual.add(entry);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i).getKey(), actual.get(i).getKey());
            assertArrayEquals(expected.get(i).getValue(), actual.get(i).getValue());
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test10() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueList) {
                    KeyStringValueList kv = (KeyStringValueList) event;
                    String key = new String(kv.getKey());
                    if (key.equals("quicklist_listpack_plain") && kv.getValueRdbType() == RDB_TYPE_LIST_QUICKLIST_2) {
                        for (byte[] element : kv.getValue()) {
                            expected.add(element);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("quicklist_listpack_plain") && kv.getValueRdbType() == RDB_TYPE_LIST_QUICKLIST_2) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueList kv = (KeyStringValueList) event;
                            for (byte[] element : kv.getValue()) {
                                actual.add(element);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test11() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueList) {
                    KeyStringValueList kv = (KeyStringValueList) event;
                    String key = new String(kv.getKey());
                    if (key.equals("quicklist_listpack_pack") && kv.getValueRdbType() == RDB_TYPE_LIST_QUICKLIST_2) {
                        for (byte[] element : kv.getValue()) {
                            expected.add(element);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("quicklist_listpack_pack") && kv.getValueRdbType() == RDB_TYPE_LIST_QUICKLIST_2) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueList kv = (KeyStringValueList) event;
                            for (byte[] element : kv.getValue()) {
                                actual.add(element);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test12() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<ZSetEntry> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueZSet) {
                    KeyStringValueZSet kv = (KeyStringValueZSet) event;
                    String key = new String(kv.getKey());
                    if (key.equals("zset_listpack") && kv.getValueRdbType() == RDB_TYPE_ZSET_LISTPACK) {
                        for (ZSetEntry zset : kv.getValue()) {
                            expected.add(zset);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<ZSetEntry> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("zset_listpack") && kv.getValueRdbType() == RDB_TYPE_ZSET_LISTPACK) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueZSet kv = (KeyStringValueZSet) event;
                            for (ZSetEntry zset : kv.getValue()) {
                                actual.add(zset);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i).getElement(), actual.get(i).getElement());
            assertEquals(expected.get(i).getScore(), actual.get(i).getScore(), 0.00001d);
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test13() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<Map.Entry<byte[], byte[]>> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueHash) {
                    KeyStringValueHash kv = (KeyStringValueHash) event;
                    String key = new String(kv.getKey());
                    if (key.equals("hash_listpack") && kv.getValueRdbType() == RDB_TYPE_HASH_LISTPACK) {
                        for (Map.Entry<byte[], byte[]> entry: kv.getValue().entrySet()) {
                            expected.add(entry);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV10.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<Map.Entry<byte[], byte[]>> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("hash_listpack") && kv.getValueRdbType() == RDB_TYPE_HASH_LISTPACK) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueHash kv = (KeyStringValueHash) event;
                            for (Map.Entry<byte[], byte[]> entry: kv.getValue().entrySet()) {
                                actual.add(entry);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i).getKey(), actual.get(i).getKey());
            assertArrayEquals(expected.get(i).getValue(), actual.get(i).getValue());
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test14() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV11.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueSet) {
                    KeyStringValueSet kv = (KeyStringValueSet) event;
                    String key = new String(kv.getKey());
                    if (key.equals("setlistpack") && kv.getValueRdbType() == RDB_TYPE_SET_LISTPACK) {
                        for (byte[] entry: kv.getValue()) {
                            expected.add(entry);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV11.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("setlistpack") && kv.getValueRdbType() == RDB_TYPE_SET_LISTPACK) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueSet kv = (KeyStringValueSet) event;
                            for (byte[] entry: kv.getValue()) {
                                actual.add(entry);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }
    }
    
    @Test
    @SuppressWarnings("resource")
    public void test15() throws IOException {
        Replicator replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV11.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> expected = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueSet) {
                    KeyStringValueSet kv = (KeyStringValueSet) event;
                    String key = new String(kv.getKey());
                    if (key.equals("setlistpack") && kv.getValueRdbType() == RDB_TYPE_SET_LISTPACK) {
                        for (byte[] entry: kv.getValue()) {
                            expected.add(entry);
                        }
                    }
                }
            }
        });
        replicator.open();
        
        replicator = new RedisReplicator(DumpRdbVisitorTest.class.
                getClassLoader().getResourceAsStream("dumpV11.rdb")
                , FileType.RDB, Configuration.defaultSetting());
        List<byte[]> actual = new ArrayList<>();
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator, 6));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair kv = (DumpKeyValuePair) event;
                String key = new String(kv.getKey());
                if (key.equals("setlistpack") && kv.getValueRdbType() == RDB_TYPE_SET) {
                    DumpValueParser parser = new DefaultDumpValueParser(replicator);
                    parser.parse(kv, new EventListener() {
                        @Override
                        public void onEvent(Replicator replicator, Event event) {
                            KeyStringValueSet kv = (KeyStringValueSet) event;
                            for (byte[] entry: kv.getValue()) {
                                actual.add(entry);
                            }
                        }
                    });
                }
            }
        });
        replicator.open();
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }
    }
}