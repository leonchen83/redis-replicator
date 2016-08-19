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
public class BRPopLPushParser implements CommandParser<BRPopLPushParser.BRPopLPushCommand> {
    @Override
    public BRPopLPushCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String source = (String) params[idx++];
        String destination = (String) params[idx++];
        int timeout = Integer.parseInt((String) params[idx++]);
        return new BRPopLPushCommand(source, destination, timeout);
    }

    public static class BRPopLPushCommand implements Command {
        public final String source;
        public final String destination;
        public final int timeout;

        public BRPopLPushCommand(String source, String destination, int timeout) {
            this.source = source;
            this.destination = destination;
            this.timeout = timeout;
        }

        @Override
        public String toString() {
            return "BRPopLPushCommand{" +
                    "source='" + source + '\'' +
                    ", destination='" + destination + '\'' +
                    ", timeout=" + timeout +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("BRPOPLPUSH");
        }
    }
}
