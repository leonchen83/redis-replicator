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
import com.moilioncircle.redis.replicator.cmd.impl.HMSetCommand;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToBytes;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToString;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class HMSetParser implements CommandParser<HMSetCommand> {

    @Override
    public HMSetCommand parse(Object[] command) {
        int idx = 1;
        String key = objToString(command[idx]);
        byte[] rawKey = objToBytes(command[idx]);
        idx++;
        Map<String, String> fields = new LinkedHashMap<>();
        Map<byte[], byte[]> rawFields = new LinkedHashMap<>();
        while (idx < command.length) {
            String field = objToString(command[idx]);
            byte[] rawField = objToBytes(command[idx]);
            idx++;
            String value = idx == command.length ? null : objToString(command[idx]);
            byte[] rawValue = idx == command.length ? null : objToBytes(command[idx]);
            idx++;
            fields.put(field, value);
            rawFields.put(rawField, rawValue);
        }
        return new HMSetCommand(key, fields, rawKey, rawFields);
    }

}
