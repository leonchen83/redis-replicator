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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.client.RESP2Client;
import com.moilioncircle.redis.replicator.cmd.impl.CompareType;
import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.HPExpireAtCommand;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * Online tests for hash field expiry commands: HEXPIRE, HPEXPIRE, HEXPIREAT.
 *
 * <p>Valkey always propagates hash field expiry commands to replicas as
 * HPEXPIREAT (absolute millisecond timestamp), regardless of which variant
 * was originally issued. These tests verify that the propagated HPEXPIREAT
 * command is parsed correctly, with the original option flags (NX/XX/GT/LT)
 * preserved.
 *
 * <p>Requires a running Valkey 9+ server at {@code 127.0.0.1:6379}.
 * Run with {@code -Dtest.flavor=valkey} to target a Valkey server.
 *
 * @author otheng03
 * @since 3.12.0
 */
@EnabledIfValkey
public class HashFieldExpireTest extends OnlineTestBase {

    /**
     * HEXPIRE (relative seconds, NX) is propagated as HPEXPIREAT with ExistType.NX.
     */
    @Test
    public void testHExpire() throws Exception {
        final AtomicReference<HPExpireAtCommand> ref = new AtomicReference<>(null);
        Replicator replicator = new RedisReplicator(HOST, PORT, config());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    try (RESP2Client client = new RESP2Client(HOST, PORT, config())) {
                        RESP2Client.Command cmd = client.newCommand();
                        cmd.invoke("DEL", "hexpire_test");
                        cmd.invoke("HSET", "hexpire_test", "f1", "v1", "f2", "v2");
                        cmd.invoke("HEXPIRE", "hexpire_test", "300", "NX", "FIELDS", "1", "f1");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if (event instanceof HPExpireAtCommand) {
                    HPExpireAtCommand cmd = (HPExpireAtCommand) event;
                    if (!"hexpire_test".equals(Strings.toString(cmd.getKey()))) return;
                    ref.compareAndSet(null, cmd);
                    try {
                        replicator.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        replicator.open();

        HPExpireAtCommand cmd = ref.get();
        assertNotNull(cmd);
        assertEquals("hexpire_test", Strings.toString(cmd.getKey()));
        assertTrue(cmd.getEx() > System.currentTimeMillis());
        assertEquals(1, cmd.getFields().length);
        assertEquals("f1", Strings.toString(cmd.getFields()[0]));
        assertEquals(ExistType.NX, cmd.getExistType());
        assertEquals(CompareType.NONE, cmd.getCompareType());
    }

    /**
     * HPEXPIRE (relative milliseconds, GT) is propagated as HPEXPIREAT with CompareType.GT.
     *
     * GT requires an existing expiry to compare against, so we first set one with NX.
     * Both commands arrive as HPEXPIREAT; we filter for the GT one.
     */
    @Test
    public void testHPExpire() throws Exception {
        final AtomicReference<HPExpireAtCommand> ref = new AtomicReference<>(null);
        Replicator replicator = new RedisReplicator(HOST, PORT, config());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    try (RESP2Client client = new RESP2Client(HOST, PORT, config())) {
                        RESP2Client.Command cmd = client.newCommand();
                        cmd.invoke("DEL", "hpexpire_test");
                        cmd.invoke("HSET", "hpexpire_test", "f1", "v1");
                        // NX sets an initial expiry; GT then succeeds because 300000 > 100000
                        cmd.invoke("HPEXPIRE", "hpexpire_test", "100000", "NX", "FIELDS", "1", "f1");
                        cmd.invoke("HPEXPIRE", "hpexpire_test", "300000", "GT", "FIELDS", "1", "f1");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if (event instanceof HPExpireAtCommand) {
                    HPExpireAtCommand cmd = (HPExpireAtCommand) event;
                    if (!"hpexpire_test".equals(Strings.toString(cmd.getKey()))) return;
                    if (cmd.getCompareType() != CompareType.GT) return;
                    ref.compareAndSet(null, cmd);
                    try {
                        replicator.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        replicator.open();

        HPExpireAtCommand cmd = ref.get();
        assertNotNull(cmd);
        assertEquals("hpexpire_test", Strings.toString(cmd.getKey()));
        assertTrue(cmd.getEx() > System.currentTimeMillis());
        assertEquals(1, cmd.getFields().length);
        assertEquals("f1", Strings.toString(cmd.getFields()[0]));
        assertEquals(ExistType.NONE, cmd.getExistType());
        assertEquals(CompareType.GT, cmd.getCompareType());
    }

    /**
     * HEXPIREAT (absolute seconds, LT) is propagated as HPEXPIREAT with CompareType.LT.
     */
    @Test
    public void testHExpireAt() throws Exception {
        final AtomicReference<HPExpireAtCommand> ref = new AtomicReference<>(null);
        Replicator replicator = new RedisReplicator(HOST, PORT, config());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PostRdbSyncEvent) {
                    try (RESP2Client client = new RESP2Client(HOST, PORT, config())) {
                        long futureSeconds = System.currentTimeMillis() / 1000 + 3600;
                        RESP2Client.Command cmd = client.newCommand();
                        cmd.invoke("DEL", "hexpireat_test");
                        cmd.invoke("HSET", "hexpireat_test", "f1", "v1", "f2", "v2");
                        cmd.invoke("HEXPIREAT", "hexpireat_test", String.valueOf(futureSeconds),
                                "LT", "FIELDS", "2", "f1", "f2");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if (event instanceof HPExpireAtCommand) {
                    HPExpireAtCommand cmd = (HPExpireAtCommand) event;
                    if (!"hexpireat_test".equals(Strings.toString(cmd.getKey()))) return;
                    ref.compareAndSet(null, cmd);
                    try {
                        replicator.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        replicator.open();

        HPExpireAtCommand cmd = ref.get();
        assertNotNull(cmd);
        assertEquals("hexpireat_test", Strings.toString(cmd.getKey()));
        assertTrue(cmd.getEx() > System.currentTimeMillis());
        assertEquals(2, cmd.getFields().length);
        assertEquals(ExistType.NONE, cmd.getExistType());
        assertEquals(CompareType.LT, cmd.getCompareType());
    }
}
