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
import com.moilioncircle.redis.replicator.cmd.impl.MaxLen;
import com.moilioncircle.redis.replicator.cmd.impl.XTrimCommand;

import java.util.Objects;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toLong;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class XTrimParser implements CommandParser<XTrimCommand> {
    @Override
    public XTrimCommand parse(Object[] command) {
        int idx = 1;
        byte[] key = toBytes(command[idx]);
        idx++;
        MaxLen maxLen = null;
        if (isEquals(toRune(command[idx]), "MAXLEN")) {
            idx++;
            boolean approximation = false;
            if (Objects.equals(toRune(command[idx]), "~")) {
                approximation = true;
                idx++;
            } else if (Objects.equals(toRune(command[idx]), "=")) {
                idx++;
            }
            long count = toLong(command[idx]);
            idx++;
            maxLen = new MaxLen(approximation, count);
        }
        return new XTrimCommand(key, maxLen);
    }
}
