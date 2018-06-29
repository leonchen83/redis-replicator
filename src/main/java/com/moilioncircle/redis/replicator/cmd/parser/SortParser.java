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
import com.moilioncircle.redis.replicator.cmd.impl.Limit;
import com.moilioncircle.redis.replicator.cmd.impl.SortCommand;

import java.util.ArrayList;
import java.util.List;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toLong;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.cmd.impl.OrderType.ASC;
import static com.moilioncircle.redis.replicator.cmd.impl.OrderType.DESC;
import static com.moilioncircle.redis.replicator.cmd.impl.OrderType.NONE;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

/**
 * @author Leon Chen
 * @since 2.3.1
 */
public class SortParser implements CommandParser<SortCommand> {
    @Override
    public SortCommand parse(Object[] command) {
        int idx = 1;
        SortCommand sort = new SortCommand();
        byte[] key = toBytes(command[idx]);
        idx++;
        sort.setKey(key);
        sort.setOrder(NONE);
        List<byte[]> getPatterns = new ArrayList<>();
        while (idx < command.length) {
            String param = toRune(command[idx]);
            if (isEquals(param, "ASC")) {
                sort.setOrder(ASC);
            } else if (isEquals(param, "DESC")) {
                sort.setOrder(DESC);
            } else if (isEquals(param, "ALPHA")) {
                sort.setAlpha(true);
            } else if (isEquals(param, "LIMIT") && idx + 2 < command.length) {
                idx++;
                long offset = toLong(command[idx]);
                idx++;
                long count = toLong(command[idx]);
                sort.setLimit(new Limit(offset, count));
            } else if (isEquals(param, "STORE") && idx + 1 < command.length) {
                idx++;
                byte[] destination = toBytes(command[idx]);
                sort.setDestination(destination);
            } else if (isEquals(param, "BY") && idx + 1 < command.length) {
                idx++;
                byte[] byPattern = toBytes(command[idx]);
                sort.setByPattern(byPattern);
            } else if (isEquals(param, "GET") && idx + 1 < command.length) {
                idx++;
                byte[] getPattern = toBytes(command[idx]);
                getPatterns.add(getPattern);
            }
            idx++;
        }
        sort.setGetPatterns(getPatterns.toArray(new byte[getPatterns.size()][]));
        return sort;
    }
}
