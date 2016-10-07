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
 * Created by leon on 8/19/16.
 */
public class LInsertParser implements CommandParser<LInsertParser.LInsertCommand> {
    @Override
    public LInsertCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        LInsertType lInsertType = null;
        String key = (String) params[idx++];
        String keyWord = (String) params[idx++];
        if (keyWord.equalsIgnoreCase("BEFORE")) {
            lInsertType = LInsertType.BEFORE;
        } else if (keyWord.equalsIgnoreCase("AFTER")) {
            lInsertType = LInsertType.AFTER;
        }
        String pivot = (String) params[idx++];
        String value = (String) params[idx++];
        return new LInsertCommand(key, lInsertType, pivot, value);
    }

    public static class LInsertCommand implements Command {
        public final String key;
        public final LInsertType lInsertType;
        public final String pivot;
        public final String value;

        public LInsertCommand(String key, LInsertType lInsertType, String pivot, String value) {
            this.key = key;
            this.pivot = pivot;
            this.value = value;
            this.lInsertType = lInsertType;
        }

        @Override
        public String toString() {
            return "LInsertCommand{" +
                    "key='" + key + '\'' +
                    ", lInsertType=" + lInsertType +
                    ", pivot='" + pivot + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("LINSERT");
        }
    }
}
