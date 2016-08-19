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
public class RestoreParser implements CommandParser<RestoreParser.RestoreCommand> {
    @Override
    public RestoreCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        Boolean isReplace = null;
        String key = (String) params[idx++];
        int ttl = Integer.parseInt((String) params[idx++]);
        String serializedValue = (String) params[idx++];
        if (idx < params.length && ((String) params[idx++]).equalsIgnoreCase("REPLACE")) {
            isReplace = true;
        }
        return new RestoreCommand(key, ttl, serializedValue, isReplace);
    }

    public static class RestoreCommand implements Command {
        public final String key;
        public final int ttl;
        public final String serializedValue;
        public final Boolean isReplace;

        public RestoreCommand(String key, int ttl, String serializedValue, Boolean isReplace) {
            this.key = key;
            this.ttl = ttl;
            this.serializedValue = serializedValue;
            this.isReplace = isReplace;
        }

        @Override
        public String toString() {
            return "RestoreCommand{" +
                    "key='" + key + '\'' +
                    ", ttl=" + ttl +
                    ", serializedValue='" + serializedValue + '\'' +
                    ", isReplace=" + isReplace +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("RESTORE");
        }
    }
}
