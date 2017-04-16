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

import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.ZAddCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ZAddParserTest {
    @Test
    public void parse() throws Exception {
        ZAddParser parser = new ZAddParser();
        ZAddCommand cmd = parser.parse("zadd abc nx ch incr 1 b".split(" "));
        assertEquals("abc", cmd.getKey());
        assertEquals(ExistType.NX, cmd.getExistType());
        assertEquals(Boolean.TRUE, cmd.getCh());
        assertEquals(Boolean.TRUE, cmd.getIncr());
        assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());
        System.out.println(cmd);

        cmd = parser.parse("zadd abc 1 b".split(" "));
        assertEquals("abc", cmd.getKey());
        assertEquals(ExistType.NONE, cmd.getExistType());
        assertEquals(null, cmd.getCh());
        assertEquals(null, cmd.getIncr());
        assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());
        System.out.println(cmd);

        cmd = parser.parse("zadd abc xx 1 b".split(" "));
        assertEquals("abc", cmd.getKey());
        assertEquals(ExistType.XX, cmd.getExistType());
        assertEquals(null, cmd.getCh());
        assertEquals(null, cmd.getIncr());
        assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());
        System.out.println(cmd);

        cmd = parser.parse("zadd abc incr 1 b".split(" "));
        assertEquals("abc", cmd.getKey());
        assertEquals(ExistType.NONE, cmd.getExistType());
        assertEquals(null, cmd.getCh());
        assertEquals(Boolean.TRUE, cmd.getIncr());
        assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());
        System.out.println(cmd);
    }

}