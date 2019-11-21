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

import java.io.IOException;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@FunctionalInterface
public interface BulkReplyHandler {
    byte[] handle(long len, RedisInputStream in) throws IOException;

    class SimpleBulkReplyHandler implements BulkReplyHandler {

        private final RedisCodec codec;

        public SimpleBulkReplyHandler() {
            this.codec = null;
        }

        public SimpleBulkReplyHandler(RedisCodec codec) {
            this.codec = codec;
        }

        @Override
        public byte[] handle(long len, RedisInputStream in) throws IOException {
            byte[] reply = len == 0 ? new byte[]{} : in.readBytes(len).first();
            int c;
            if ((c = in.read()) != '\r') throw new AssertionError("expect '\\r' but :" + (char) c);
            if ((c = in.read()) != '\n') throw new AssertionError("expect '\\n' but :" + (char) c);
            return reply;
        }
    }
}
