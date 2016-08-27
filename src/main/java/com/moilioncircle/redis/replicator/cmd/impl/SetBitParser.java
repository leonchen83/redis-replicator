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
 * Created by leon on 8/14/16.
 */
public class SetBitParser implements CommandParser<SetBitParser.SetBitCommand> {
    @Override
    public SetBitCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String key = (String) params[idx++];
        int offset = Integer.parseInt((String) params[idx++]);
        int value = Integer.parseInt((String) params[idx++]);
        return new SetBitCommand(key, offset, value);
    }

    public static class SetBitCommand implements Command {
        public final String key;
        public final int offset;
        public final int value;

        public SetBitCommand(String key, int offset, int value) {
            this.key = key;
            this.offset = offset;
            this.value = value;
        }

        @Override
        public String toString() {
            return "SetBitCommand{" +
                    "key='" + key + '\'' +
                    ", offset=" + offset +
                    ", value=" + value +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("SETBIT");
        }
    }
}
