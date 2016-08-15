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
import com.moilioncircle.redis.replicator.util.ByteBuilder;

import java.io.IOException;

/**
 * Created by leon on 8/13/16.
 */
public interface BulkReplyHandler {
    String handle(long len, RedisInputStream in) throws IOException;

    class SimpleBulkReplyHandler implements BulkReplyHandler {
        @Override
        public String handle(long len, RedisInputStream in) throws IOException {
            ByteBuilder builder = ByteBuilder.allocate(512);
            int c;
            for (int i = 0; i < len; i++) {
                c = in.read();
                builder.put((byte) c);
            }
            if ((c = in.read()) != '\r') throw new AssertionError("Expect '\r' but :" + (char) c);
            if ((c = in.read()) != '\n') throw new AssertionError("Expect '\n' but :" + (char) c);
            //simple reply
            String reply = builder.toString();
            if (builder.length() != len) {
                throw new AssertionError("reply len " + reply.length() + " != bulk len " + len);
            }
            return reply;
        }
    }
}
