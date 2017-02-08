package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.PublishCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/8/17.
 */
public class PublishParserTest {
    @Test
    public void parse() throws Exception {
        PublishParser parser = new PublishParser();
        PublishCommand cmd = parser.parse("publish channel msg".split(" "));
        assertEquals("channel", cmd.getChannel());
        assertEquals("msg", cmd.getMessage());
        System.out.println(cmd);
    }

}