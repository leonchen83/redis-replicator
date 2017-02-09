package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.FlushAllCommand;
import com.moilioncircle.redis.replicator.cmd.impl.FlushDBCommand;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Baoyi Chen on 2017/2/9.
 */
public class FlushAllParserTest {
    @Test
    public void parse() throws Exception {
        {
            FlushAllParser parser = new FlushAllParser();
            FlushAllCommand cmd = parser.parse("flushall".split(" "));
            assertEquals(null, cmd.isAsync());

            parser = new FlushAllParser();
            cmd = parser.parse("flushall async".split(" "));
            assertEquals(Boolean.TRUE, cmd.isAsync());
            System.out.println(cmd);
        }

        {
            FlushDBParser parser = new FlushDBParser();
            FlushDBCommand cmd = parser.parse("flushdb".split(" "));
            assertEquals(null, cmd.isAsync());

            parser = new FlushDBParser();
            cmd = parser.parse("flushdb async".split(" "));
            assertEquals(Boolean.TRUE, cmd.isAsync());
            System.out.println(cmd);
        }
    }

}