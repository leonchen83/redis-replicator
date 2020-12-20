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
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toInt;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.CopyCommand;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class CopyParser implements CommandParser<CopyCommand> {
    
    @Override
    public CopyCommand parse(Object[] command) {
        int idx = 1;
        byte[] source = toBytes(command[idx++]);
        byte[] destination = toBytes(command[idx++]);
        boolean replace = false;
        Integer db = null;
        for (int i = idx; i < command.length; i++) {
            String str = toRune(command[i]);
            if (isEquals(str, "REPLACE")) {
                replace = true;
            } else if (isEquals(str, "DB")) {
                i++;
                db = toInt(command[i]);
            } else {
                throw new AssertionError("parse [COPY] command error." + str);
            }
        }
        return new CopyCommand(source, destination, db, replace);
    }
}
