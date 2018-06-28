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
import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.eq;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class SetParser implements CommandParser<SetCommand> {

    @Override
    public SetCommand parse(Object[] command) {
        String key = toRune(command[1]);
        byte[] rawKey = toBytes(command[1]);
        String value = toRune(command[2]);
        byte[] rawValue = toBytes(command[2]);
        int idx = 3;
        ExistType existType = ExistType.NONE;
        Integer ex = null;
        Long px = null;
        boolean et = false, st = false;
        while (idx < command.length) {
            String param = toRune(command[idx++]);
            if (!et && eq(param, "NX")) {
                existType = ExistType.NX;
                et = true;
            } else if (!et && eq(param, "XX")) {
                existType = ExistType.XX;
                et = true;
            }

            if (!st && eq(param, "EX")) {
                ex = Integer.valueOf(toRune(command[idx++]));
                st = true;
            } else if (!st && eq(param, "PX")) {
                px = Long.valueOf(toRune(command[idx++]));
                st = true;
            }
        }
        return new SetCommand(key, value, ex, px, existType, rawKey, rawValue);
    }

}
