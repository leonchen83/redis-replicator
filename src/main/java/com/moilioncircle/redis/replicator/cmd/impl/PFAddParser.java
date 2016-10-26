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
public class PFAddParser implements CommandParser<PFAddParser.PFAddCommand> {
    @Override
    public PFAddCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String key = (String) params[idx++];
        String[] elements = new String[params.length - 1];
        for (int i = idx, j = 0; i < params.length; i++, j++) {
            elements[j] = (String) params[idx];
        }
        return new PFAddCommand(key, elements);
    }

    public static class PFAddCommand implements Command {
        private final String key;
        private final String elements[];

        public String getKey() {
            return key;
        }

        public String[] getElements() {
            return elements;
        }

        public PFAddCommand(String key, String... elements) {
            this.key = key;
            this.elements = elements;
        }

        @Override
        public String toString() {
            return "PFAddCommand{" +
                    "key='" + key + '\'' +
                    ", element=" + Arrays.toString(elements) +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("PFADD");
        }
    }
}
