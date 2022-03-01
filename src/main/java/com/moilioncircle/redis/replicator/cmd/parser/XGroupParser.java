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

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toLong;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupCreateCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupCreateConsumerCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupDelConsumerCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupDestroyCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XGroupSetIdCommand;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class XGroupParser implements CommandParser<XGroupCommand> {
    @Override
    public XGroupCommand parse(Object[] command) {
        int idx = 1;
        String next = toRune(command[idx++]);
        if (isEquals(next, "CREATE")) {
            byte[] key = toBytes(command[idx]);
            idx++;
            byte[] group = toBytes(command[idx]);
            idx++;
            byte[] id = toBytes(command[idx]);
            idx++;
            boolean mkStream = false;
            Long entriesRead = null;
            while (idx < command.length) {
                next = toRune(command[idx++]);
                if (isEquals(next, "MKSTREAM")) {
                    mkStream = true;
                } else if (isEquals(next, "ENTRIESREAD")) {
                    entriesRead = toLong(command[idx++]);
                } else {
                    throw new UnsupportedOperationException(next);
                }
            }
            return new XGroupCreateCommand(key, group, id, mkStream, entriesRead);
        } else if (isEquals(next, "SETID")) {
            byte[] key = toBytes(command[idx]);
            idx++;
            byte[] group = toBytes(command[idx]);
            idx++;
            byte[] id = toBytes(command[idx]);
            idx++;
            Long entriesRead = null;
            while (idx < command.length) {
                next = toRune(command[idx++]);
                if (isEquals(next, "ENTRIESREAD")) {
                    entriesRead = toLong(command[idx++]);
                } else {
                    throw new UnsupportedOperationException(next);
                }
            }
            return new XGroupSetIdCommand(key, group, id, entriesRead);
        } else if (isEquals(next, "DESTROY")) {
            byte[] key = toBytes(command[idx]);
            idx++;
            byte[] group = toBytes(command[idx]);
            idx++;
            return new XGroupDestroyCommand(key, group);
        } else if (isEquals(next, "CREATECONSUMER")) {
            byte[] key = toBytes(command[idx]);
            idx++;
            byte[] group = toBytes(command[idx]);
            idx++;
            byte[] consumer = toBytes(command[idx]);
            idx++;
            return new XGroupCreateConsumerCommand(key, group, consumer);
        } else if (isEquals(next, "DELCONSUMER")) {
            byte[] key = toBytes(command[idx]);
            idx++;
            byte[] group = toBytes(command[idx]);
            idx++;
            byte[] consumer = toBytes(command[idx]);
            idx++;
            return new XGroupDelConsumerCommand(key, group, consumer);
        } else {
            throw new UnsupportedOperationException(next);
        }
    }
}
