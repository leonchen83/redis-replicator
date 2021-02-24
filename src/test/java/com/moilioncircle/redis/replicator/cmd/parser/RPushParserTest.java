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

package com.moilioncircle.redis.replicator.cmd.parser;

import static junit.framework.TestCase.assertNull;

import com.moilioncircle.redis.replicator.cmd.impl.LPopCommand;
import com.moilioncircle.redis.replicator.cmd.impl.LPushCommand;
import com.moilioncircle.redis.replicator.cmd.impl.LPushXCommand;
import com.moilioncircle.redis.replicator.cmd.impl.RPopCommand;
import com.moilioncircle.redis.replicator.cmd.impl.RPushCommand;
import com.moilioncircle.redis.replicator.cmd.impl.RPushXCommand;
import org.junit.Test;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class RPushParserTest extends AbstractParserTest {
    @Test
    public void parse() {
        {
            RPushParser parser = new RPushParser();
            RPushCommand cmd = parser.parse(toObjectArray("rpush key v1 v2".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("v1", cmd.getValues()[0]);
            assertEquals("v2", cmd.getValues()[1]);
        }

        {
            RPushXParser parser = new RPushXParser();
            RPushXCommand cmd = parser.parse(toObjectArray("rpushx key v1 v2".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("v1", cmd.getValues()[0]);
            assertEquals("v2", cmd.getValues()[1]);
        }

        {
            LPushParser parser = new LPushParser();
            LPushCommand cmd = parser.parse(toObjectArray("lpush key v1 v2".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("v1", cmd.getValues()[0]);
            assertEquals("v2", cmd.getValues()[1]);
        }

        {
            LPushXParser parser = new LPushXParser();
            LPushXCommand cmd = parser.parse(toObjectArray("lpushx key v1 v2".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("v1", cmd.getValues()[0]);
            assertEquals("v2", cmd.getValues()[1]);
        }

        {
            LPopParser parser = new LPopParser();
            LPopCommand cmd = parser.parse(toObjectArray("lpop key".split(" ")));
            assertEquals("key", cmd.getKey());
            assertNull(cmd.getCount());
        }

        {
            RPopParser parser = new RPopParser();
            RPopCommand cmd = parser.parse(toObjectArray("rpop key".split(" ")));
            assertEquals("key", cmd.getKey());
            assertNull(cmd.getCount());
        }
    
        {
            LPopParser parser = new LPopParser();
            LPopCommand cmd = parser.parse(toObjectArray("lpop key 5".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(5, cmd.getCount().getCount());
        }
    
        {
            RPopParser parser = new RPopParser();
            RPopCommand cmd = parser.parse(toObjectArray("rpop key 5".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(5, cmd.getCount().getCount());
        }
    }

}