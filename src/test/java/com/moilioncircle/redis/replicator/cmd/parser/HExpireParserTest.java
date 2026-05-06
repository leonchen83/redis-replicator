/*
 * Copyright 2026 otheng03
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
import com.moilioncircle.redis.replicator.cmd.impl.HExpireAtCommand;
import com.moilioncircle.redis.replicator.cmd.impl.HExpireCommand;
import com.moilioncircle.redis.replicator.cmd.impl.HPExpireCommand;

import junit.framework.TestCase;

/**
 * @author otheng03
 * @since 3.12.0
 */
public class HExpireParserTest extends AbstractParserTest {

    @Test
    public void testHExpire() {
        HExpireParser parser = new HExpireParser();

        HExpireCommand cmd = parser.parse(toObjectArray("hexpire mykey 100 fields 2 f1 f2".split(" ")));
        assertEquals("mykey", cmd.getKey());
        assertEquals(100L, cmd.getEx());
        assertEquals(2, cmd.getFields().length);
        assertEquals("f1", cmd.getFields()[0]);
        assertEquals("f2", cmd.getFields()[1]);
        TestCase.assertEquals(ExistType.NONE, cmd.getExistType());
        TestCase.assertEquals(CompareType.NONE, cmd.getCompareType());

        cmd = parser.parse(toObjectArray("hexpire mykey 100 NX fields 1 f1".split(" ")));
        assertEquals("mykey", cmd.getKey());
        assertEquals(100L, cmd.getEx());
        assertEquals(1, cmd.getFields().length);
        assertEquals("f1", cmd.getFields()[0]);
        TestCase.assertEquals(ExistType.NX, cmd.getExistType());
        TestCase.assertEquals(CompareType.NONE, cmd.getCompareType());

        cmd = parser.parse(toObjectArray("hexpire mykey 100 GT fields 1 f1".split(" ")));
        assertEquals("mykey", cmd.getKey());
        assertEquals(100L, cmd.getEx());
        TestCase.assertEquals(ExistType.NONE, cmd.getExistType());
        TestCase.assertEquals(CompareType.GT, cmd.getCompareType());

        cmd = parser.parse(toObjectArray("hexpire mykey 100 XX LT fields 1 f1".split(" ")));
        assertEquals("mykey", cmd.getKey());
        assertEquals(100L, cmd.getEx());
        TestCase.assertEquals(ExistType.XX, cmd.getExistType());
        TestCase.assertEquals(CompareType.LT, cmd.getCompareType());
    }

    @Test
    public void testHPExpire() {
        HPExpireParser parser = new HPExpireParser();

        HPExpireCommand cmd = parser.parse(toObjectArray("hpexpire mykey 100000 fields 2 f1 f2".split(" ")));
        assertEquals("mykey", cmd.getKey());
        assertEquals(100000L, cmd.getEx());
        assertEquals(2, cmd.getFields().length);
        assertEquals("f1", cmd.getFields()[0]);
        assertEquals("f2", cmd.getFields()[1]);
        TestCase.assertEquals(ExistType.NONE, cmd.getExistType());
        TestCase.assertEquals(CompareType.NONE, cmd.getCompareType());

        cmd = parser.parse(toObjectArray("hpexpire mykey 100000 NX fields 1 f1".split(" ")));
        assertEquals("mykey", cmd.getKey());
        assertEquals(100000L, cmd.getEx());
        TestCase.assertEquals(ExistType.NX, cmd.getExistType());
        TestCase.assertEquals(CompareType.NONE, cmd.getCompareType());
    }

    @Test
    public void testHExpireAt() {
        HExpireAtParser parser = new HExpireAtParser();

        HExpireAtCommand cmd = parser.parse(toObjectArray("hexpireat mykey 1614139099 fields 2 f1 f2".split(" ")));
        assertEquals("mykey", cmd.getKey());
        assertEquals(1614139099L, cmd.getEx());
        assertEquals(2, cmd.getFields().length);
        assertEquals("f1", cmd.getFields()[0]);
        assertEquals("f2", cmd.getFields()[1]);
        TestCase.assertEquals(ExistType.NONE, cmd.getExistType());
        TestCase.assertEquals(CompareType.NONE, cmd.getCompareType());

        cmd = parser.parse(toObjectArray("hexpireat mykey 1614139099 GT fields 1 f1".split(" ")));
        assertEquals("mykey", cmd.getKey());
        assertEquals(1614139099L, cmd.getEx());
        TestCase.assertEquals(ExistType.NONE, cmd.getExistType());
        TestCase.assertEquals(CompareType.GT, cmd.getCompareType());
    }
}
