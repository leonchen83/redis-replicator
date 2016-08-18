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

package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;

/**
 * Created by leon on 8/13/16.
 */
public class FlushAllParser implements CommandParser<FlushAllParser.FlushAllCommand> {
    @Override
    public FlushAllCommand parse(CommandName cmdName, Object[] params) {
        return new FlushAllCommand();
    }

    public static class FlushAllCommand implements Command {
        @Override
        public String toString() {
            return "FlushAllCommand{}";
        }

        @Override
        public CommandName name() {
            return CommandName.name("FLUSHALL");
        }
    }
}
