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
public class ZIncrByParser implements CommandParser<ZIncrByParser.ZIncrByCommand> {

    @Override
    public ZIncrByCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String key = (String) params[idx++];
        int increment = Integer.parseInt((String) params[idx++]);
        String member = (String) params[idx++];
        return new ZIncrByCommand(key, increment, member);
    }

    public static class ZIncrByCommand implements Command {
        private final String key;
        private final int increment;
        private final String member;

        public String getKey() {
            return key;
        }

        public int getIncrement() {
            return increment;
        }

        public String getMember() {
            return member;
        }

        public ZIncrByCommand(String key, int increment, String member) {
            this.key = key;
            this.increment = increment;
            this.member = member;
        }

        @Override
        public String toString() {
            return "ZIncrByCommand{" +
                    "key='" + key + '\'' +
                    ", increment='" + increment + '\'' +
                    ", member='" + member + '\'' +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("ZINCRBY");
        }
    }
}
