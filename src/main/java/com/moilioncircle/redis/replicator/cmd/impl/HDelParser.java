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
public class HDelParser implements CommandParser<HDelParser.HDelCommand> {
    @Override
    public HDelCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String key = (String) params[idx++];
        String[] fields = new String[params.length - 1];
        for (int i = idx, j = 0; i < params.length; i++, j++) {
            fields[j] = (String) params[idx];
        }
        return new HDelCommand(key, fields);
    }

    public static class HDelCommand implements Command {
        private final String key;
        private final String fields[];

        public String getKey() {
            return key;
        }

        public String[] getFields() {
            return fields;
        }

        public HDelCommand(String key, String... fields) {
            this.key = key;
            this.fields = fields;
        }

        @Override
        public String toString() {
            return "HDelCommand{" +
                    "key='" + key + '\'' +
                    ", fields=" + Arrays.toString(fields) +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("HDEL");
        }
    }
}
