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

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toLong;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.CompareType;
import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.PExpireCommand;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class PExpireParser implements CommandParser<PExpireCommand> {
    @Override
    public PExpireCommand parse(Object[] command) {
        int idx = 1;
        byte[] key = toBytes(command[idx]);
        idx++;
        long ex = toLong(command[idx++]);
        
        ExistType existType = ExistType.NONE;
        CompareType compareType = CompareType.NONE;
        while (idx < command.length) {
            String param = toRune(command[idx]);
            if (isEquals(param, "NX")) {
                existType = ExistType.NX;
            } else if (isEquals(param, "XX")) {
                existType = ExistType.XX;
            } else if (isEquals(param, "GT")) {
                compareType = CompareType.GT;
            } else if (isEquals(param, "LT")) {
                compareType = CompareType.LT;
            }
            idx++;
        }
        return new PExpireCommand(key, ex, existType, compareType);
    }

}
