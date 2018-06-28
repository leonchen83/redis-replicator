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
import static com.moilioncircle.redis.replicator.util.Strings.eq;

/**
 * @author Leon Chen
 * @since 2.3.1
 */
public class SortParser implements CommandParser<SortCommand> {
    @Override
    public SortCommand parse(Object[] command) {
        int idx = 1;
        SortCommand sort = new SortCommand();
        String key = toRune(command[idx]);
        byte[] rawKey = toBytes(command[idx]);
        idx++;
        sort.setKey(key);
        sort.setRawKey(rawKey);
        sort.setOrder(NONE);
        List<String> getPatterns = new ArrayList<>();
        List<byte[]> rawGetPatterns = new ArrayList<>();
        while (idx < command.length) {
            String param = toRune(command[idx]);
            if (eq(param, "ASC")) {
                sort.setOrder(ASC);
            } else if (eq(param, "DESC")) {
                sort.setOrder(DESC);
            } else if (eq(param, "ALPHA")) {
                sort.setAlpha(true);
            } else if (eq(param, "LIMIT") && idx + 2 < command.length) {
                idx++;
                long offset = toLong(command[idx]);
                idx++;
                long count = toLong(command[idx]);
                sort.setLimit(new Limit(offset, count));
            } else if (eq(param, "STORE") && idx + 1 < command.length) {
                idx++;
                String destination = toRune(command[idx]);
                byte[] rawDestination = toBytes(command[idx]);
                sort.setDestination(destination);
                sort.setRawDestination(rawDestination);
            } else if (eq(param, "BY") && idx + 1 < command.length) {
                idx++;
                String byPattern = toRune(command[idx]);
                byte[] rawByPattern = toBytes(command[idx]);
                sort.setByPattern(byPattern);
                sort.setRawByPattern(rawByPattern);
            } else if (eq(param, "GET") && idx + 1 < command.length) {
                idx++;
                String getPattern = toRune(command[idx]);
                byte[] rawGetPattern = toBytes(command[idx]);
                getPatterns.add(getPattern);
                rawGetPatterns.add(rawGetPattern);
            }
            idx++;
        }
        sort.setGetPatterns(getPatterns.toArray(new String[getPatterns.size()]));
        sort.setRawGetPatterns(rawGetPatterns.toArray(new byte[rawGetPatterns.size()][]));
        return sort;
    }
}
