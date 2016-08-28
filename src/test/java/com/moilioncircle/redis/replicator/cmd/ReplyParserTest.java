package com.moilioncircle.redis.replicator.cmd;

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * Created by leon on 8/28/16.
 */
public class ReplyParserTest {

    @Test
    public void testParse() throws Exception {
        {
            RedisInputStream in = new RedisInputStream(new ByteArrayInputStream(":56789\r\n".getBytes()));
            ReplyParser replyParser = new ReplyParser(in);
            Long r = (Long) replyParser.parse(new BulkReplyHandler.SimpleBulkReplyHandler());
            System.out.println(r);
        }

    }
}