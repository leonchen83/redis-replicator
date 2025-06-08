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

import static junit.framework.TestCase.assertNull;

import org.junit.Test;

import com.moilioncircle.redis.replicator.cmd.impl.FieldExistType;
import com.moilioncircle.redis.replicator.cmd.impl.HSetExCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XATType;
import com.moilioncircle.redis.replicator.rdb.datatype.ExpiredType;

import junit.framework.TestCase;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class HSetExParserTest extends AbstractParserTest {
    @Test
    public void parse() {
        HSetExParser parser = new HSetExParser();
        HSetExCommand cmd = parser.parse(toObjectArray("hsetex a ex 15 fnx fields 2 f1 v1".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals(1, cmd.getFields().length);
        assertEquals(1, cmd.getValues().length);
        assertEquals("f1", cmd.getFields()[0]);
        assertEquals("v1", cmd.getValues()[0]);
        TestCase.assertEquals(ExpiredType.SECOND, cmd.getExpiredType());
        assertEquals(15L, cmd.getExpiredValue());
        TestCase.assertEquals(FieldExistType.FNX, cmd.getExistType());
        assertEquals(false, cmd.getKeepTtl());
    
        cmd = parser.parse(toObjectArray("hsetex a px 123 fxx fields 2 f1 v1 f2 v2".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals(2, cmd.getFields().length);
        assertEquals(2, cmd.getValues().length);
        assertEquals("f1", cmd.getFields()[0]);
        assertEquals("f2", cmd.getFields()[1]);
        assertEquals("v1", cmd.getValues()[0]);
        assertEquals("v2", cmd.getValues()[1]);
        TestCase.assertEquals(ExpiredType.MS, cmd.getExpiredType());
        assertEquals(123L, cmd.getExpiredValue());
        TestCase.assertEquals(FieldExistType.FXX, cmd.getExistType());
        assertEquals(false, cmd.getKeepTtl());
    
        cmd = parser.parse(toObjectArray("hsetex a px 123 fields 2 f1 v1 f2 v2".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals(2, cmd.getFields().length);
        assertEquals(2, cmd.getValues().length);
        assertEquals("f1", cmd.getFields()[0]);
        assertEquals("f2", cmd.getFields()[1]);
        assertEquals("v1", cmd.getValues()[0]);
        assertEquals("v2", cmd.getValues()[1]);
        TestCase.assertEquals(ExpiredType.MS, cmd.getExpiredType());
        assertEquals(123L, cmd.getExpiredValue());
        TestCase.assertEquals(FieldExistType.NONE, cmd.getExistType());
        assertEquals(false, cmd.getKeepTtl());
    
        cmd = parser.parse(toObjectArray("hsetex a fxx px 123 fields 2 f1 v1 f2 v2".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals(2, cmd.getFields().length);
        assertEquals(2, cmd.getValues().length);
        assertEquals("f1", cmd.getFields()[0]);
        assertEquals("f2", cmd.getFields()[1]);
        assertEquals("v1", cmd.getValues()[0]);
        assertEquals("v2", cmd.getValues()[1]);
        TestCase.assertEquals(ExpiredType.MS, cmd.getExpiredType());
        assertEquals(123L, cmd.getExpiredValue());
        assertEquals(false, cmd.getKeepTtl());
        TestCase.assertEquals(FieldExistType.FXX, cmd.getExistType());
    
        cmd = parser.parse(toObjectArray("hsetex a fxx keepttl px 123 fields 2 f1 v1 f2 v2".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals(2, cmd.getFields().length);
        assertEquals(2, cmd.getValues().length);
        assertEquals("f1", cmd.getFields()[0]);
        assertEquals("f2", cmd.getFields()[1]);
        assertEquals("v1", cmd.getValues()[0]);
        assertEquals("v2", cmd.getValues()[1]);
        TestCase.assertEquals(ExpiredType.MS, cmd.getExpiredType());
        assertEquals(true, cmd.getKeepTtl());
        assertEquals(123L, cmd.getExpiredValue());
        TestCase.assertEquals(FieldExistType.FXX, cmd.getExistType());
    
        cmd = parser.parse(toObjectArray("hsetex a fxx keepttl EXAT 1614139099 fields 2 f1 v1 f2 v2".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals(2, cmd.getFields().length);
        assertEquals(2, cmd.getValues().length);
        assertEquals("f1", cmd.getFields()[0]);
        assertEquals("f2", cmd.getFields()[1]);
        assertEquals("v1", cmd.getValues()[0]);
        assertEquals("v2", cmd.getValues()[1]);
        TestCase.assertEquals(ExpiredType.NONE, cmd.getExpiredType());
        assertEquals(true, cmd.getKeepTtl());
        assertNull(cmd.getExpiredValue());
        TestCase.assertEquals(XATType.EXAT, cmd.getXatType());
        assertEquals(1614139099L, cmd.getXatValue());
        TestCase.assertEquals(FieldExistType.FXX, cmd.getExistType());
    
        cmd = parser.parse(toObjectArray("hsetex a fxx keepttl PXAT 1614139099000 fields 2 f1 v1 f2 v2".split(" ")));
        assertEquals("a", cmd.getKey());
        assertEquals(2, cmd.getFields().length);
        assertEquals(2, cmd.getValues().length);
        assertEquals("f1", cmd.getFields()[0]);
        assertEquals("f2", cmd.getFields()[1]);
        assertEquals("v1", cmd.getValues()[0]);
        assertEquals("v2", cmd.getValues()[1]);
        TestCase.assertEquals(ExpiredType.NONE, cmd.getExpiredType());
        assertEquals(true, cmd.getKeepTtl());
        assertNull(cmd.getExpiredValue());
        TestCase.assertEquals(XATType.PXAT, cmd.getXatType());
        assertEquals(1614139099000L, cmd.getXatValue());
        TestCase.assertEquals(FieldExistType.FXX, cmd.getExistType());
    
    }

}