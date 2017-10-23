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

package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@SuppressWarnings("unchecked")
public class RdbV8ParserTest {
    @Test
    public void testParse() throws Exception {
        ConcurrentHashMap<String, KeyValuePair<?>> map = new ConcurrentHashMap<>();
        String[] resources = new String[]{"rdb_version_8_with_64b_length_and_scores.rdb", "non_ascii_values.rdb"};
        for (String resource : resources) {
            template(resource, map);
        }
        assertEquals("bar", map.get("foo").getValue());
        List<ZSetEntry> zset = new ArrayList<>(((Set<ZSetEntry>) map.get("bigset").getValue()));
        assertEquals(1000, zset.size());
        for (ZSetEntry entry : zset) {
            if (entry.getElement().equals("finalfield")) {
                assertEquals(2.718d, entry.getScore(), 0.0001);
            }
        }
    }

    @SuppressWarnings("resource")
    public void template(String filename, final ConcurrentHashMap<String, KeyValuePair<?>> map) {
        try {
            Replicator replicator = new RedisReplicator(RdbParserTest.class.
                    getClassLoader().getResourceAsStream(filename)
                    , FileType.RDB, Configuration.defaultSetting());
            replicator.addRdbListener(new RdbListener.Adaptor() {
                @Override
                public void handle(Replicator replicator, KeyValuePair<?> kv) {
                    map.put(kv.getKey(), kv);
                }
            });
            replicator.open();
        } catch (Exception e) {
            fail();
        }
    }
}
