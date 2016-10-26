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
public class MoveParser implements CommandParser<MoveParser.MoveCommand> {
    @Override
    public MoveCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String key = (String) params[idx++];
        int db = Integer.parseInt((String) params[idx++]);
        return new MoveCommand(key, db);
    }

    public static class MoveCommand implements Command {
        private final String key;
        private final int db;

        public String getKey() {
            return key;
        }

        public int getDb() {
            return db;
        }

        public MoveCommand(String key, int db) {
            this.key = key;
            this.db = db;
        }

        @Override
        public String toString() {
            return "MoveCommand{" +
                    "key='" + key + '\'' +
                    ", db=" + db +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("MOVE");
        }
    }
}
