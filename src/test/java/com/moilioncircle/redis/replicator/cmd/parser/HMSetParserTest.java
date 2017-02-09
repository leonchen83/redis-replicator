package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.HMSetCommand;
import com.moilioncircle.redis.replicator.cmd.parser.HMSetParser;
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
            HMSetCommand command = hmSetParser.parse(new Object[]{"hmset", "key", "field", "value"});
            assertEquals("key", command.getKey());
            assertEquals(1, command.getFields().size());
        }

        {
            HMSetParser hmSetParser = new HMSetParser();
            HMSetCommand command = hmSetParser.parse(new Object[]{"hmset", "key", "field", "value", "field1", "value1"});
            assertEquals("key", command.getKey());
            assertEquals(2, command.getFields().size());
        }
    }
}