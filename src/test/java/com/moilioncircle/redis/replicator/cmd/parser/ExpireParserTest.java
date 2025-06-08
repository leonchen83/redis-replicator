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

import com.moilioncircle.redis.replicator.cmd.impl.CompareType;
import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.ExpireCommand;

import junit.framework.TestCase;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ExpireParserTest extends AbstractParserTest {
    @Test
    public void parse() {
        {
            ExpireParser parser = new ExpireParser();
            ExpireCommand cmd = parser.parse(toObjectArray("expire key 100".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(100, cmd.getEx());
            TestCase.assertEquals(ExistType.NONE, cmd.getExistType());
            TestCase.assertEquals(CompareType.NONE, cmd.getCompareType());
        }
        
        {
            ExpireParser parser = new ExpireParser();
            ExpireCommand cmd = parser.parse(toObjectArray("expire key 100 xx".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(100, cmd.getEx());
            TestCase.assertEquals(ExistType.XX, cmd.getExistType());
            TestCase.assertEquals(CompareType.NONE, cmd.getCompareType());
        }
        
        {
            ExpireParser parser = new ExpireParser();
            ExpireCommand cmd = parser.parse(toObjectArray("expire key 100 gt".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(100, cmd.getEx());
            TestCase.assertEquals(ExistType.NONE, cmd.getExistType());
            TestCase.assertEquals(CompareType.GT, cmd.getCompareType());
        }
        
    }

}