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

import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.ZAddCommand;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leon on 8/19/16.
 */
public class ZAddParser implements CommandParser<ZAddCommand> {

    @Override
    public ZAddCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        Boolean isCh = null, isIncr = null;
        ExistType existType = ExistType.NONE;
        List<ZSetEntry> list = new ArrayList<>();
        String key = (String) params[idx++];
        while (idx < params.length) {
            String param = (String) params[idx];
            if (param.equalsIgnoreCase("NX")) {
                existType = ExistType.NX;
            } else if (param.equalsIgnoreCase("XX")) {
                existType = ExistType.XX;
            } else if (param.equalsIgnoreCase("CH")) {
                isCh = true;
            } else if (param.equalsIgnoreCase("INCR")) {
                isIncr = true;
            } else {
                double score = Double.parseDouble(param);
                idx++;
                String member = (String) params[idx];
                list.add(new ZSetEntry(member, score));
            }
            idx++;
        }
        ZSetEntry[] zSetEntries = new ZSetEntry[list.size()];
        list.toArray(zSetEntries);
        return new ZAddCommand(key, existType, isCh, isIncr, zSetEntries);
    }

}
