package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.BitOpCommand;
import com.moilioncircle.redis.replicator.cmd.impl.Op;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Baoyi Chen on 2017/2/9.
 */
public class BitOpParserTest {
    @Test
    public void parse() throws Exception {
        BitOpParser parser = new BitOpParser();
        BitOpCommand cmd = parser.parse("bitop and des key1 key2".split(" "));
        assertEquals("des",cmd.getDestkey());
        assertEquals(Op.AND,cmd.getOp());
        assertEquals("key1",cmd.getKeys()[0]);
        assertEquals("key2",cmd.getKeys()[1]);
        System.out.println(cmd);
    }

}