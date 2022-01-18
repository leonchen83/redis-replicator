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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.RedisCodec;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.rdb.datatype.Function;
import com.moilioncircle.redis.replicator.rdb.dump.DumpRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.dump.datatype.DumpFunction;

/**
 * @author Leon Chen
 * @since 3.6.0
 */
public class FunctionTest {
    
    @Test
    public void test() throws IOException {
        @SuppressWarnings("resource")
        Replicator replicator = new RedisReplicator(FunctionTest.class.getClassLoader().getResourceAsStream("function.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        List<Function> functions = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof Function) {
                    functions.add((Function) event);
                }
            }
        });
        replicator.open();
        
        assertEquals(2, functions.size());
        {
            Function fn = functions.get(0);
            assertArrayEquals("lib2".getBytes(), fn.getName());
            assertArrayEquals("LUA".getBytes(), fn.getEngineName());
            assertNull(fn.getDescription());
            assertArrayEquals("local function test2() return 2 end redis.register_function('test2', test2)".getBytes(), fn.getCode());
        }
        
        {
            Function fn = functions.get(1);
            assertArrayEquals("lib1".getBytes(), fn.getName());
            assertArrayEquals("LUA".getBytes(), fn.getEngineName());
            assertArrayEquals("description function".getBytes(), fn.getDescription());
            assertArrayEquals("local function test1() return 1 end redis.register_function('test1', test1)".getBytes(), fn.getCode());
        }
    }
    
    @Test
    public void test1() throws IOException {
        @SuppressWarnings("resource")
        Replicator replicator = new RedisReplicator(FunctionTest.class.getClassLoader().getResourceAsStream("function.rdb"), FileType.RDB,
                Configuration.defaultSetting());
        replicator.setRdbVisitor(new DumpRdbVisitor(replicator));
        List<String> results = new ArrayList<>();
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof DumpFunction) {
                    DumpFunction function = (DumpFunction) event;
                    byte[] bytes = function.getSerialized();
                    RedisCodec codec = new RedisCodec();
                    byte[] encoded = codec.encode(bytes);
                    results.add(new String(encoded));
                }
            }
        });
        replicator.open();
        assertEquals("\\xf6\\x04lib2\\x03LUA\\x00\\xc3@D@K\\x1flocal\\x20function\\x20test2()\\x20return\\x202\\x20\\x02end\\x20\\x0c\\x0cdis.register_\\xc0,\\x01('`-\\x01',`5\\x012)\\n\\x00\\x827\\x9e\\x84$\\x91\\x84\\x19", results.get(0));
        assertEquals("\\xf6\\x04lib1\\x03LUA\\x01\\x14description\\x20function\\xc3@D@K\\x1flocal\\x20function\\x20test1()\\x20return\\x201\\x20\\x02end\\x20\\x0c\\x0cdis.register_\\xc0,\\x01('`-\\x01',`5\\x011)\\n\\x007\\xb7q\\xdb\\xb1i\\x18\\xd2", results.get(1));
    }
}
