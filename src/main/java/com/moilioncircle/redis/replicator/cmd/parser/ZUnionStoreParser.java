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
import com.moilioncircle.redis.replicator.cmd.impl.AggregateType;
import com.moilioncircle.redis.replicator.cmd.impl.ZUnionStoreCommand;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toDouble;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toInt;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ZUnionStoreParser implements CommandParser<ZUnionStoreCommand> {
    @Override
    public ZUnionStoreCommand parse(Object[] command) {
        int idx = 1;
        AggregateType aggregateType = null;
        byte[] destination = toBytes(command[idx]);
        idx++;
        int numkeys = toInt(command[idx++]);
        byte[][] keys = new byte[numkeys][];
        for (int i = 0; i < numkeys; i++) {
            keys[i] = toBytes(command[idx]);
            idx++;
        }
        double[] weights = null;
        while (idx < command.length) {
            String param = toRune(command[idx]);
            if (isEquals(param, "WEIGHTS")) {
                idx++;
                weights = new double[numkeys];
                for (int i = 0; i < numkeys; i++) {
                    weights[i] = toDouble(command[idx++]);
                }
            } else if (isEquals(param, "AGGREGATE")) {
                idx++;
                String next = toRune(command[idx++]);
                if (isEquals(next, "SUM")) {
                    aggregateType = AggregateType.SUM;
                } else if (isEquals(next, "MIN")) {
                    aggregateType = AggregateType.MIN;
                } else if (isEquals(next, "MAX")) {
                    aggregateType = AggregateType.MAX;
                }
            }
        }
        return new ZUnionStoreCommand(destination, numkeys, keys, weights, aggregateType);
    }

}
