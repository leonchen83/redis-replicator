package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Baoyi Chen on 2017/2/9.
 */
public class RPushParserTest {
    @Test
    public void parse() throws Exception {
        {
            RPushParser parser = new RPushParser();
            RPushCommand cmd = parser.parse("rpush key v1 v2".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals("v1",cmd.getValues()[0]);
            assertEquals("v2",cmd.getValues()[1]);
            System.out.println(cmd);
        }

        {
            RPushXParser parser = new RPushXParser();
            RPushXCommand cmd = parser.parse("rpushx key v1".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals("v1",cmd.getValue());
            System.out.println(cmd);
        }

        {
            LPushParser parser = new LPushParser();
            LPushCommand cmd = parser.parse("lpush key v1 v2".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals("v1",cmd.getValues()[0]);
            assertEquals("v2",cmd.getValues()[1]);
            System.out.println(cmd);
        }

        {
            LPushXParser parser = new LPushXParser();
            LPushXCommand cmd = parser.parse("lpushx key v1".split(" "));
            assertEquals("key",cmd.getKey());
            assertEquals("v1",cmd.getValue());
            System.out.println(cmd);
        }

        {
            LPopParser parser = new LPopParser();
            LPopCommand cmd = parser.parse("lpop key".split(" "));
            assertEquals("key",cmd.getKey());
            System.out.println(cmd);
        }

        {
            RPopParser parser = new RPopParser();
            RPopCommand cmd = parser.parse("rpop key".split(" "));
            assertEquals("key",cmd.getKey());
            System.out.println(cmd);
        }
    }

}