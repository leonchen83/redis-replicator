/*
 * Copyright 2026 otheng03
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.Flavor;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.skip.SkipRdbVisitor;

/**
 * @since 3.12.0
 */
public class SlotImportTest {

    // dump-slot-import.rdb was saved by a valkey 9.0.3 cluster primary in the middle of an atomic slot migration:
    //   source: SET k v
    //   target: DEBUG SLOTMIGRATION PREVENT-FAILOVER 1
    //   source: CLUSTER MIGRATESLOTS SLOTSRANGE 7629 7629 NODE <target-id>
    //   target: SAVE
    private static List<Event> parse(boolean skip) throws IOException {
        Replicator replicator = new RedisReplicator(SlotImportTest.class.getClassLoader().getResourceAsStream("dump-slot-import.rdb"),
                FileType.RDB, Configuration.defaultSetting().setFlavor(Flavor.VALKEY));
        if (skip) replicator.setRdbVisitor(new SkipRdbVisitor(replicator));
        List<Event> events = new ArrayList<>();
        replicator.addEventListener((r, event) -> events.add(event));
        replicator.open();
        return events;
    }

    @Test
    public void testDefaultRdbVisitor() throws IOException {
        List<Event> events = parse(false);
        KeyStringValueString kv = (KeyStringValueString) events.stream()
                .filter(e -> e instanceof KeyStringValueString).findFirst().get();
        assertEquals("k", new String(kv.getKey()));
        assertEquals("v", new String(kv.getValue()));
        assertTrue(events.get(events.size() - 1) instanceof PostRdbSyncEvent);
    }

    @Test
    public void testSkipRdbVisitor() throws IOException {
        List<Event> events = parse(true);
        assertTrue(events.get(events.size() - 1) instanceof PostRdbSyncEvent);
    }

    @Test
    public void testRedisFlavorRejectsOpcode243() throws IOException {
        // redis 8.6+ uses 243 for RDB_OPCODE_KEY_META, so it must never be parsed as a slot import
        byte[] rdb = {'R', 'E', 'D', 'I', 'S', '0', '0', '1', '2', (byte) 243};
        Replicator replicator = new RedisReplicator(new ByteArrayInputStream(rdb), FileType.RDB, Configuration.defaultSetting());
        try {
            replicator.open();
            fail("expected opcode 243 to be rejected for redis");
        } catch (AssertionError e) {
            assertTrue(e.getMessage(), e.getMessage().startsWith("unexpected value type:243"));
        }
    }
}
