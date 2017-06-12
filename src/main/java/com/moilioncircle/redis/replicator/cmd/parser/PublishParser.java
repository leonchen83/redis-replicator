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

package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.PublishCommand;

import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToBytes;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToString;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class PublishParser implements CommandParser<PublishCommand> {
    @Override
    public PublishCommand parse(Object[] command) {
        int idx = 1;
        String channel = objToString(command[idx]);
        byte[] rawChannel = objToBytes(command[idx]);
        idx++;
        String message = objToString(command[idx]);
        byte[] rawMessage = objToBytes(command[idx]);
        idx++;
        return new PublishCommand(channel, message, rawChannel, rawMessage);
    }

}
