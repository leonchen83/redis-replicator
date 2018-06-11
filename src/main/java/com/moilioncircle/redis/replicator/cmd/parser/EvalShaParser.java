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
import com.moilioncircle.redis.replicator.cmd.impl.EvalShaCommand;

import java.util.ArrayList;
import java.util.List;

import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.toInt;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.toRune;

/**
 * @author Leon Chen
 * @since 2.4.7
 */
public class EvalShaParser implements CommandParser<EvalShaCommand> {
    @Override
    public EvalShaCommand parse(Object[] command) {
        int idx = 1;
        String sha = toRune(command[idx]);
        byte[] rawSha = toBytes(command[idx]);
        idx++;
        int numkeys = toInt(command[idx++]);
        String[] keys = new String[numkeys];
        byte[][] rawKeys = new byte[numkeys][];
        for (int i = 0; i < numkeys; i++) {
            keys[i] = toRune(command[idx]);
            rawKeys[i] = toBytes(command[idx]);
            idx++;
        }
        List<String> list = new ArrayList<>();
        List<byte[]> rawList = new ArrayList<>();
        while (idx < command.length) {
            list.add(toRune(command[idx]));
            rawList.add(toBytes(command[idx]));
            idx++;
        }
        String[] args = new String[list.size()];
        byte[][] rawArgs = new byte[rawList.size()][];
        list.toArray(args);
        rawList.toArray(rawArgs);
        return new EvalShaCommand(sha, numkeys, keys, args, rawSha, rawKeys, rawArgs);
    }

}
