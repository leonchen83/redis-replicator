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
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.DirectionType;
import com.moilioncircle.redis.replicator.cmd.impl.LMoveCommand;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class LMoveParser implements CommandParser<LMoveCommand> {
    
    @Override
    public LMoveCommand parse(Object[] command) {
        int idx = 1;
        byte[] source = toBytes(command[idx++]);
        byte[] destination = toBytes(command[idx++]);
        DirectionType from = parseDirection(toRune(command[idx++]));
        DirectionType to = parseDirection(toRune(command[idx++]));
        return new LMoveCommand(source, destination, from, to);
    }
    
    private DirectionType parseDirection(String direction) {
        if (isEquals(direction, "LEFT")) {
            return DirectionType.LEFT;
        } else if (isEquals(direction, "RIGHT")) {
            return DirectionType.RIGHT;
        } else {
            throw new AssertionError("parse [LMOVE] command error." + direction);
        }
    }
}
