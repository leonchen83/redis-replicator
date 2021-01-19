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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.rdb.dump.datatype.DumpKeyValuePair;
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
                "rdb_version_8_with_64b_length_and_scores.rdb", "non_ascii_values.rdb", "dump-stream.rdb", "dump-module-2.rdb"};
        for (String resource : resources) {
            template(resource);
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
}