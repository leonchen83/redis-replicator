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

import com.moilioncircle.redis.replicator.cmd.impl.XAckCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XAddCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XClaimCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XDelCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupCreateCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupDelConsumerCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupDestroyCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupSetIdCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XSetIdCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XTrimCommand;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class StreamParserTest extends AbstractParserTest {
    
    @Test
    public void parse() {
        {
            XAckParser parser = new XAckParser();
            XAckCommand cmd = parser.parse(toObjectArray("xack key group 1528524799760-0".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("group", cmd.getGroup());
            assertEquals(1, cmd.getIds().length);
            assertEquals("1528524799760-0", cmd.getIds()[0]);
        }
        
        {
            XAckParser parser = new XAckParser();
            XAckCommand cmd = parser.parse(toObjectArray("xack key group 1528524799760-0 1528524789760-0".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("group", cmd.getGroup());
            assertEquals(2, cmd.getIds().length);
            assertEquals("1528524789760-0", cmd.getIds()[1]);
        }
        
        {
            XAddParser parser = new XAddParser();
            XAddCommand cmd = parser.parse(toObjectArray("XADD key * field value".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("*", cmd.getId());
            assertNull(cmd.getMaxLen());
            assertTrue(cmd.getFields().containsKey("field".getBytes()));
        }
        
        {
            XAddParser parser = new XAddParser();
            XAddCommand cmd = parser.parse(toObjectArray("XADD key maxlen 100 * field value".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("*", cmd.getId());
            assertEquals(100L, cmd.getMaxLen().getCount());
            assertFalse(cmd.getMaxLen().isApproximation());
            assertTrue(cmd.getFields().containsKey("field".getBytes()));
        }
        
        {
            XAddParser parser = new XAddParser();
            XAddCommand cmd = parser.parse(toObjectArray("XADD key maxlen ~ 100 * field value field1 value1".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("*", cmd.getId());
            assertEquals(100L, cmd.getMaxLen().getCount());
            assertTrue(cmd.getMaxLen().isApproximation());
            assertTrue(cmd.getFields().containsKey("field".getBytes()));
            assertTrue(cmd.getFields().containsKey("field1".getBytes()));
        }
    
        {
            XAddParser parser = new XAddParser();
            XAddCommand cmd = parser.parse(toObjectArray("XADD key maxlen = 100 * field value field1 value1".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("*", cmd.getId());
            assertEquals(100L, cmd.getMaxLen().getCount());
            assertFalse(cmd.getMaxLen().isApproximation());
            assertTrue(cmd.getFields().containsKey("field".getBytes()));
            assertTrue(cmd.getFields().containsKey("field1".getBytes()));
        }
        
        {
            XClaimParser parser = new XClaimParser();
            XClaimCommand cmd = parser.parse(toObjectArray("XCLAIM key group consumer 10000 1528524799760-0 1528524789760-0 IDLE 10000000 TIME 20000000 RETRYCOUNT 3 FORCE JUSTID".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("group", cmd.getGroup());
            assertEquals("consumer", cmd.getConsumer());
            assertEquals(10000L, cmd.getMinIdle());
            assertEquals(2, cmd.getIds().length);
            assertEquals(10000000L, cmd.getIdle().longValue());
            assertEquals(20000000L, cmd.getTime().longValue());
            assertEquals(3L, cmd.getRetryCount().longValue());
            assertTrue(cmd.isForce());
            assertTrue(cmd.isJustId());
        }
        
        {
            XClaimParser parser = new XClaimParser();
            XClaimCommand cmd = parser.parse(toObjectArray("XCLAIM key group consumer 10000 1528524799760-0 1528524789760-0 TIME 20000000 RETRYCOUNT 3 FORCE JUSTID".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("group", cmd.getGroup());
            assertEquals("consumer", cmd.getConsumer());
            assertEquals(10000L, cmd.getMinIdle());
            assertEquals(2, cmd.getIds().length);
            assertNull(cmd.getIdle());
            assertEquals(20000000L, cmd.getTime().longValue());
            assertEquals(3L, cmd.getRetryCount().longValue());
            assertTrue(cmd.isForce());
            assertTrue(cmd.isJustId());
        }
        
        {
            XClaimParser parser = new XClaimParser();
            XClaimCommand cmd = parser.parse(toObjectArray("XCLAIM key group consumer 10000 1528524799760-0 1528524789760-0 RETRYCOUNT 3 FORCE JUSTID".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("group", cmd.getGroup());
            assertEquals("consumer", cmd.getConsumer());
            assertEquals(10000L, cmd.getMinIdle());
            assertEquals(2, cmd.getIds().length);
            assertNull(cmd.getIdle());
            assertNull(cmd.getTime());
            assertEquals(3L, cmd.getRetryCount().longValue());
            assertTrue(cmd.isForce());
            assertTrue(cmd.isJustId());
        }
        
        {
            XClaimParser parser = new XClaimParser();
            XClaimCommand cmd = parser.parse(toObjectArray("XCLAIM key group consumer 10000 1528524799760-0 1528524789760-0 FORCE JUSTID".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("group", cmd.getGroup());
            assertEquals("consumer", cmd.getConsumer());
            assertEquals(10000L, cmd.getMinIdle());
            assertEquals(2, cmd.getIds().length);
            assertNull(cmd.getIdle());
            assertNull(cmd.getTime());
            assertNull(cmd.getRetryCount());
            assertTrue(cmd.isForce());
            assertTrue(cmd.isJustId());
        }
    
        {
            XClaimParser parser = new XClaimParser();
            XClaimCommand cmd = parser.parse(toObjectArray("XCLAIM key group consumer 10000 1528524799760-0 1528524789760-0 FORCE JUSTID LASTID $".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("group", cmd.getGroup());
            assertEquals("consumer", cmd.getConsumer());
            assertEquals(10000L, cmd.getMinIdle());
            assertEquals(2, cmd.getIds().length);
            assertNull(cmd.getIdle());
            assertNull(cmd.getTime());
            assertNull(cmd.getRetryCount());
            assertTrue(cmd.isForce());
            assertTrue(cmd.isJustId());
            assertEquals("$", cmd.getLastId());
        }
        
        {
            XClaimParser parser = new XClaimParser();
            XClaimCommand cmd = parser.parse(toObjectArray("XCLAIM key group consumer 10000 1528524799760-0 1528524789760-0 JUSTID".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("group", cmd.getGroup());
            assertEquals("consumer", cmd.getConsumer());
            assertEquals(10000L, cmd.getMinIdle());
            assertEquals(2, cmd.getIds().length);
            assertNull(cmd.getIdle());
            assertNull(cmd.getTime());
            assertNull(cmd.getRetryCount());
            assertFalse(cmd.isForce());
            assertTrue(cmd.isJustId());
        }
        
        {
            XClaimParser parser = new XClaimParser();
            XClaimCommand cmd = parser.parse(toObjectArray("XCLAIM key group consumer 10000 1528524799760-0 1528524789760-0".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("group", cmd.getGroup());
            assertEquals("consumer", cmd.getConsumer());
            assertEquals(10000L, cmd.getMinIdle());
            assertEquals(2, cmd.getIds().length);
            assertNull(cmd.getIdle());
            assertNull(cmd.getTime());
            assertNull(cmd.getRetryCount());
            assertFalse(cmd.isForce());
            assertFalse(cmd.isJustId());
        }
        
        {
            XClaimParser parser = new XClaimParser();
            XClaimCommand cmd = parser.parse(toObjectArray("XCLAIM key group consumer 10000 1528524799760-0".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("group", cmd.getGroup());
            assertEquals("consumer", cmd.getConsumer());
            assertEquals(10000L, cmd.getMinIdle());
            assertEquals(1, cmd.getIds().length);
            assertNull(cmd.getIdle());
            assertNull(cmd.getTime());
            assertNull(cmd.getRetryCount());
            assertFalse(cmd.isForce());
            assertFalse(cmd.isJustId());
        }
        
        {
            XDelParser parser = new XDelParser();
            XDelCommand cmd = parser.parse(toObjectArray("xdel key 1528524799760-0 1528524899760-0".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(2, cmd.getIds().length);
            assertEquals("1528524799760-0", cmd.getIds()[0]);
            assertEquals("1528524899760-0", cmd.getIds()[1]);
        }
        
        {
            XGroupParser parser = new XGroupParser();
            XGroupCommand cmd = parser.parse(toObjectArray("XGROUP CREATE key group $".split(" ")));
            if (cmd instanceof XGroupCreateCommand) {
                XGroupCreateCommand ccmd = (XGroupCreateCommand) cmd;
                assertEquals("key", ccmd.getKey());
                assertEquals("group", ccmd.getGroup());
                assertEquals("$", ccmd.getId());
            } else if (cmd instanceof XGroupDelConsumerCommand) {
                fail();
            }
        }
        
        {
            XGroupParser parser = new XGroupParser();
            XGroupCommand cmd = parser.parse(toObjectArray("XGROUP CREATE key group 1528524899760-0".split(" ")));
            if (cmd instanceof XGroupCreateCommand) {
                XGroupCreateCommand ccmd = (XGroupCreateCommand) cmd;
                assertEquals("key", ccmd.getKey());
                assertEquals("group", ccmd.getGroup());
                assertEquals("1528524899760-0", ccmd.getId());
            } else if (cmd instanceof XGroupDelConsumerCommand) {
                fail();
            }
        }
    
        {
            XGroupParser parser = new XGroupParser();
            XGroupCommand cmd = parser.parse(toObjectArray("XGROUP setid key group 1528524899760-0".split(" ")));
            if (cmd instanceof XGroupSetIdCommand) {
                XGroupSetIdCommand ccmd = (XGroupSetIdCommand) cmd;
                assertEquals("key", ccmd.getKey());
                assertEquals("group", ccmd.getGroup());
                assertEquals("1528524899760-0", ccmd.getId());
            } else if (cmd instanceof XGroupDelConsumerCommand) {
                fail();
            }
        }
        
        {
            XGroupParser parser = new XGroupParser();
            XGroupCommand cmd = parser.parse(toObjectArray("XGROUP DELCONSUMER key group consumer".split(" ")));
            if (cmd instanceof XGroupCreateCommand) {
                fail();
            } else if (cmd instanceof XGroupDelConsumerCommand) {
                XGroupDelConsumerCommand ccmd = (XGroupDelConsumerCommand) cmd;
                assertEquals("key", ccmd.getKey());
                assertEquals("group", ccmd.getGroup());
                assertEquals("consumer", ccmd.getConsumer());
            }
        }
    
        {
            XGroupParser parser = new XGroupParser();
            XGroupCommand cmd = parser.parse(toObjectArray("XGROUP DESTROY key group".split(" ")));
            if (cmd instanceof XGroupCreateCommand) {
                fail();
            } else if (cmd instanceof XGroupDestroyCommand) {
                XGroupDestroyCommand ccmd = (XGroupDestroyCommand) cmd;
                assertEquals("key", ccmd.getKey());
                assertEquals("group", ccmd.getGroup());
            }
        }
        
        {
            XTrimParser parser = new XTrimParser();
            XTrimCommand cmd = parser.parse(toObjectArray("XTRIM key maxlen 100".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(100L, cmd.getMaxLen().getCount());
            assertFalse(cmd.getMaxLen().isApproximation());
        }
        
        {
            XTrimParser parser = new XTrimParser();
            XTrimCommand cmd = parser.parse(toObjectArray("XTRIM key maxlen ~ 100".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(100L, cmd.getMaxLen().getCount());
            assertTrue(cmd.getMaxLen().isApproximation());
        }
    
        {
            XTrimParser parser = new XTrimParser();
            XTrimCommand cmd = parser.parse(toObjectArray("XTRIM key maxlen = 100".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals(100L, cmd.getMaxLen().getCount());
            assertFalse(cmd.getMaxLen().isApproximation());
        }
    
        {
            XSetIdParser parser = new XSetIdParser();
            XSetIdCommand cmd = parser.parse(toObjectArray("XSETID key $".split(" ")));
            assertEquals("key", cmd.getKey());
            assertEquals("$", cmd.getId());
        }
        
    }
    
}
