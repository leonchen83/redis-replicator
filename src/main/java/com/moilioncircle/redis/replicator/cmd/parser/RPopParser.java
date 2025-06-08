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
import com.moilioncircle.redis.replicator.cmd.impl.Count;
import com.moilioncircle.redis.replicator.cmd.impl.RPopCommand;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class RPopParser implements CommandParser<RPopCommand> {
    @Override
    public RPopCommand parse(Object[] command) {
        byte[] key = toBytes(command[1]);
        Count count = null;
        if (command.length == 3) {
            count = new Count(toInt(command[2]));
        }
        return new RPopCommand(key, count);
    }

}
