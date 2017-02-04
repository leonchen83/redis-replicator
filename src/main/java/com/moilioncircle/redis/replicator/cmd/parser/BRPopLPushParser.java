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
import com.moilioncircle.redis.replicator.cmd.impl.BRPopLPushCommand;

/**
 * Created by leon on 8/19/16.
 */
public class BRPopLPushParser implements CommandParser<BRPopLPushCommand> {
    @Override
    public BRPopLPushCommand parse(Object[] command) {
        int idx = 1;
        String source = (String) command[idx++];
        String destination = (String) command[idx++];
        int timeout = Integer.parseInt((String) command[idx++]);
        return new BRPopLPushCommand(source, destination, timeout);
    }

}
