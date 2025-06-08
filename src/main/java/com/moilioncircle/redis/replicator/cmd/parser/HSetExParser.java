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

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.FieldExistType;
import com.moilioncircle.redis.replicator.cmd.impl.HSetExCommand;
import com.moilioncircle.redis.replicator.cmd.impl.XATType;
import com.moilioncircle.redis.replicator.rdb.datatype.ExpiredType;

/**
 * @author Leon Chen
 * @since 3.9.0
 */
public class HSetExParser  implements CommandParser<HSetExCommand> {
    
    @Override
    public HSetExCommand parse(Object[] command) {
        byte[] key = toBytes(command[1]);
        byte[] value = toBytes(command[2]);
        int idx = 3;
        FieldExistType existType = FieldExistType.NONE;
        Long expiredValue = null;
        XATType xatType = XATType.NONE;
        Long xatValue = null;
        boolean et = false, st = false;
        boolean keepTtl = false;
        ExpiredType expiredType = ExpiredType.NONE;
        while (idx < command.length) {
            String param = toRune(command[idx++]);
            if (!et && isEquals(param, "FNX")) {
                existType = FieldExistType.FNX;
                et = true;
            } else if (!et && isEquals(param, "FXX")) {
                existType = FieldExistType.FXX;
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
            } else if (!st && isEquals(param, "EXAT")) {
                xatType = XATType.EXAT;
                xatValue = Long.valueOf(toRune(command[idx++]));
                st = true;
            } else if (!st && isEquals(param, "PXAT")) {
                xatType = XATType.PXAT;
                xatValue = Long.valueOf(toRune(command[idx++]));
                st = true;
            }
            
            if (isEquals(param, "FIELDS")) {
                break;
            }
        }
        
        idx += 2; // skip FIELDS numFields
        int n = command.length - idx;
        byte[][] fields = new byte[n / 2][];
        byte[][] values = new byte[n / 2][];
        for (int i = idx, j = 0; i < command.length; i += 2, j++) {
            fields[j] = toBytes(command[i]);
            values[j] = toBytes(command[i + 1]);
        }
        
        return new HSetExCommand(key, fields, values, keepTtl, expiredType, expiredValue, xatType, xatValue, existType);
    }
    
}