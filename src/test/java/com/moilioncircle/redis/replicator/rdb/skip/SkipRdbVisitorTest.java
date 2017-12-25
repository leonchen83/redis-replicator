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

package com.moilioncircle.redis.replicator.rdb.skip;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.ModuleTest;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.ValueIterableRdbParserTest;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Leon Chen
 * @since 2.4.6
 */
public class SkipRdbVisitorTest {
    @Test
    public void test() {
        String[] resources = new String[]{"dictionary.rdb",
                "easily_compressible_string_key.rdb", "empty_database.rdb",
                "hash_as_ziplist.rdb", "integer_keys.rdb", "intset_16.rdb",
                "intset_32.rdb", "intset_64.rdb", "keys_with_expiry.rdb",
                "linkedlist.rdb", "multiple_databases.rdb",
                "parser_filters.rdb", "rdb_version_5_with_checksum.rdb", "regular_set.rdb",
                "regular_sorted_set.rdb", "sorted_set_as_ziplist.rdb", "uncompressible_string_keys.rdb",
                "ziplist_that_compresses_easily.rdb", "ziplist_that_doesnt_compress.rdb",
                "ziplist_with_integers.rdb", "zipmap_that_compresses_easily.rdb",
                "zipmap_that_doesnt_compress.rdb", "zipmap_with_big_values.rdb", "rdb_version_8_with_64b_length_and_scores.rdb", "non_ascii_values.rdb", "binarydump.rdb", "module.rdb"};
        for (String file : resources) {
            testFile(file);
        }
    }

    private void testFile(String fileName) {
        final AtomicInteger acc = new AtomicInteger(0);
        final AtomicLong count = new AtomicLong(0);
        CountInputStream in = new CountInputStream(ValueIterableRdbParserTest.class.getClassLoader().getResourceAsStream(fileName));
        Replicator r = new RedisReplicator(in, FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new SkipRdbVisitor(r));
        r.addModuleParser("hellotype", 0, new ModuleTest.HelloTypeModuleParser());
        final RawByteListener rawByteListener = new RawByteListener() {
            @Override
            public void handle(byte... rawBytes) {
                count.getAndAdd(rawBytes.length);
            }
        };
        r.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void preFullSync(Replicator replicator) {
                acc.incrementAndGet();
                replicator.addRawByteListener(rawByteListener);
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                acc.incrementAndGet();
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                acc.incrementAndGet();
                replicator.removeRawByteListener(rawByteListener);
            }
        });
        try {
            r.open();
            assertEquals(2, acc.get());
            assertEquals(in.count, count.get());
        } catch (Exception e) {
            fail();
        }
    }

    private static class CountInputStream extends InputStream {
        private long count;
        private InputStream in;

        private CountInputStream(InputStream in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            int i = in.read();
            if (i > 0) {
                count++;
            }
            return i;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int i = in.read(b, off, len);
            if (i > 0) {
                count += i;
            }
            return i;
        }

        @Override
        public long skip(long n) throws IOException {
            long i = in.skip(n);
            if (i > 0) {
                count += i;
            }
            return i;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }
}