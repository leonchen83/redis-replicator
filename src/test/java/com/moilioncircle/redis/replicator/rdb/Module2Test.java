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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.online.RedisSocketReplicatorTest;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueModule;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.dump.DumpRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.dump.datatype.DumpKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.skip.SkipRdbVisitor;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class Module2Test {

    @Test
    public void test() throws IOException {
        @SuppressWarnings("resource")
        Replicator replicator = new RedisReplicator(Module2Test.class.getClassLoader().getResourceAsStream("dump-module-2.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        replicator.addModuleParser("hellotype", 0, new ModuleTest.HelloTypeModuleParser());
        final List<long[]> list = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueModule) {
                    KeyStringValueModule kv = (KeyStringValueModule) event;
                    ModuleTest.HelloTypeModule module = (ModuleTest.HelloTypeModule) kv.getValue();
                    long[] value = module.getValue();
                    list.add(value);
                }
            }
        });

        replicator.open();
        assertEquals(1, list.size());
        assertEquals(-1025L, list.get(0)[0]);
        assertEquals(-1024L, list.get(0)[1]);
    }

    @Test
    public void testSkipModule() {
        @SuppressWarnings("resource")
        Replicator replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dump-module-2.rdb"), FileType.RDB,
                Configuration.defaultSetting());

        final Map<String, Module> map = new HashMap<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueModule) {
                    KeyStringValueModule kv = (KeyStringValueModule) event;
                    map.put(Strings.toString(kv.getKey()), kv.getValue());
                }
            }
        });
        try {
            replicator.open();
        } catch (Throwable e) {
            fail();
        }

        assertEquals(1, map.size());
        for (Map.Entry<String, Module> entry : map.entrySet()) {
            assertNull(entry.getValue());
        }
    }

    @Test
    public void testSkipModule1() {
        Replicator replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dump-module-2.rdb"), FileType.RDB,
                Configuration.defaultSetting());

        replicator.setRdbVisitor(new SkipRdbVisitor(replicator));
        try {
            replicator.open();
        } catch (Throwable e) {
            fail();
        }

        replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dump-module-2.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        replicator.addModuleParser("hellotype", 0, new ModuleTest.HelloTypeModuleParser());
        replicator.setRdbVisitor(new SkipRdbVisitor(replicator));
        try {
            replicator.open();
        } catch (Throwable e) {
            fail();
        }
    }

    @Test
    public void testDumpModule1() {
        {
            Replicator replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dump-module-2.rdb"), FileType.RDB,
                    Configuration.defaultSetting());
            replicator.setRdbVisitor(new DumpRdbVisitor(replicator));

            final Map<String, byte[]> map = new HashMap<>();
            replicator.addEventListener(new EventListener() {
                @Override
                public void onEvent(Replicator replicator, Event event) {
                    if (event instanceof DumpKeyValuePair) {
                        DumpKeyValuePair dkv = (DumpKeyValuePair) event;
                        map.put(Strings.toString(dkv.getKey()), dkv.getValue());
                    }
                }
            });
            try {
                replicator.open();
            } catch (Throwable e) {
                fail();
            }

            assertEquals(1, map.size());
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                assertNotNull(entry.getValue());
            }
        }

        {
            Replicator replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dump-module-2.rdb"), FileType.RDB,
                    Configuration.defaultSetting());
            replicator.addModuleParser("hellotype", 0, new ModuleTest.HelloTypeModuleParser());
            replicator.setRdbVisitor(new DumpRdbVisitor(replicator));

            final Map<String, byte[]> map = new HashMap<>();
            replicator.addEventListener(new EventListener() {
                @Override
                public void onEvent(Replicator replicator, Event event) {
                    if (event instanceof DumpKeyValuePair) {
                        DumpKeyValuePair dkv = (DumpKeyValuePair) event;
                        map.put(Strings.toString(dkv.getKey()), dkv.getValue());
                    }
                }
            });
            try {
                replicator.open();
            } catch (Throwable e) {
                fail();
            }

            assertEquals(1, map.size());
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                assertNotNull(entry.getValue());
            }
        }

    }
}
