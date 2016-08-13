/*
 * Copyright 2016 leon chen
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

import com.moilioncircle.redis.replicator.io.RedisInputStream;

import java.io.IOException;

import static com.moilioncircle.redis.replicator.Constants.*;

/**
 * Created by leon on 8/13/16.
 */
public class ReplyParser {
    private final RedisInputStream in;

    public ReplyParser(RedisInputStream in) {
        this.in = in;
    }

    public Object parse() throws IOException {
        return parse(new BulkReplyHandler.SimpleBulkReplyHandler());
    }

    /**
     * @param handler
     * @return return Object[] or String or Long
     * @throws IOException
     */
    public Object parse(BulkReplyHandler handler) throws IOException {
        char c = (char) in.read();
        switch (c) {
            case DOLLAR:
                //RESP Bulk Strings
                StringBuilder builder = new StringBuilder();
                while (true) {
                    while ((c = (char) in.read()) != '\r') {
                        builder.append(c);
                    }
                    if ((c = (char) in.read()) == '\n') {
                        break;
                    } else {
                        builder.append(c);
                    }
                }
                long len = Long.parseLong(builder.toString());
                // $-1\r\n. this is called null string.
                // see http://redis.io/topics/protocol
                if (len == -1) return null;
                if (handler != null) return handler.handle(len, in);
                throw new AssertionError("callback is null");
            case COLON:
                // RESP Integers
                builder = new StringBuilder();
                while (true) {
                    while ((c = (char) in.read()) != '\r') {
                        builder.append(c);
                    }
                    if ((c = (char) in.read()) == '\n') {
                        break;
                    } else {
                        builder.append(c);
                    }
                }
                //as integer
                return Long.parseLong(builder.toString());
            case STAR:
                // RESP Arrays
                builder = new StringBuilder();
                while (true) {
                    while ((c = (char) in.read()) != '\r') {
                        builder.append(c);
                    }
                    if ((c = (char) in.read()) == '\n') {
                        break;
                    } else {
                        builder.append(c);
                    }
                }
                len = Long.parseLong(builder.toString());
                if (len == -1) {
                    return null;
                }
                Object[] ary = new Object[(int) len];
                for (int i = 0; i < len; i++) {
                    Object obj = parse();
                    ary[i] = obj;
                }
                return ary;
            case PLUS:
                // RESP Simple Strings
                builder = new StringBuilder();
                while (true) {
                    while ((c = (char) in.read()) != '\r') {
                        builder.append(c);
                    }
                    if ((c = (char) in.read()) == '\n') {
                        return builder.toString();
                    } else {
                        builder.append(c);
                    }
                }
            case MINUS:
                // RESP Errors
                builder = new StringBuilder();
                while (true) {
                    while ((c = (char) in.read()) != '\r') {
                        builder.append(c);
                    }
                    if ((c = (char) in.read()) == '\n') {
                        return builder.toString();
                    } else {
                        builder.append(c);
                    }
                }
            default:
                throw new AssertionError("Expect [$,:,*,+,-] but: 0x" + Integer.toHexString(c));

        }
    }
}
