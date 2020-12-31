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

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.moilioncircle.redis.replicator.cmd.impl.CopyCommand;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class CopyParserTest extends AbstractParserTest {
    @Test
    public void parse() {
        {
            CopyParser parser = new CopyParser();
            CopyCommand cmd = parser.parse(toObjectArray("copy aaa bbb db 1 replace".split(" ")));
            assertEquals("aaa", cmd.getSource());
            assertEquals("bbb", cmd.getDestination());
            assertEquals(1, cmd.getDb());
            assertEquals(true, cmd.isReplace());
        }
        
        {
            CopyParser parser = new CopyParser();
            CopyCommand cmd = parser.parse(toObjectArray("copy aaa bbb replace".split(" ")));
            assertEquals("aaa", cmd.getSource());
            assertEquals("bbb", cmd.getDestination());
            assertNull(cmd.getDb());
            assertEquals(true, cmd.isReplace());
        }
        
        {
            CopyParser parser = new CopyParser();
            CopyCommand cmd = parser.parse(toObjectArray("copy aaa bbb db 1".split(" ")));
            assertEquals("aaa", cmd.getSource());
            assertEquals("bbb", cmd.getDestination());
            assertEquals(1, cmd.getDb());
            assertEquals(false, cmd.isReplace());
        }
        
        {
            CopyParser parser = new CopyParser();
            CopyCommand cmd = parser.parse(toObjectArray("copy aaa bbb".split(" ")));
            assertEquals("aaa", cmd.getSource());
            assertEquals("bbb", cmd.getDestination());
            assertNull(cmd.getDb());
            assertEquals(false, cmd.isReplace());
        }
    }
}