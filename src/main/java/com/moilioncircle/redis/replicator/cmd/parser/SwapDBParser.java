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
import com.moilioncircle.redis.replicator.cmd.impl.SwapDBCommand;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toInt;

/**
 * @author Leon Chen
 * @since 2.1.1
 */
public class SwapDBParser implements CommandParser<SwapDBCommand> {

    @Override
    public SwapDBCommand parse(Object[] command) {
        int idx = 1;
        int source = toInt(command[idx++]);
        int target = toInt(command[idx++]);
        return new SwapDBCommand(source, target);
    }
}
