package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.SetExCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/8/17.
 */
public class SetExParserTest {
    @Test
    public void parse() throws Exception {
        SetExParser parser = new SetExParser();
        SetExCommand cmd = parser.parse("setex key 100 value".split(" "));
        assertEquals("key", cmd.getKey());
        assertEquals(100, cmd.getEx());
        assertEquals("value", cmd.getValue());
        System.out.println(cmd);
    }

}