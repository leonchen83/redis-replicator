/*
 * Copyright 2016-2018 Leon Chen
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

package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.DefaultCommand;

import static com.moilioncircle.redis.replicator.util.Strings.format;

/**
 * @author Leon Chen
 * @since 2.5.0
 */
public class DefaultCommandParser implements CommandParser<DefaultCommand> {
    @Override
    public DefaultCommand parse(Object[] command) {
        byte[][] args = new byte[command.length - 1][];
        for (int i = 1, j = 0; i < command.length; i++) {
            if (command[i] instanceof Long) {
                args[j++] = String.valueOf(command[i]).getBytes();
            } else if (command[i] instanceof byte[]) {
                args[j++] = (byte[]) command[i];
            } else if (command[i] instanceof Object[]) {
                throw new UnsupportedOperationException(format(command));
            }
        }
        return new DefaultCommand((byte[]) command[0], args);
    }
}
