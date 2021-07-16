/*
 * Copyright 2016-2018 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.cmd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

import com.moilioncircle.redis.replicator.io.RedisInputStream;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ReplyParserTest {

    @Test
    public void testParse() throws Exception {
        {
            RedisInputStream in = new RedisInputStream(new ByteArrayInputStream(":56789\r\n".getBytes()));
            ReplyParser replyParser = new ReplyParser(in, new RedisCodec());
            Long r = (Long) replyParser.parse(new BulkReplyHandler.SimpleBulkReplyHandler());
            assertEquals(56789L, r.longValue());
        }

    }
}