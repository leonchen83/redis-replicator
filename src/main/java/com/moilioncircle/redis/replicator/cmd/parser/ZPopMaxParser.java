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
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toInt;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.ZPopMaxCommand;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class ZPopMaxParser implements CommandParser<ZPopMaxCommand> {

    @Override
    public ZPopMaxCommand parse(Object[] command) {
        int idx = 1;
        byte[] key = toBytes(command[idx]);
        int count = 1;
        idx++;
        if (idx < command.length) {
            count = toInt(command[idx++]);
        }
        return new ZPopMaxCommand(key, count);
    }
}