package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Baoyi Chen on 2017/2/9.
 */
public class DecrByParserTest {
    @Test
    public void parse() throws Exception {
        {
            DecrByParser parser = new DecrByParser();
            DecrByCommand cmd = parser.parse("decrby key 5".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals(5,cmd.getValue());
            System.out.println(cmd);
        }

        {
            IncrByParser parser = new IncrByParser();
            IncrByCommand cmd = parser.parse("incrby key 5".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals(5,cmd.getValue());
            System.out.println(cmd);
        }

        {
            DecrParser parser = new DecrParser();
            DecrCommand cmd = parser.parse("decr key".split(" "));
            assertEquals("key",cmd.getKey());
            System.out.println(cmd);
        }

        {
            IncrParser parser = new IncrParser();
            IncrCommand cmd = parser.parse("incr key".split(" "));
            assertEquals("key",cmd.getKey());
            System.out.println(cmd);
        }

        {
            ZIncrByParser parser = new ZIncrByParser();
            ZIncrByCommand cmd = parser.parse("zincrby key 5 mem".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals(5,cmd.getIncrement(),0);
            assertEquals("mem",cmd.getMember());
            System.out.println(cmd);
        }

        {
            HIncrByParser parser = new HIncrByParser();
            HIncrByCommand cmd = parser.parse("hincrby key mem 5".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals(5,cmd.getIncrement());
            assertEquals("mem",cmd.getField());
            System.out.println(cmd);
        }
    }

}