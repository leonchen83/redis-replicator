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

import java.util.Arrays;

/**
 * Created by leon on 8/14/16.
 */
public class SAddParser implements CommandParser<SAddParser.SAddCommand> {


    @Override
    public SAddCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0, newIdx = 0;
        String key = (String) params[idx++];
        String[] members = new String[params.length - 1];
        while (idx < params.length) {
            members[newIdx++] = (String) params[idx++];
        }
        return new SAddCommand(key, members);
    }

    public static class SAddCommand implements Command {
        public final String key;
        public final String[] members;

        public SAddCommand(String key, String... members) {
            this.key = key;
            this.members = members;
        }

        @Override
        public String toString() {
            return "SAddCommand{" +
                    "key='" + key + '\'' +
                    ", members=" + Arrays.toString(members) +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("SADD");
        }
    }
}
