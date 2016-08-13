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
public class SetParser implements CommandParser<SetParser.SetCommand> {

    @Override
    public SetCommand parse(CommandName cmdName, Object[] params) {
        String key = (String) params[0];
        String value = (String) params[1];
        int idx = 2;
        Boolean isNx = null;
        Boolean isXx = null;
        Integer ex = null;
        Long px = null;
        while (idx < params.length) {
            String param = (String) params[idx++];
            if (param.equalsIgnoreCase("NX")) {
                isNx = true;
                break;
            } else if (param.equalsIgnoreCase("XX")) {
                isXx = true;
                break;
            } else if (param.equalsIgnoreCase("EX")) {
                ex = Integer.valueOf((String) params[idx++]);
                break;
            } else if (param.equalsIgnoreCase("PX")) {
                px = Long.valueOf((String) params[idx++]);
                break;
            }
        }
        return new SetCommand(key, value, isNx, isXx, ex, px);
    }

    public static class SetCommand implements Command {
        public final String key;
        public final String value;
        public final Boolean isNx;
        public final Boolean isXx;
        public final Integer ex;
        public final Long px;

        public SetCommand(String key, String value, Boolean isNx, Boolean isXx, Integer ex, Long px) {
            this.key = key;
            this.value = value;
            this.isNx = isNx;
            this.isXx = isXx;
            this.ex = ex;
            this.px = px;
        }

        @Override
        public String toString() {
            return "SetCommand{" +
                    "name='" + key + '\'' +
                    ", value='" + value + '\'' +
                    ", isNx=" + isNx +
                    ", isXx=" + isXx +
                    ", ex=" + ex +
                    ", px=" + px +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("SET");
        }
    }
}
