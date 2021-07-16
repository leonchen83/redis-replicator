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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.online.RedisSocketReplicatorTest;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueModule;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.dump.DumpRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.module.DefaultRdbModuleParser;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;
import com.moilioncircle.redis.replicator.rdb.skip.SkipRdbVisitor;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ModuleTest {
    @Test
    public void testModule() throws IOException {
        @SuppressWarnings("resource")
        Replicator replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("module.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        replicator.addModuleParser("hellotype", 0, new HelloTypeModuleParser());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueModule) {
                    KeyStringValueModule kv = (KeyStringValueModule) event;
                    assertEquals(12123123112L, ((HelloTypeModule) kv.getValue()).getValue()[0]);
                }
            }
        });

        replicator.open();

        replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly6.aof"), FileType.AOF,
                Configuration.defaultSetting());
        replicator.addCommandParser(CommandName.name("hellotype.insert"), new HelloTypeParser());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof HelloTypeCommand) {
                    HelloTypeCommand htc = (HelloTypeCommand) event;
                    assertEquals(12123123112L, htc.getValue());
                }
            }
        });

        replicator.open();
    }

    @Test
    public void testSkipModule() {
        @SuppressWarnings("resource")
        Replicator replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("module.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        try {
            replicator.open();
            fail();
        } catch (Throwable e) {
        }
    }

    @Test
    public void testSkipModule1() {
        Replicator replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("module.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        replicator.setRdbVisitor(new SkipRdbVisitor(replicator));

        try {
            replicator.open();
            fail();
        } catch (Throwable e) {
        }

        replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("module.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        replicator.addModuleParser("hellotype", 0, new HelloTypeModuleParser());
        replicator.setRdbVisitor(new SkipRdbVisitor(replicator));

        try {
            replicator.open();
        } catch (Throwable e) {
            fail();
        }
    }

    @Test
    public void testDumpModule1() {
        Replicator replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("module.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator));

        try {
            replicator.open();
            fail();
        } catch (Throwable e) {
        }

        replicator = new RedisReplicator(RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("module.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        replicator.addModuleParser("hellotype", 0, new HelloTypeModuleParser());
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator));

        final Map<String, byte[]> map = new HashMap<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyValuePair) {
                    @SuppressWarnings("unchecked")
                    KeyValuePair<byte[], byte[]> dkv = (KeyValuePair<byte[], byte[]>) event;
                    map.put(Strings.toString(dkv.getKey()), dkv.getValue());
                }
            }
        });
        try {
            replicator.open();
        } catch (Throwable e) {
            fail();
        }

        Assertions.assertEquals(27, map.size());
        for (Map.Entry<String, byte[]> entry : map.entrySet()) {
            assertNotNull(entry.getValue());
        }
    }

    public static class HelloTypeModuleParser implements ModuleParser<HelloTypeModule> {

        @Override
        public HelloTypeModule parse(RedisInputStream in, int version) throws IOException {
            DefaultRdbModuleParser parser = new DefaultRdbModuleParser(in);
            int elements = parser.loadUnsigned(version).intValue();
            long[] ary = new long[elements];
            int i = 0;
            while (elements-- > 0) {
                ary[i++] = parser.loadSigned(version);
            }
            return new HelloTypeModule(ary);
        }
    }

    public static class HelloTypeModule implements Module {

        private static final long serialVersionUID = 1L;

        private final long[] value;

        public HelloTypeModule(long[] value) {
            this.value = value;
        }

        public long[] getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "HelloTypeModule{" +
                    "value=" + Arrays.toString(value) +
                    '}';
        }
    }

    public static class HelloTypeParser implements CommandParser<HelloTypeCommand> {
        @Override
        public HelloTypeCommand parse(Object[] command) {
            String key = Strings.toString(command[1]);
            long value = Long.parseLong(Strings.toString(command[2]));
            return new HelloTypeCommand(key, value);
        }
    }

    public static class HelloTypeCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final String key;
        private final long value;

        public long getValue() {
            return value;
        }

        public String getKey() {
            return key;
        }

        public HelloTypeCommand(String key, long value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "HelloTypeCommand{" +
                    "key='" + key + '\'' +
                    ", value=" + value +
                    '}';
        }

    }
}
