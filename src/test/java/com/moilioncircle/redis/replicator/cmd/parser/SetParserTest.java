package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/8/17.
 */
public class SetParserTest {
    @Test
    public void parse() throws Exception {
        SetParser parser = new SetParser();
        SetCommand cmd = parser.parse("set a b ex 15 nx".split(" "));
        assertEquals("a", cmd.getKey());
        assertEquals("b", cmd.getValue());
        assertEquals(15, cmd.getEx().intValue());
        assertEquals(ExistType.NX, cmd.getExistType());
        System.out.println(cmd);

        cmd = parser.parse("set a b px 123 xx".split(" "));
        assertEquals("a", cmd.getKey());
        assertEquals("b", cmd.getValue());
        assertEquals(123L, cmd.getPx().longValue());
        assertEquals(ExistType.XX, cmd.getExistType());
        System.out.println(cmd);

        cmd = parser.parse("set a b xx px 123".split(" "));
        assertEquals("a", cmd.getKey());
        assertEquals("b", cmd.getValue());
        assertEquals(123L, cmd.getPx().longValue());
        assertEquals(ExistType.XX, cmd.getExistType());
        System.out.println(cmd);

    }

}