package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.BRPopLPushCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/8/17.
 */
public class BRPopLPushParserTest {
    @Test
    public void parse() throws Exception {
        BRPopLPushParser parser = new BRPopLPushParser();
        BRPopLPushCommand cmd = parser.parse(new Object[]{"brpoplpush", "source", "target", "100"});
        assertEquals("source", cmd.getSource());
        assertEquals("target", cmd.getDestination());
        assertEquals(100, cmd.getTimeout());
        System.out.println(cmd);
    }

}