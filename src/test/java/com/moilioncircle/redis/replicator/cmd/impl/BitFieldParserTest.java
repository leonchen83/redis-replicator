package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.CommandName;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 8/28/16.
 */
public class BitFieldParserTest {

    @Test
    public void testParse() throws Exception {
        {
            BitFieldParser parser = new BitFieldParser();
            BitFieldParser.BitFieldCommand command = parser.parse(CommandName.name("BITFIELD"),
                    new Object[]{"mykey", "overflow", "sat"});
            assertEquals("mykey", command.key);
            assertEquals(0, command.statements.size());
            assertEquals(1, command.overFlows.size());
        }


        //
        {
            BitFieldParser parser = new BitFieldParser();
            BitFieldParser.BitFieldCommand command = parser.parse(CommandName.name("BITFIELD"),
                    new Object[]{"mykey", "incrby", "i5", "100", "1", "overflow", "sat"});
            assertEquals("mykey", command.key);
            assertEquals(1, command.statements.size());
            assertEquals(1, command.overFlows.size());
        }

        //
        {
            BitFieldParser parser = new BitFieldParser();
            BitFieldParser.BitFieldCommand command = parser.parse(CommandName.name("BITFIELD"),
                    new Object[]{"mykey", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "sat"});
            assertEquals("mykey", command.key);
            assertEquals(2, command.statements.size());
            assertEquals(1, command.overFlows.size());
        }

        //
        {
            BitFieldParser parser = new BitFieldParser();
            BitFieldParser.BitFieldCommand command = parser.parse(CommandName.name("BITFIELD"),
                    new Object[]{"mykey", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "fail"});
            assertEquals("mykey", command.key);
            assertEquals(2, command.statements.size());
            assertEquals(1, command.overFlows.size());
        }

        {
            BitFieldParser parser = new BitFieldParser();
            BitFieldParser.BitFieldCommand command = parser.parse(CommandName.name("BITFIELD"),
                    new Object[]{"mykey", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "wrap", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "wrap", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "fail"});
            assertEquals("mykey", command.key);
            assertEquals(2, command.statements.size());
            assertEquals(3, command.overFlows.size());
            assertEquals(2, command.overFlows.get(0).statements.size());
        }

    }
}