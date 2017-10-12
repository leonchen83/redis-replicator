/*
 * Copyright 2016 leon chen
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

import com.moilioncircle.redis.replicator.cmd.impl.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class DecrByParserTest extends AbstractParserTest {
    @Test
    public void parse() throws Exception {
        {
            DecrByParser parser = new DecrByParser();
            DecrByCommand cmd = parser.parse(toObjectArray("decrby key 5".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(5, cmd.getValue());
            System.out.println(cmd);
        }

        {
            IncrByParser parser = new IncrByParser();
            IncrByCommand cmd = parser.parse(toObjectArray("incrby key 5".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(5, cmd.getValue());
            System.out.println(cmd);
        }

        {
            DecrParser parser = new DecrParser();
            DecrCommand cmd = parser.parse(toObjectArray("decr key".split(" ")));
            assertEquals("key", cmd.getKey());
            System.out.println(cmd);
        }

        {
            IncrParser parser = new IncrParser();
            IncrCommand cmd = parser.parse(toObjectArray("incr key".split(" ")));
            assertEquals("key", cmd.getKey());
            System.out.println(cmd);
        }

        {
            ZIncrByParser parser = new ZIncrByParser();
            ZIncrByCommand cmd = parser.parse(toObjectArray("zincrby key 5 mem".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(5, cmd.getIncrement(), 0);
            assertEquals("mem", cmd.getMember());
            System.out.println(cmd);
        }

        {
            HIncrByParser parser = new HIncrByParser();
            HIncrByCommand cmd = parser.parse(toObjectArray("hincrby key mem 5".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(5, cmd.getIncrement());
            assertEquals("mem", cmd.getField());
            System.out.println(cmd);
        }
    }

}