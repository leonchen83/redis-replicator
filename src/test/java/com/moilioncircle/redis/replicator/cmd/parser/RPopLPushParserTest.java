package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.RPopLPushCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Leon Chen
 * @since 2.3.1
 */
public class RPopLPushParserTest extends AbstractParserTest {
    @Test
    public void parse() throws Exception {
        RPopLPushParser parser = new RPopLPushParser();
        RPopLPushCommand cmd = parser.parse(toObjectArray("RPOPLPUSH mylist myotherlist".split(" ")));
        assertEquals("mylist", cmd.getSource());
        assertEquals("myotherlist", cmd.getDestination());
        System.out.println(cmd);
    }

}