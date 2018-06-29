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

import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.ZAddCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ZRemRangeByLexCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ZRemRangeByRankCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ZRemRangeByScoreCommand;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ZAddParserTest extends AbstractParserTest {
    @Test
    public void parse() {
        ZAddParser parser = new ZAddParser();
        ZAddCommand cmd = parser.parse(toObjectArray("zadd abc nx ch incr 1 b".split(" ")));
        assertEquals("abc", cmd.getKey());
        TestCase.assertEquals(ExistType.NX, cmd.getExistType());
        assertEquals(true, cmd.isCh());
        assertEquals(true, cmd.isIncr());
        TestCase.assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());

        cmd = parser.parse(toObjectArray("zadd abc 1 b".split(" ")));
        assertEquals("abc", cmd.getKey());
        TestCase.assertEquals(ExistType.NONE, cmd.getExistType());
        assertEquals(false, cmd.isCh());
        assertEquals(false, cmd.isIncr());
        TestCase.assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());

        cmd = parser.parse(toObjectArray("zadd abc xx 1 b".split(" ")));
        assertEquals("abc", cmd.getKey());
        TestCase.assertEquals(ExistType.XX, cmd.getExistType());
        assertEquals(false, cmd.isCh());
        assertEquals(false, cmd.isIncr());
        TestCase.assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());

        cmd = parser.parse(toObjectArray("zadd abc incr 1 b".split(" ")));
        assertEquals("abc", cmd.getKey());
        TestCase.assertEquals(ExistType.NONE, cmd.getExistType());
        assertEquals(false, cmd.isCh());
        assertEquals(true, cmd.isIncr());
        TestCase.assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());

        {
            ZRemRangeByLexParser parser1 = new ZRemRangeByLexParser();
            ZRemRangeByLexCommand cmd1 = parser1.parse(toObjectArray("ZREMRANGEBYLEX myzset [alpha [omega".split(" ")));
            assertEquals("myzset", cmd1.getKey());
            assertEquals("[alpha", cmd1.getMin());
            assertEquals("[omega", cmd1.getMax());
        }

        {
            ZRemRangeByScoreParser parser1 = new ZRemRangeByScoreParser();
            ZRemRangeByScoreCommand cmd1 = parser1.parse(toObjectArray("ZREMRANGEBYSCORE myzset -inf (2".split(" ")));
            assertEquals("myzset", cmd1.getKey());
            assertEquals("-inf", cmd1.getMin());
            assertEquals("(2", cmd1.getMax());
        }

        {
            ZRemRangeByRankParser parser1 = new ZRemRangeByRankParser();
            ZRemRangeByRankCommand cmd1 = parser1.parse(toObjectArray("ZREMRANGEBYRANK myzset 0 1".split(" ")));
            assertEquals("myzset", cmd1.getKey());
            assertEquals(0L, cmd1.getStart());
            assertEquals(1L, cmd1.getStop());
        }

    }

}