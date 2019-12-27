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
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.rdb.datatype.ExpiredType;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class SetParserTest extends AbstractParserTest {
    @Test
    public void parse() {
        SetParser parser = new SetParser();
        SetCommand cmd = parser.parse(toObjectArray("set a b ex 15 nx".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals("b", cmd.getValue());
        TestCase.assertEquals(ExpiredType.SECOND, cmd.getExpiredType());
        assertEquals(15L, cmd.getExpiredValue());
        TestCase.assertEquals(ExistType.NX, cmd.getExistType());

        cmd = parser.parse(toObjectArray("set a b px 123 xx".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals("b", cmd.getValue());
        TestCase.assertEquals(ExpiredType.MS, cmd.getExpiredType());
        assertEquals(123L, cmd.getExpiredValue());
        TestCase.assertEquals(ExistType.XX, cmd.getExistType());

        cmd = parser.parse(toObjectArray("set a b xx px 123".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals("b", cmd.getValue());
        TestCase.assertEquals(ExpiredType.MS, cmd.getExpiredType());
        assertEquals(123L, cmd.getExpiredValue());
        assertEquals(false, cmd.getKeepTtl());
        TestCase.assertEquals(ExistType.XX, cmd.getExistType());

        cmd = parser.parse(toObjectArray("set a b xx keepttl px 123".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals("b", cmd.getValue());
        TestCase.assertEquals(ExpiredType.MS, cmd.getExpiredType());
        assertEquals(true, cmd.getKeepTtl());
        assertEquals(123L, cmd.getExpiredValue());
        TestCase.assertEquals(ExistType.XX, cmd.getExistType());

    }

}