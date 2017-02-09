package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Baoyi Chen on 2017/2/9.
 */
public class DelParserTest {
    @Test
    public void parse() throws Exception {
        {
            DelParser parser = new DelParser();
            DelCommand cmd = parser.parse("del key1 key2".split(" "));
            assertEquals("key1",cmd.getKeys()[0]);
            assertEquals("key2",cmd.getKeys()[1]);
            System.out.println(cmd);
            UnLinkParser parser1 = new UnLinkParser();
            UnLinkCommand cmd1 = parser1.parse("unlink key1 key2".split(" "));
            assertEquals("key1",cmd1.getKeys()[0]);
            assertEquals("key2",cmd1.getKeys()[1]);
            System.out.println(cmd1);
        }

        {
            HDelParser parser = new HDelParser();
            HDelCommand cmd = parser.parse("hdel key f1 f2".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals("f1",cmd.getFields()[0]);
            assertEquals("f2",cmd.getFields()[1]);
            System.out.println(cmd);
        }

        {
            LRemParser parser = new LRemParser();
            LRemCommand cmd = parser.parse("lrem key 1 val".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals("val",cmd.getValue());
            assertEquals(1,cmd.getIndex());
            System.out.println(cmd);
        }

        {
            SRemParser parser = new SRemParser();
            SRemCommand cmd = parser.parse("srem key m1 m2".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals("m1",cmd.getMembers()[0]);
            assertEquals("m2",cmd.getMembers()[1]);
            System.out.println(cmd);
        }

        {
            ZRemParser parser = new ZRemParser();
            ZRemCommand cmd = parser.parse("zrem key m1 m2".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals("m1",cmd.getMembers()[0]);
            assertEquals("m2",cmd.getMembers()[1]);
            System.out.println(cmd);
        }
    }

}