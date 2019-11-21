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

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.util.ByteBuilder;

import java.io.IOException;

import static com.moilioncircle.redis.replicator.Constants.COLON;
import static com.moilioncircle.redis.replicator.Constants.DOLLAR;
import static com.moilioncircle.redis.replicator.Constants.MINUS;
import static com.moilioncircle.redis.replicator.Constants.PLUS;
import static com.moilioncircle.redis.replicator.Constants.STAR;

/**
 * @author Leon Chen
 * @see <a href="http://redis.io/topics/protocol">protocol</a>
 * @since 2.1.0
 */
public class ReplyParser {
    private final RedisCodec codec;
    private final RedisInputStream in;

    public ReplyParser(RedisInputStream in) {
        this(in, null);
    }

    public ReplyParser(RedisInputStream in, RedisCodec codec) {
        this.in = in;
        this.codec = codec;
    }

    public Object parse() throws IOException {
        return parse(new BulkReplyHandler.SimpleBulkReplyHandler(), null);
    }

    public Object parse(OffsetHandler offsetHandler) throws IOException {
        return parse(new BulkReplyHandler.SimpleBulkReplyHandler(), offsetHandler);
    }

    public Object parse(BulkReplyHandler handler, OffsetHandler offsetHandler) throws IOException {
        in.mark();
        Object rs = parse(handler);
        long len = in.unmark();
        if (offsetHandler != null) offsetHandler.handle(len);
        return rs;
    }

    /**
     * @param handler bulk reply handler
     * @return Object[] or byte[] or Long
     * @throws IOException when read timeout
     */
    public Object parse(BulkReplyHandler handler) throws IOException {
        while (true) {
            int c = in.read();
            switch (c) {
                case DOLLAR:
                    // RESP Bulk Strings
                    ByteBuilder builder = ByteBuilder.allocate(128);
                    while (true) {
                        while ((c = in.read()) != '\r') {
                            builder.put((byte) c);
                        }
                        if ((c = in.read()) == '\n') {
                            break;
                        } else {
                            builder.put((byte) c);
                        }
                    }
                    String payload = builder.toString();
                    long len = -1;
                    // disk-less replication
                    // $EOF:<40 bytes delimiter>
                    if (!payload.startsWith("EOF:")) {
                        len = Long.parseLong(builder.toString());
                        // $-1\r\n. this is called null string.
                        // see http://redis.io/topics/protocol
                        if (len == -1) return null;
                    } else {
                        if (handler instanceof BulkReplyHandler.SimpleBulkReplyHandler) {
                            throw new AssertionError("Parse reply for disk-less replication can not use BulkReplyHandler.SimpleBulkReplyHandler.");
                        }
                    }
                    if (handler != null) return handler.handle(len, in);
                    throw new AssertionError("Callback is null");
                case COLON:
                    // RESP Integers
                    builder = ByteBuilder.allocate(128);
                    while (true) {
                        while ((c = in.read()) != '\r') {
                            builder.put((byte) c);
                        }
                        if ((c = in.read()) == '\n') {
                            break;
                        } else {
                            builder.put((byte) c);
                        }
                    }
                    // As integer
                    return Long.parseLong(builder.toString());
                case STAR:
                    // RESP Arrays
                    builder = ByteBuilder.allocate(128);
                    while (true) {
                        while ((c = in.read()) != '\r') {
                            builder.put((byte) c);
                        }
                        if ((c = in.read()) == '\n') {
                            break;
                        } else {
                            builder.put((byte) c);
                        }
                    }
                    len = Long.parseLong(builder.toString());
                    if (len == -1) return null;
                    Object[] ary = new Object[(int) len];
                    for (int i = 0; i < len; i++) {
                        Object obj = parse(new BulkReplyHandler.SimpleBulkReplyHandler());
                        ary[i] = obj;
                    }
                    return ary;
                case PLUS:
                    // RESP Simple Strings
                    builder = ByteBuilder.allocate(128);
                    while (true) {
                        while ((c = in.read()) != '\r') {
                            builder.put((byte) c);
                        }
                        if ((c = in.read()) == '\n') {
                            return builder.array();
                        } else {
                            builder.put((byte) c);
                        }
                    }
                case MINUS:
                    // RESP Errors
                    builder = ByteBuilder.allocate(128);
                    while (true) {
                        while ((c = in.read()) != '\r') {
                            builder.put((byte) c);
                        }
                        if ((c = in.read()) == '\n') {
                            return builder.array();
                        } else {
                            builder.put((byte) c);
                        }
                    }
                case '\n':
                    // skip +CONTINUE\r\n[\n]
                    // skip +FULLRESYNC 8de1787ba490483314a4d30f1c628bc5025eb761 2443808505[\n]$2443808505\r\nxxxxxxxxxxxxxxxx\r\n
                    // At this stage just a newline works as a PING in order to take the connection live
                    // bug fix
                    if (in.isMarked()) {
                        in.mark(Math.max(in.unmark() - 1, 0)); // skip [\n]
                    }
                    break;
                default:
                    throw new AssertionError("expect [$,:,*,+,-] but: " + (char) c);

            }
        }
    }
}
