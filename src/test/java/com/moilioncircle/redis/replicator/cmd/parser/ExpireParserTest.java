package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.ExpireCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/8/17.
 */
public class ExpireParserTest {
    @Test
    public void parse() throws Exception {
        ExpireParser parser = new ExpireParser();
        ExpireCommand cmd = parser.parse("expire key 100".split(" "));
        assertEquals("key", cmd.getKey());
        assertEquals(100, cmd.getEx());
        System.out.println(cmd);
    }

}