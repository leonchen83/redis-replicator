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
import com.moilioncircle.redis.replicator.rdb.datatype.ExpiredType;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class SetParser implements CommandParser<SetCommand> {

    @Override
    public SetCommand parse(Object[] command) {
        byte[] key = toBytes(command[1]);
        byte[] value = toBytes(command[2]);
        int idx = 3;
        ExistType existType = ExistType.NONE;
        Long expiredValue = null;
        boolean et = false, st = false;
        boolean keepTtl = false;
        ExpiredType expiredType = ExpiredType.NONE;
        while (idx < command.length) {
            String param = toRune(command[idx++]);
            if (!et && isEquals(param, "NX")) {
                existType = ExistType.NX;
                et = true;
            } else if (!et && isEquals(param, "XX")) {
                existType = ExistType.XX;
                et = true;
            } else if (!keepTtl && isEquals(param, "KEEPTTL")) {
                keepTtl = true;
            }

            if (!st && isEquals(param, "EX")) {
                expiredType = ExpiredType.SECOND;
                expiredValue = Long.valueOf(toRune(command[idx++]));
                st = true;
            } else if (!st && isEquals(param, "PX")) {
                expiredType = ExpiredType.MS;
                expiredValue = Long.valueOf(toRune(command[idx++]));
                st = true;
            }
        }
        return new SetCommand(key, value, keepTtl, expiredType, expiredValue, existType);
    }

}
