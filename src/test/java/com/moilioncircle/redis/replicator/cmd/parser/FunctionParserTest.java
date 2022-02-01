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

package com.moilioncircle.redis.replicator.cmd.parser;

import static junit.framework.TestCase.assertNull;

import org.junit.Test;

import com.moilioncircle.redis.replicator.cmd.impl.FunctionDeleteCommand;
import com.moilioncircle.redis.replicator.cmd.impl.FunctionFlushCommand;
import com.moilioncircle.redis.replicator.cmd.impl.FunctionLoadCommand;
import com.moilioncircle.redis.replicator.cmd.impl.FunctionRestoreCommand;

/**
 * @author Leon Chen
 * @since 3.6.0
 */
public class FunctionParserTest extends AbstractParserTest {
    @Test
    public void test() {
        {
            FunctionParser parser = new FunctionParser();
            FunctionFlushCommand cmd = (FunctionFlushCommand)parser.parse(toObjectArray(new Object[]{"function", "flush", "sync"}));
            assertEquals(false, cmd.isAsync());
            assertEquals(true, cmd.isSync());
        }
    
        {
            FunctionParser parser = new FunctionParser();
            FunctionFlushCommand cmd = (FunctionFlushCommand)parser.parse(toObjectArray(new Object[]{"function", "flush", "async"}));
            assertEquals(true, cmd.isAsync());
            assertEquals(false, cmd.isSync());
        }
    
        {
            FunctionParser parser = new FunctionParser();
            FunctionFlushCommand cmd = (FunctionFlushCommand)parser.parse(toObjectArray(new Object[]{"function", "flush"}));
            assertEquals(false, cmd.isAsync());
            assertEquals(false, cmd.isSync());
        }
    
        {
            FunctionParser parser = new FunctionParser();
            FunctionDeleteCommand cmd = (FunctionDeleteCommand)parser.parse(toObjectArray(new Object[]{"function", "delete", "myfunc"}));
            assertEquals("myfunc", cmd.getLibraryName());
        }
        
        {
            FunctionParser parser = new FunctionParser();
            FunctionRestoreCommand cmd = (FunctionRestoreCommand)parser.parse(toObjectArray(new Object[]{"function", "restore", "\\xf6\\x05mylib\\x03LUA\\x00\\xc3@D@J\\x1aredis.register_function('my@\\x0b\\x02', @\\x06`\\x12\\x11keys, args) return`\\x0c\\a[1] end)\\n\\x00@\\n)\\x11\\xc8|\\x9b\\xe4"}));
            assertEquals("\\xf6\\x05mylib\\x03LUA\\x00\\xc3@D@J\\x1aredis.register_function('my@\\x0b\\x02', @\\x06`\\x12\\x11keys, args) return`\\x0c\\a[1] end)\\n\\x00@\\n)\\x11\\xc8|\\x9b\\xe4", cmd.getSerializedValue());
            assertEquals(false, cmd.isReplace());
            assertEquals(false, cmd.isAppend());
            assertEquals(false, cmd.isFlush());
        }
    
        {
            FunctionParser parser = new FunctionParser();
            FunctionRestoreCommand cmd = (FunctionRestoreCommand)parser.parse(toObjectArray(new Object[]{"function", "restore", "\\xf6\\x05mylib\\x03LUA\\x00\\xc3@D@J\\x1aredis.register_function('my@\\x0b\\x02', @\\x06`\\x12\\x11keys, args) return`\\x0c\\a[1] end)\\n\\x00@\\n)\\x11\\xc8|\\x9b\\xe4", "append"}));
            assertEquals("\\xf6\\x05mylib\\x03LUA\\x00\\xc3@D@J\\x1aredis.register_function('my@\\x0b\\x02', @\\x06`\\x12\\x11keys, args) return`\\x0c\\a[1] end)\\n\\x00@\\n)\\x11\\xc8|\\x9b\\xe4", cmd.getSerializedValue());
            assertEquals(false, cmd.isReplace());
            assertEquals(true, cmd.isAppend());
            assertEquals(false, cmd.isFlush());
        }
    
        {
            FunctionParser parser = new FunctionParser();
            FunctionRestoreCommand cmd = (FunctionRestoreCommand)parser.parse(toObjectArray(new Object[]{"function", "restore", "\\xf6\\x05mylib\\x03LUA\\x00\\xc3@D@J\\x1aredis.register_function('my@\\x0b\\x02', @\\x06`\\x12\\x11keys, args) return`\\x0c\\a[1] end)\\n\\x00@\\n)\\x11\\xc8|\\x9b\\xe4", "replace"}));
            assertEquals("\\xf6\\x05mylib\\x03LUA\\x00\\xc3@D@J\\x1aredis.register_function('my@\\x0b\\x02', @\\x06`\\x12\\x11keys, args) return`\\x0c\\a[1] end)\\n\\x00@\\n)\\x11\\xc8|\\x9b\\xe4", cmd.getSerializedValue());
            assertEquals(true, cmd.isReplace());
            assertEquals(false, cmd.isAppend());
            assertEquals(false, cmd.isFlush());
        }
    
        {
            FunctionParser parser = new FunctionParser();
            FunctionRestoreCommand cmd = (FunctionRestoreCommand)parser.parse(toObjectArray(new Object[]{"function", "restore", "\\xf6\\x05mylib\\x03LUA\\x00\\xc3@D@J\\x1aredis.register_function('my@\\x0b\\x02', @\\x06`\\x12\\x11keys, args) return`\\x0c\\a[1] end)\\n\\x00@\\n)\\x11\\xc8|\\x9b\\xe4", "flush"}));
            assertEquals("\\xf6\\x05mylib\\x03LUA\\x00\\xc3@D@J\\x1aredis.register_function('my@\\x0b\\x02', @\\x06`\\x12\\x11keys, args) return`\\x0c\\a[1] end)\\n\\x00@\\n)\\x11\\xc8|\\x9b\\xe4", cmd.getSerializedValue());
            assertEquals(false, cmd.isReplace());
            assertEquals(false, cmd.isAppend());
            assertEquals(true, cmd.isFlush());
        }
    
        {
            FunctionParser parser = new FunctionParser();
            FunctionLoadCommand cmd = (FunctionLoadCommand)parser.parse(toObjectArray(new Object[]{"function", "load", "Lua", "mylib", "redis.register_function('myfunc', function(keys, args) return args[1] end)"}));
            assertEquals("Lua", cmd.getEngineName());
            assertEquals("mylib", cmd.getLibraryName());
            assertEquals("redis.register_function('myfunc', function(keys, args) return args[1] end)", cmd.getFunctionCode());
            assertEquals(false, cmd.isReplace());
            assertNull(cmd.getDescription());
        }
    
        {
            FunctionParser parser = new FunctionParser();
            FunctionLoadCommand cmd = (FunctionLoadCommand)parser.parse(toObjectArray(new Object[]{"function", "load", "Lua", "mylib", "redis.register_function('myfunc', function(keys, args) return args[1] end)", "replace"}));
            assertEquals("Lua", cmd.getEngineName());
            assertEquals("mylib", cmd.getLibraryName());
            assertEquals("redis.register_function('myfunc', function(keys, args) return args[1] end)", cmd.getFunctionCode());
            assertEquals(true, cmd.isReplace());
            assertNull(cmd.getDescription());
        }
    
        {
            FunctionParser parser = new FunctionParser();
            FunctionLoadCommand cmd = (FunctionLoadCommand)parser.parse(toObjectArray(new Object[]{"function", "load", "Lua", "mylib", "redis.register_function('myfunc', function(keys, args) return args[1] end)", "replace", "description", "desc"}));
            assertEquals("Lua", cmd.getEngineName());
            assertEquals("mylib", cmd.getLibraryName());
            assertEquals("redis.register_function('myfunc', function(keys, args) return args[1] end)", cmd.getFunctionCode());
            assertEquals(true, cmd.isReplace());
            assertEquals("desc", cmd.getDescription());
        }
    
        {
            FunctionParser parser = new FunctionParser();
            FunctionLoadCommand cmd = (FunctionLoadCommand)parser.parse(toObjectArray(new Object[]{"function", "load", "Lua", "mylib", "description", "desc", "redis.register_function('myfunc', function(keys, args) return args[1] end)", "replace"}));
            assertEquals("Lua", cmd.getEngineName());
            assertEquals("mylib", cmd.getLibraryName());
            assertEquals("redis.register_function('myfunc', function(keys, args) return args[1] end)", cmd.getFunctionCode());
            assertEquals(true, cmd.isReplace());
            assertEquals("desc", cmd.getDescription());
        }
    }
}
