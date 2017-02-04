package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.parser.BitFieldParser;
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
            BitFieldCommand command = parser.parse(
                    new Object[]{"bitfield", "mykey", "overflow", "sat"});
            assertEquals("mykey", command.getKey());
            assertEquals(0, command.getStatements().size());
            assertEquals(1, command.getOverFlows().size());
        }


        //
        {
            BitFieldParser parser = new BitFieldParser();
            BitFieldCommand command = parser.parse(
                    new Object[]{"bitfield", "mykey", "incrby", "i5", "100", "1", "overflow", "sat"});
            assertEquals("mykey", command.getKey());
            assertEquals(1, command.getStatements().size());
            assertEquals(1, command.getOverFlows().size());
        }

        //
        {
            BitFieldParser parser = new BitFieldParser();
            BitFieldCommand command = parser.parse(
                    new Object[]{"bitfield", "mykey", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "sat"});
            assertEquals("mykey", command.getKey());
            assertEquals(2, command.getStatements().size());
            assertEquals(1, command.getOverFlows().size());
        }

        //
        {
            BitFieldParser parser = new BitFieldParser();
            BitFieldCommand command = parser.parse(
                    new Object[]{"bitfield", "mykey", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "fail"});
            assertEquals("mykey", command.getKey());
            assertEquals(2, command.getStatements().size());
            assertEquals(1, command.getOverFlows().size());
        }

        {
            BitFieldParser parser = new BitFieldParser();
            BitFieldCommand command = parser.parse(
                    new Object[]{"bitfield", "mykey", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "wrap", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "wrap", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "fail"});
            assertEquals("mykey", command.getKey());
            assertEquals(2, command.getStatements().size());
            assertEquals(3, command.getOverFlows().size());
            assertEquals(2, command.getOverFlows().get(0).getStatements().size());
        }

        {
            BitFieldParser parser = new BitFieldParser();
            BitFieldCommand command = parser.parse(
                    new Object[]{"bitfield", "mykey", "incrby", "i5", "100", "1", "get", "i8", "10", "overflow", "wrap", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "wrap", "incrby", "i5", "100", "1", "set", "i8", "#0", "100", "overflow", "fail"});
            assertEquals("mykey", command.getKey());
            assertEquals(2, command.getStatements().size());
            assertEquals(3, command.getOverFlows().size());
            assertEquals(2, command.getOverFlows().get(0).getStatements().size());
        }

    }
}