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

package com.moilioncircle.examples.migration.cmd;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import redis.clients.jedis.Protocol;

import java.nio.charset.StandardCharsets;

/**
 * @author Leon Chen
 * @since 2.4.3
 */
public class DefaultCommandParser implements CommandParser<DefaultCommand> {
    @Override
    public DefaultCommand parse(Object[] command) {
        Protocol.Command cmd = Protocol.Command.valueOf(new String((byte[]) command[0], StandardCharsets.UTF_8).toUpperCase());
        byte[][] args = new byte[command.length - 1][];
        for (int i = 1, j = 0; i < command.length; i++) {
            args[j++] = (byte[]) command[i];
        }
        return new DefaultCommand(cmd, args);
    }
}
