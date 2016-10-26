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
public class SUnionStoreParser implements CommandParser<SUnionStoreParser.SUnionStoreCommand> {
    @Override
    public SUnionStoreCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String destination = (String) params[idx++];
        String[] keys = new String[params.length - 1];
        for (int i = idx, j = 0; i < params.length; i++, j++) {
            keys[j] = (String) params[idx];
        }
        return new SUnionStoreCommand(destination, keys);
    }

    public static class SUnionStoreCommand implements Command {
        private final String destination;
        private final String keys[];

        public String getDestination() {
            return destination;
        }

        public String[] getKeys() {
            return keys;
        }

        public SUnionStoreCommand(String destination, String... keys) {
            this.destination = destination;
            this.keys = keys;
        }

        @Override
        public String toString() {
            return "SUnionStoreCommand{" +
                    "destination='" + destination + '\'' +
                    ", key=" + Arrays.toString(keys) +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("SUNIONSTORE");
        }
    }
}
