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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by leon on 8/14/16.
 */
public class HMSetParser implements CommandParser<HMSetParser.HMSetCommand> {

    @Override
    public HMSetCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String key = (String) params[idx++];
        Map<String, String> fields = new LinkedHashMap<>();
        while (idx < params.length) {
            String field = (String) params[idx++];
            String value = idx == params.length ? null : (String) params[idx++];
            fields.put(field, value);
        }
        return new HMSetCommand(key, fields);
    }

    public static class HMSetCommand implements Command {
        public final String key;
        public final Map<String, String> fields;

        public HMSetCommand(String key, Map<String, String> fields) {
            this.key = key;
            this.fields = fields;
        }

        @Override
        public String toString() {
            return "HMSetCommand{" +
                    "key='" + key + '\'' +
                    ", fields=" + fields +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("HMSET");
        }
    }
}
