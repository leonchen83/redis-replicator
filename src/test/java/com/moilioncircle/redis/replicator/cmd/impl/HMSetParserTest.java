package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.CommandName;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 9/15/16.
 */
public class HMSetParserTest {

    @Test
    public void testParse() throws Exception {
        {
            HMSetParser hmSetParser = new HMSetParser();
            HMSetParser.HMSetCommand command = hmSetParser.parse(CommandName.name("HMSET"), new Object[]{"key", "field", "value"});
            assertEquals("key", command.key);
            assertEquals(1, command.fields.size());
        }

        {
            HMSetParser hmSetParser = new HMSetParser();
            HMSetParser.HMSetCommand command = hmSetParser.parse(CommandName.name("HMSET"), new Object[]{"key", "field", "value", "field1", "value1"});
            assertEquals("key", command.key);
            assertEquals(2, command.fields.size());
        }
    }
}