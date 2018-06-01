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

package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupCreateCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupDelConsumerCommand;

import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.eq;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.toRune;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class XGroupParser implements CommandParser<XGroupCommand> {
    @Override
    public XGroupCommand parse(Object[] command) {
        int idx = 1;
        String next = toRune(command[idx++]);
        if (eq(next, "CREATE")) {
            String key = toRune(command[idx]);
            byte[] rawKey = toBytes(command[idx]);
            idx++;
            String group = toRune(command[idx]);
            byte[] rawGroup = toBytes(command[idx]);
            idx++;
            String id = toRune(command[idx]);
            byte[] rawId = toBytes(command[idx]);
            idx++;
            return new XGroupCreateCommand(key, group, id, rawKey, rawGroup, rawId);
        } else if (eq(next, "DELCONSUMER")) {
            String key = toRune(command[idx]);
            byte[] rawKey = toBytes(command[idx]);
            idx++;
            String group = toRune(command[idx]);
            byte[] rawGroup = toBytes(command[idx]);
            idx++;
            String consumer = toRune(command[idx]);
            byte[] rawConsumer = toBytes(command[idx]);
            idx++;
            return new XGroupDelConsumerCommand(key, group, consumer, rawKey, rawGroup, rawConsumer);
        } else {
            throw new UnsupportedOperationException(next);
        }
    }
}
