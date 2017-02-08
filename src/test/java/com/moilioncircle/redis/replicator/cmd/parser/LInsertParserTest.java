package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.LInsertCommand;
import com.moilioncircle.redis.replicator.cmd.impl.LInsertType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/8/17.
 */
public class LInsertParserTest {
    @Test
    public void parse() throws Exception {
        LInsertParser parser = new LInsertParser();
        LInsertCommand cmd = parser.parse("LINSERT mylist BEFORE World There".split(" "));
        assertEquals("mylist", cmd.getKey());
        assertEquals(LInsertType.BEFORE, cmd.getlInsertType());
        assertEquals("World", cmd.getPivot());
        assertEquals("There", cmd.getValue());
        System.out.println(cmd);

        cmd = parser.parse("LINSERT mylist AFTER World There".split(" "));
        assertEquals("mylist", cmd.getKey());
        assertEquals(LInsertType.AFTER, cmd.getlInsertType());
        assertEquals("World", cmd.getPivot());
        assertEquals("There", cmd.getValue());

        System.out.println(cmd);
    }

}