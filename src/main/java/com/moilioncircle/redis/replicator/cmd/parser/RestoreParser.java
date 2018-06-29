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
import com.moilioncircle.redis.replicator.cmd.impl.RestoreCommand;
import com.moilioncircle.redis.replicator.rdb.datatype.EvictType;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toLong;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class RestoreParser implements CommandParser<RestoreCommand> {
    @Override
    public RestoreCommand parse(Object[] command) {
        int idx = 1;
        byte[] key = toBytes(command[idx]);
        idx++;
        long ttl = toLong(command[idx++]);
        byte[] serializedValue = toBytes(command[idx]);
        idx++;
        boolean replace = false;
        boolean absTtl = false;
        EvictType evictType = EvictType.NONE;
        Long evictValue = null;
        for (; idx < command.length; idx++) {
            if (isEquals(toRune(command[idx]), "REPLACE")) {
                replace = true;
            } else if (isEquals(toRune(command[idx]), "ABSTTL")) {
                absTtl = true;
            } else if (isEquals(toRune(command[idx]), "IDLETIME")) {
                evictType = EvictType.LRU;
                idx++;
                evictValue = toLong(command[idx]);
            } else if (isEquals(toRune(command[idx]), "FREQ")) {
                evictType = EvictType.LFU;
                idx++;
                evictValue = toLong(command[idx]);
            } else {
                throw new UnsupportedOperationException(toRune(command[idx]));
            }
        }
        return new RestoreCommand(key, ttl, serializedValue, replace, absTtl, evictType, evictValue);
    }
    
}
