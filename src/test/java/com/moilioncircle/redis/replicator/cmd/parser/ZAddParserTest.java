package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.ZAddCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/8/17.
 */
public class ZAddParserTest {
    @Test
    public void parse() throws Exception {
        ZAddParser parser = new ZAddParser();
        ZAddCommand cmd = parser.parse("zadd abc nx ch incr 1 b".split(" "));
        assertEquals("abc", cmd.getKey());
        assertEquals(ExistType.NX, cmd.getExistType());
        assertEquals(Boolean.TRUE, cmd.getCh());
        assertEquals(Boolean.TRUE, cmd.getIncr());
        assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());
        System.out.println(cmd);

        cmd = parser.parse("zadd abc 1 b".split(" "));
        assertEquals("abc", cmd.getKey());
        assertEquals(ExistType.NONE, cmd.getExistType());
        assertEquals(null, cmd.getCh());
        assertEquals(null, cmd.getIncr());
        assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());
        System.out.println(cmd);

        cmd = parser.parse("zadd abc xx 1 b".split(" "));
        assertEquals("abc", cmd.getKey());
        assertEquals(ExistType.XX, cmd.getExistType());
        assertEquals(null, cmd.getCh());
        assertEquals(null, cmd.getIncr());
        assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());
        System.out.println(cmd);

        cmd = parser.parse("zadd abc incr 1 b".split(" "));
        assertEquals("abc", cmd.getKey());
        assertEquals(ExistType.NONE, cmd.getExistType());
        assertEquals(null, cmd.getCh());
        assertEquals(Boolean.TRUE, cmd.getIncr());
        assertEquals(1, cmd.getZSetEntries()[0].getScore(), 0);
        assertEquals("b", cmd.getZSetEntries()[0].getElement());
        System.out.println(cmd);
    }

}