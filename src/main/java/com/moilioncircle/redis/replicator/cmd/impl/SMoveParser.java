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
public class SMoveParser implements CommandParser<SMoveParser.SMoveCommand> {

    @Override
    public SMoveCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String source = (String) params[idx++];
        String destination = (String) params[idx++];
        String member = (String) params[idx++];
        return new SMoveCommand(source, destination, member);
    }

    public static class SMoveCommand implements Command {
        public final String source;
        public final String destination;
        public final String member;

        public SMoveCommand(String source, String destination, String member) {
            this.source = source;
            this.destination = destination;
            this.member = member;
        }

        @Override
        public String toString() {
            return "SMoveCommand{" +
                    "source='" + source + '\'' +
                    ", destination='" + destination + '\'' +
                    ", member='" + member + '\'' +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("SMOVE");
        }
    }
}
