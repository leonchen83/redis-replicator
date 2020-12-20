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
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

import java.util.ArrayList;
import java.util.List;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.CompareType;
import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.ZAddCommand;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ZAddParser implements CommandParser<ZAddCommand> {

    @Override
    public ZAddCommand parse(Object[] command) {
        int idx = 1;
        boolean isCh = false, isIncr = false;
        ExistType existType = ExistType.NONE;
        CompareType compareType = CompareType.NONE;
        List<ZSetEntry> list = new ArrayList<>();
        byte[] key = toBytes(command[idx]);
        idx++;
        while (idx < command.length) {
            String param = toRune(command[idx]);
            if (isEquals(param, "NX")) {
                existType = ExistType.NX;
            } else if (isEquals(param, "XX")) {
                existType = ExistType.XX;
            } else if (isEquals(param, "CH")) {
                isCh = true;
            } else if (isEquals(param, "INCR")) {
                isIncr = true;
            } else if (isEquals(param, "GT")) {
                compareType = CompareType.GT;
            } else if (isEquals(param, "LT")) {
                compareType = CompareType.LT;
            } else {
                double score = Double.parseDouble(param);
                idx++;
                byte[] member = toBytes(command[idx]);
                list.add(new ZSetEntry(member, score));
            }
            idx++;
        }
        ZSetEntry[] zSetEntries = new ZSetEntry[list.size()];
        list.toArray(zSetEntries);
        return new ZAddCommand(key, existType, compareType, isCh, isIncr, zSetEntries);
    }

}
