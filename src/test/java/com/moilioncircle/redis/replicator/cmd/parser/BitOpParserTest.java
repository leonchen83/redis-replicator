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

import com.moilioncircle.redis.replicator.cmd.impl.BitOpCommand;
import com.moilioncircle.redis.replicator.cmd.impl.Op;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class BitOpParserTest extends AbstractParserTest {
    @Test
    public void parse() {
        BitOpParser parser = new BitOpParser();
        BitOpCommand cmd = parser.parse(toObjectArray("bitop and des key1 key2".split(" ")));
        assertEquals("des", cmd.getDestkey());
        TestCase.assertEquals(Op.AND, cmd.getOp());
        assertEquals("key1", cmd.getKeys()[0]);
        assertEquals("key2", cmd.getKeys()[1]);
    }

}