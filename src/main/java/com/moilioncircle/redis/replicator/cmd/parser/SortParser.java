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

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.Limit;
import com.moilioncircle.redis.replicator.cmd.impl.SortCommand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.moilioncircle.redis.replicator.cmd.impl.OrderType.ASC;
import static com.moilioncircle.redis.replicator.cmd.impl.OrderType.DESC;
import static com.moilioncircle.redis.replicator.cmd.impl.OrderType.NONE;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToBytes;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToString;

/**
 * @author Leon Chen
 * @since 2.3.1
 */
public class SortParser implements CommandParser<SortCommand> {
    @Override
    public SortCommand parse(Object[] command) {
        int idx = 1;
        SortCommand sort = new SortCommand();
        String key = objToString(command[idx]);
        byte[] rawKey = objToBytes(command[idx]);
        idx++;
        sort.setKey(key);
        sort.setRawKey(rawKey);
        sort.setOrder(NONE);
        List<String> getPatterns = new ArrayList<>();
        List<byte[]> rawGetPatterns = new ArrayList<>();
        while (idx < command.length) {
            String param = objToString(command[idx]);
            if ("ASC".equalsIgnoreCase(param)) {
                sort.setOrder(ASC);
            } else if ("DESC".equalsIgnoreCase(param)) {
                sort.setOrder(DESC);
            } else if ("ALPHA".equalsIgnoreCase(param)) {
                sort.setAlpha(true);
            } else if ("LIMIT".equalsIgnoreCase(param) && idx + 2 < command.length) {
                idx++;
                long offset = new BigDecimal(objToString(command[idx])).longValueExact();
                idx++;
                long count = new BigDecimal(objToString(command[idx])).longValueExact();
                sort.setLimit(new Limit(offset, count));
            } else if ("STORE".equalsIgnoreCase(param) && idx + 1 < command.length) {
                idx++;
                String destination = objToString(command[idx]);
                byte[] rawDestination = objToBytes(command[idx]);
                sort.setDestination(destination);
                sort.setRawDestination(rawDestination);
            } else if ("BY".equalsIgnoreCase(param) && idx + 1 < command.length) {
                idx++;
                String byPattern = objToString(command[idx]);
                byte[] rawByPattern = objToBytes(command[idx]);
                sort.setByPattern(byPattern);
                sort.setRawByPattern(rawByPattern);
            } else if ("GET".equalsIgnoreCase(param) && idx + 1 < command.length) {
                idx++;
                String getPattern = objToString(command[idx]);
                byte[] rawGetPattern = objToBytes(command[idx]);
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
