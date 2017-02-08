package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.SMoveCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/8/17.
 */
public class SMoveParserTest {
    @Test
    public void parse() throws Exception {
        SMoveParser parser = new SMoveParser();
        SMoveCommand cmd = parser.parse("smove src des field".split(" "));
        assertEquals("src", cmd.getSource());
        assertEquals("des", cmd.getDestination());
        assertEquals("field", cmd.getMember());
        System.out.println(cmd);
    }

}