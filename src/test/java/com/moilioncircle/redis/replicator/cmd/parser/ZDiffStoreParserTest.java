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

import com.moilioncircle.redis.replicator.cmd.impl.ZDiffStoreCommand;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class ZDiffStoreParserTest extends AbstractParserTest {
    @Test
    public void parse() {
        {
            ZDiffStoreParser parser = new ZDiffStoreParser();
            ZDiffStoreCommand cmd = parser.parse(toObjectArray("zdiffstore aaa 2 key1 key2".split(" ")));
            assertEquals("aaa", cmd.getDestination());
            assertEquals(2, cmd.getNumkeys());
            assertEquals("key1", cmd.getKeys()[0]);
            assertEquals("key2", cmd.getKeys()[1]);
        }
        
        {
            ZDiffStoreParser parser = new ZDiffStoreParser();
            ZDiffStoreCommand cmd = parser.parse(toObjectArray("zdiffstore aaa 1 key1".split(" ")));
            assertEquals("aaa", cmd.getDestination());
            assertEquals(1, cmd.getNumkeys());
            assertEquals("key1", cmd.getKeys()[0]);
        }
    }
}