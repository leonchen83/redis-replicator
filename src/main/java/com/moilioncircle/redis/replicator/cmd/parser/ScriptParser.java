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

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.ScriptCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ScriptFlushCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ScriptLoadCommand;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ScriptParser implements CommandParser<ScriptCommand> {
    @Override
    public ScriptCommand parse(Object[] command) {
        int idx = 1;
        String keyword = toRune(command[idx++]);
        boolean isAsync = false;
        boolean isSync = false;
        if (isEquals(keyword, "LOAD")) {
            byte[] script = toBytes(command[idx]);
            idx++;
            return new ScriptLoadCommand(script);
        } else if (isEquals(keyword, "FLUSH")) {
            if (idx >= command.length) {
                return new ScriptFlushCommand(false, false);
            } else {
                String value = toRune(command[idx]);
                if (isEquals(value, "ASYNC")) {
                    isAsync = true;
                } else if (isEquals(value, "SYNC")) {
                    isSync = true;
                }
                return new ScriptFlushCommand(isAsync, isSync);
            }
        }
        throw new AssertionError("SCRIPT " + keyword);
    }


}
