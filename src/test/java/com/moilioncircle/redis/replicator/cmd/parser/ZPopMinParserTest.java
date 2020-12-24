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

import org.junit.Test;

import com.moilioncircle.redis.replicator.cmd.impl.ZPopMinCommand;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class ZPopMinParserTest extends AbstractParserTest {
    
    @Test
    public void parse() {
        {
            ZPopMinParser parser = new ZPopMinParser();
            ZPopMinCommand cmd = parser.parse(toObjectArray("ZPOPMIN myzset 5".split(" ")));
            assertEquals("myzset", cmd.getKey());
            assertEquals(5, cmd.getCount());
        }
    
        {
            ZPopMinParser parser = new ZPopMinParser();
            ZPopMinCommand cmd = parser.parse(toObjectArray("ZPOPMIN myzset".split(" ")));
            assertEquals("myzset", cmd.getKey());
            assertEquals(1, cmd.getCount());
        }
    }
}