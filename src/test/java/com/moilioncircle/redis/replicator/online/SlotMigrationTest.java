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

package com.moilioncircle.redis.replicator.online;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.client.RESP2;
import com.moilioncircle.redis.replicator.client.RESP2Client;
import com.moilioncircle.redis.replicator.cmd.impl.DelCommand;
import com.moilioncircle.redis.replicator.cmd.impl.UnLinkCommand;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * Full sync from a Valkey 9 primary while it is importing a slot (atomic slot migration).
 * The RDB it sends contains RDB_OPCODE_SLOT_IMPORT, and the replicator must parse past it.
 * Cancelling the migration on the source fails the import on the target, and the target's
 * cleanup must then reach the replicator as a deletion of the imported key.
 *
 * Requires the 2-node cluster from {@code .github/scripts/valkey-cluster.sh up} and
 * {@code -Dtest.flavor=valkey -Dtest.valkey.cluster=true}.
 *
 * @since 3.12.0
 */
@EnabledIfValkey
public class SlotMigrationTest extends OnlineTestBase {

    private static final int SOURCE_PORT = 7379;
    private static final int TARGET_PORT = 7380;
    private static final String KEY = "slot_migration_test";

    @Before
    public void setUp() {
        Assume.assumeTrue("skipped: requires -Dtest.valkey.cluster=true", Boolean.getBoolean("test.valkey.cluster"));
    }

    @After
    public void tearDown() {
        if (!Boolean.getBoolean("test.valkey.cluster")) return;
        // only the source can cancel, and "No migrations ongoing" is expected when the test already did
        invokeQuietly(SOURCE_PORT, "CLUSTER", "CANCELSLOTMIGRATIONS");
        invokeQuietly(TARGET_PORT, "DEBUG", "SLOTMIGRATION", "PREVENT-FAILOVER", "0");
    }

    @Test(timeout = 60000)
    public void testFullSyncDuringSlotImport() throws Exception {
        invoke(SOURCE_PORT, "SET", KEY, "v");
        String slot = String.valueOf(invoke(SOURCE_PORT, "CLUSTER", "KEYSLOT", KEY).getNumber());
        String target = invoke(TARGET_PORT, "CLUSTER", "MYID").getString();
        invoke(TARGET_PORT, "DEBUG", "SLOTMIGRATION", "PREVENT-FAILOVER", "1");
        invoke(SOURCE_PORT, "CLUSTER", "MIGRATESLOTS", "SLOTSRANGE", slot, slot, "NODE", target);
        awaitImportHeld();

        AtomicBoolean imported = new AtomicBoolean();
        AtomicBoolean rdbDone = new AtomicBoolean();
        AtomicBoolean deleted = new AtomicBoolean();
        Replicator replicator = new RedisReplicator(HOST, TARGET_PORT, config());
        replicator.addEventListener((r, event) -> {
            if (event instanceof KeyStringValueString && KEY.equals(Strings.toString(((KeyStringValueString) event).getKey()))) {
                imported.set(true);
            }
            if (event instanceof PostRdbSyncEvent) {
                rdbDone.set(true);
                try {
                    invoke(SOURCE_PORT, "CLUSTER", "CANCELSLOTMIGRATIONS");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (deletes(event)) {
                deleted.set(true);
                try {
                    r.close();
                } catch (IOException ignored) {
                }
            }
        });
        replicator.open();

        assertTrue("importing key was not in the full sync RDB", imported.get());
        assertTrue("full sync did not finish", rdbDone.get());
        assertTrue("cancelled import did not propagate a deletion", deleted.get());
    }

    private static boolean deletes(Event event) {
        byte[][] keys = null;
        if (event instanceof DelCommand) keys = ((DelCommand) event).getKeys();
        if (event instanceof UnLinkCommand) keys = ((UnLinkCommand) event).getKeys();
        if (keys == null) return false;
        for (byte[] key : keys) {
            if (KEY.equals(Strings.toString(key))) return true;
        }
        return false;
    }

    private void awaitImportHeld() throws IOException, InterruptedException {
        for (int i = 0; i < 100; i++) {
            if (flatten(invoke(TARGET_PORT, "CLUSTER", "GETSLOTMIGRATIONS")).contains("failover-granted")) return;
            Thread.sleep(100);
        }
        fail("slot import did not reach failover-granted");
    }

    private static String flatten(RESP2.Node node) {
        if (node.getArray() == null) return String.valueOf(node.getString());
        StringBuilder builder = new StringBuilder();
        for (RESP2.Node child : node.getArray()) builder.append(flatten(child)).append(' ');
        return builder.toString();
    }

    private void invokeQuietly(int port, String... command) {
        try {
            invoke(port, command);
        } catch (IOException ignored) {
        }
    }

    private RESP2.Node invoke(int port, String... command) throws IOException {
        try (RESP2Client client = new RESP2Client(HOST, port, config())) {
            RESP2.Node reply = client.newCommand().invoke(command);
            if (reply.getError() != null) throw new IOException(String.join(" ", command) + ": " + reply.getError());
            return reply;
        }
    }
}
