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
import com.moilioncircle.redis.replicator.cmd.impl.FunctionCommand;
import com.moilioncircle.redis.replicator.cmd.impl.FunctionDeleteCommand;
import com.moilioncircle.redis.replicator.cmd.impl.FunctionFlushCommand;
import com.moilioncircle.redis.replicator.cmd.impl.FunctionLoadCommand;
import com.moilioncircle.redis.replicator.cmd.impl.FunctionRestoreCommand;

/**
 * @author Leon Chen
 * @since 3.6.0
 */
public class FunctionParser implements CommandParser<FunctionCommand> {
    @Override
    public FunctionCommand parse(Object[] command) {
        int idx = 1;
        String keyword = toRune(command[idx++]);
        if (isEquals(keyword, "LOAD")) {
            // redis-7.0-RC2
            boolean replace = false;
            byte[] engineName = null;
            byte[] description = null;
            byte[] libraryName = null;
            byte[] functionCode = null;
            if (command.length > 4) {
                engineName = toBytes(command[idx]);
                idx++;
                libraryName = toBytes(command[idx]);
                idx++;
                for (int i = idx; i < command.length; i++) {
                    String token = toRune(command[i]);
                    if (isEquals(token, "REPLACE")) {
                        replace = true;
                    } else if (isEquals(token, "DESCRIPTION")) {
                        i++;
                        description = toBytes(command[i]);
                    } else {
                        functionCode = toBytes(command[i]);
                    }
                }
            } else {
                // redis-7.0
                for (int i = idx; i < command.length; i++) {
                    String token = toRune(command[i]);
                    if (isEquals(token, "REPLACE")) {
                        replace = true;
                    } else {
                        functionCode = toBytes(command[i]);
                    }
                }
            }
            return new FunctionLoadCommand(engineName, libraryName, replace, description, functionCode);
        } else if (isEquals(keyword, "FLUSH")) {
            boolean isAsync = false;
            boolean isSync = false;
            if (idx >= command.length) {
                return new FunctionFlushCommand(false, false);
            } else {
                String value = toRune(command[idx]);
                if (isEquals(value, "ASYNC")) {
                    isAsync = true;
                } else if (isEquals(value, "SYNC")) {
                    isSync = true;
                }
                return new FunctionFlushCommand(isAsync, isSync);
            }
        } else if (isEquals(keyword, "DELETE")) {
            byte[] libraryName = toBytes(command[idx]);
            return new FunctionDeleteCommand(libraryName);
        } else if (isEquals(keyword, "RESTORE")) {
            byte[] serializedValue = toBytes(command[idx++]);
            boolean replace = false;
            boolean flush = false;
            boolean append = false;
            if (idx < command.length) {
                String value = toRune(command[idx]);
                if (isEquals(value, "REPLACE")) {
                    replace = true;
                } else if (isEquals(value, "APPEND")) {
                    append = true;
                } else if (isEquals(value, "FLUSH")) {
                    flush = true;
                }
            }
            return new FunctionRestoreCommand(serializedValue, replace, flush, append);
        }
        throw new AssertionError("FUNCTION " + keyword);
    }


}
