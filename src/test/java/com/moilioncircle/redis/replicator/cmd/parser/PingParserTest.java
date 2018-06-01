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

import com.moilioncircle.redis.replicator.cmd.impl.AggregateType;
import com.moilioncircle.redis.replicator.cmd.impl.AppendCommand;
import com.moilioncircle.redis.replicator.cmd.impl.EvalCommand;
import com.moilioncircle.redis.replicator.cmd.impl.EvalShaCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ExpireAtCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ExpireCommand;
import com.moilioncircle.redis.replicator.cmd.impl.GetSetCommand;
import com.moilioncircle.redis.replicator.cmd.impl.HSetCommand;
import com.moilioncircle.redis.replicator.cmd.impl.HSetNxCommand;
import com.moilioncircle.redis.replicator.cmd.impl.LSetCommand;
import com.moilioncircle.redis.replicator.cmd.impl.MoveCommand;
import com.moilioncircle.redis.replicator.cmd.impl.PExpireAtCommand;
import com.moilioncircle.redis.replicator.cmd.impl.PExpireCommand;
import com.moilioncircle.redis.replicator.cmd.impl.PSetExCommand;
import com.moilioncircle.redis.replicator.cmd.impl.PingCommand;
import com.moilioncircle.redis.replicator.cmd.impl.RenameCommand;
import com.moilioncircle.redis.replicator.cmd.impl.RenameNxCommand;
import com.moilioncircle.redis.replicator.cmd.impl.RestoreCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SAddCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SDiffStoreCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SInterStoreCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SUnionStoreCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ScriptLoadCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SelectCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SetBitCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SetNxCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SetRangeCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ZInterStoreCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ZUnionStoreCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class PingParserTest extends AbstractParserTest {
    @Test
    public void parse() {
        {
            PingParser parser = new PingParser();
            PingCommand cmd = parser.parse(toObjectArray("ping msg".split(" ")));
            assertEquals("msg", cmd.getMessage());
        }

        {
            MoveParser parser = new MoveParser();
            MoveCommand cmd = parser.parse(toObjectArray("move key 2".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(2, cmd.getDb());
        }

        {
            SelectParser parser = new SelectParser();
            SelectCommand cmd = parser.parse(toObjectArray("select 2".split(" ")));
            assertEquals(2, cmd.getIndex());
        }

        {
            RenameParser parser = new RenameParser();
            RenameCommand cmd = parser.parse(toObjectArray("rename key key1".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("key1", cmd.getNewKey());
        }

        {
            RenameNxParser parser = new RenameNxParser();
            RenameNxCommand cmd = parser.parse(toObjectArray("renamenx key key1".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("key1", cmd.getNewKey());
        }

        {
            AppendParser parser = new AppendParser();
            AppendCommand cmd = parser.parse(toObjectArray("append key val".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("val", cmd.getValue());
        }

        {
            SetBitParser parser = new SetBitParser();
            SetBitCommand cmd = parser.parse(toObjectArray("setbit key 10 0".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(10, cmd.getOffset());
            assertEquals(0, cmd.getValue());
        }

        {
            SetRangeParser parser = new SetRangeParser();
            SetRangeCommand cmd = parser.parse(toObjectArray("setrange key 10 val".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(10, cmd.getIndex());
            assertEquals("val", cmd.getValue());
        }

        {
            GetSetParser parser = new GetSetParser();
            GetSetCommand cmd = parser.parse(toObjectArray("getset key val".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("val", cmd.getValue());
        }

        {
            HSetNxParser parser = new HSetNxParser();
            HSetNxCommand cmd = parser.parse(toObjectArray("hsetnx key fie val".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("fie", cmd.getField());
            assertEquals("val", cmd.getValue());
        }

        {
            HSetParser parser = new HSetParser();
            HSetCommand cmd = parser.parse(toObjectArray("hset key fie val".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("fie", cmd.getField());
            assertEquals("val", cmd.getValue());
        }

        {
            LSetParser parser = new LSetParser();
            LSetCommand cmd = parser.parse(toObjectArray("lset key 1 val".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(1, cmd.getIndex());
            assertEquals("val", cmd.getValue());
        }

        {
            PSetExParser parser = new PSetExParser();
            PSetExCommand cmd = parser.parse(toObjectArray("pset key 1 val".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(1, cmd.getEx());
            assertEquals("val", cmd.getValue());
        }

        {
            SAddParser parser = new SAddParser();
            SAddCommand cmd = parser.parse(toObjectArray("sadd key v1 v2".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("v1", cmd.getMembers()[0]);
            assertEquals("v2", cmd.getMembers()[1]);
        }

        {
            SetNxParser parser = new SetNxParser();
            SetNxCommand cmd = parser.parse(toObjectArray("setnx key v1".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("v1", cmd.getValue());
        }

        {
            ExpireAtParser parser = new ExpireAtParser();
            ExpireAtCommand cmd = parser.parse(toObjectArray("expireat key 5".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(5, cmd.getEx());
        }

        {
            ExpireParser parser = new ExpireParser();
            ExpireCommand cmd = parser.parse(toObjectArray("expire key 5".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(5, cmd.getEx());
        }

        {
            PExpireAtParser parser = new PExpireAtParser();
            PExpireAtCommand cmd = parser.parse(toObjectArray("pexpireat key 5".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(5, cmd.getEx());
        }

        {
            PExpireParser parser = new PExpireParser();
            PExpireCommand cmd = parser.parse(toObjectArray("pexpire key 5".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(5, cmd.getEx());
        }

        {
            SDiffStoreParser parser = new SDiffStoreParser();
            SDiffStoreCommand cmd = parser.parse(toObjectArray("sdiffstore des k1 k2".split(" ")));
            assertEquals("des", cmd.getDestination());
            assertEquals("k1", cmd.getKeys()[0]);
            assertEquals("k2", cmd.getKeys()[1]);
        }

        {
            SInterStoreParser parser = new SInterStoreParser();
            SInterStoreCommand cmd = parser.parse(toObjectArray("sinterstore des k1 k2".split(" ")));
            assertEquals("des", cmd.getDestination());
            assertEquals("k1", cmd.getKeys()[0]);
            assertEquals("k2", cmd.getKeys()[1]);
        }

        {
            SUnionStoreParser parser = new SUnionStoreParser();
            SUnionStoreCommand cmd = parser.parse(toObjectArray("sunionstore des k1 k2".split(" ")));
            assertEquals("des", cmd.getDestination());
            assertEquals("k1", cmd.getKeys()[0]);
            assertEquals("k2", cmd.getKeys()[1]);
        }

        {
            ZInterStoreParser parser = new ZInterStoreParser();
            ZInterStoreCommand cmd = parser.parse(toObjectArray("zinterstore des 2 k1 k2 WEIGHTS 2 3 AGGREGATE sum".split(" ")));
            assertEquals("des", cmd.getDestination());
            assertEquals(2, cmd.getNumkeys());
            assertEquals("k1", cmd.getKeys()[0]);
            assertEquals("k2", cmd.getKeys()[1]);
            assertEquals(2, cmd.getWeights()[0], 0);
            assertEquals(3, cmd.getWeights()[1], 0);
            assertEquals(AggregateType.SUM, cmd.getAggregateType());
        }

        {
            ZUnionStoreParser parser = new ZUnionStoreParser();
            ZUnionStoreCommand cmd = parser.parse(toObjectArray("zunionstore des 2 k1 k2 WEIGHTS 2 3 AGGREGATE min".split(" ")));
            assertEquals("des", cmd.getDestination());
            assertEquals(2, cmd.getNumkeys());
            assertEquals("k1", cmd.getKeys()[0]);
            assertEquals("k2", cmd.getKeys()[1]);
            assertEquals(2, cmd.getWeights()[0], 0);
            assertEquals(3, cmd.getWeights()[1], 0);
            assertEquals(AggregateType.MIN, cmd.getAggregateType());
        }

        {
            EvalParser parser = new EvalParser();
            EvalCommand cmd = parser.parse(toObjectArray(new Object[]{"eval", "return redis.call('set',KEYS[1],'bar')", "1", "foo"}));
            assertEquals("return redis.call('set',KEYS[1],'bar')", cmd.getScript());
            assertEquals(1, cmd.getNumkeys());
            assertEquals("foo", cmd.getKeys()[0]);
        }

        {
            EvalShaParser parser = new EvalShaParser();
            EvalShaCommand cmd = parser.parse(toObjectArray(new Object[]{"evalsha", "2fa2b029f72572e803ff55a09b1282699aecae6a", "1", "foo"}));
            assertEquals("2fa2b029f72572e803ff55a09b1282699aecae6a", cmd.getSha());
            assertEquals(1, cmd.getNumkeys());
            assertEquals("foo", cmd.getKeys()[0]);
        }

        {
            ScriptParser parser = new ScriptParser();
            ScriptLoadCommand cmd = (ScriptLoadCommand) parser.parse(toObjectArray(new Object[]{"script", "load", "return redis.call('set',KEYS[1],'bar')"}));
            assertEquals("return redis.call('set',KEYS[1],'bar')", cmd.getScript());
        }

        {
            ScriptParser parser = new ScriptParser();
            parser.parse(toObjectArray(new Object[]{"script", "flush"}));
        }

        {
            RestoreParser parser = new RestoreParser();
            RestoreCommand cmd = parser.parse(toObjectArray(new Object[]{"restore", "mykey", "0", "\\n\\x17\\x17\\x00\\x00\\x00\\x12\\x00\\x00\\x00\\x03\\x00\\x00\\xc0\\x01\\x00\\x04\\xc0\\x02\\x00\\x04\\xc0\\x03\\x00\\xff\\x04\\x00u#<\\xc0;.\\xe9\\xdd"}));
            assertEquals("\\n\\x17\\x17\\x00\\x00\\x00\\x12\\x00\\x00\\x00\\x03\\x00\\x00\\xc0\\x01\\x00\\x04\\xc0\\x02\\x00\\x04\\xc0\\x03\\x00\\xff\\x04\\x00u#<\\xc0;.\\xe9\\xdd", cmd.getSerializedValue());
            assertEquals("mykey", cmd.getKey());
            assertEquals(0L, cmd.getTtl());
            assertEquals(null, cmd.getReplace());
        }

    }

}