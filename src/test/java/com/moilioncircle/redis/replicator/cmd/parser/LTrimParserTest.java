package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.LTrimCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Leon Chen
 * @since 2.3.1
 */
public class LTrimParserTest extends AbstractParserTest {
    @Test
    public void parse() throws Exception {
        LTrimParser parser = new LTrimParser();
        LTrimCommand cmd = parser.parse(toObjectArray("LTRIM mylist 0 99".split(" ")));
        assertEquals("mylist", cmd.getKey());
        assertEquals(0L, cmd.getStart());
        assertEquals(99L, cmd.getStop());
        System.out.println(cmd);
    }

}