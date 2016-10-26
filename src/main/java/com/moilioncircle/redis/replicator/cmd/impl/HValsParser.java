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
public class HValsParser implements CommandParser<HValsParser.HValsCommand> {
    @Override
    public HValsCommand parse(CommandName cmdName, Object[] params) {
        String key = (String) params[0];
        return new HValsCommand(key);
    }

    public static class HValsCommand implements Command {
        private final String key;

        public String getKey() {
            return key;
        }

        public HValsCommand(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return "HValsCommand{" +
                    "key='" + key + '\'' +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("HVALS");
        }
    }
}
