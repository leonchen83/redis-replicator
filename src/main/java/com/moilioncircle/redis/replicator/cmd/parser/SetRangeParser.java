/*
 * Copyright 2016-2017 Leon Chen
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
import com.moilioncircle.redis.replicator.cmd.impl.SetRangeCommand;

import java.math.BigDecimal;

import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToBytes;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToString;


/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class SetRangeParser implements CommandParser<SetRangeCommand> {
    @Override
    public SetRangeCommand parse(Object[] command) {
        int idx = 1;
        String key = objToString(command[idx]);
        byte[] rawKey = objToBytes(command[idx]);
        idx++;
        long index = new BigDecimal(objToString(command[idx++])).longValueExact();
        String value = objToString(command[idx]);
        byte[] rawValue = objToBytes(command[idx]);
        idx++;
        return new SetRangeCommand(key, index, value, rawKey, rawValue);
    }

}
