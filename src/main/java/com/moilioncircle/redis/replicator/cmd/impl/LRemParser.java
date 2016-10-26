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
public class LRemParser implements CommandParser<LRemParser.LRemCommand> {
    @Override
    public LRemCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String key = (String) params[idx++];
        int index = Integer.parseInt((String) params[idx++]);
        String value = (String) params[idx++];
        return new LRemCommand(key, index, value);
    }

    public static class LRemCommand implements Command {
        private final String key;
        private final int index;
        private final String value;

        public String getKey() {
            return key;
        }

        public int getIndex() {
            return index;
        }

        public String getValue() {
            return value;
        }

        public LRemCommand(String key, int index, String value) {
            this.key = key;
            this.index = index;
            this.value = value;
        }

        @Override
        public String toString() {
            return "LRemCommand{" +
                    "key='" + key + '\'' +
                    ", index=" + index +
                    ", value='" + value + '\'' +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("LREM");
        }
    }
}
