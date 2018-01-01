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
import com.moilioncircle.redis.replicator.cmd.impl.ZInterStoreCommand;

import java.math.BigDecimal;

import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToBytes;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToString;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ZInterStoreParser implements CommandParser<ZInterStoreCommand> {
    @Override
    public ZInterStoreCommand parse(Object[] command) {
        int idx = 1;
        AggregateType aggregateType = null;
        String destination = objToString(command[idx]);
        byte[] rawDestination = objToBytes(command[idx]);
        idx++;
        int numkeys = new BigDecimal(objToString(command[idx++])).intValueExact();
        String[] keys = new String[numkeys];
        byte[][] rawKeys = new byte[numkeys][];
        for (int i = 0; i < numkeys; i++) {
            keys[i] = objToString(command[idx]);
            rawKeys[i] = objToBytes(command[idx]);
            idx++;
        }
        double[] weights = null;
        while (idx < command.length) {
            String param = objToString(command[idx]);
            if ("WEIGHTS".equalsIgnoreCase(param)) {
                idx++;
                weights = new double[numkeys];
                for (int i = 0; i < numkeys; i++) {
                    weights[i] = Double.parseDouble(objToString(command[idx++]));
                }
            }
            if ("AGGREGATE".equalsIgnoreCase(param)) {
                idx++;
                String next = objToString(command[idx++]);
                if ("SUM".equalsIgnoreCase(next)) {
                    aggregateType = AggregateType.SUM;
                } else if ("MIN".equalsIgnoreCase(next)) {
                    aggregateType = AggregateType.MIN;
                } else if ("MAX".equalsIgnoreCase(next)) {
                    aggregateType = AggregateType.MAX;
                }
            }
        }
        return new ZInterStoreCommand(destination, numkeys, keys, weights, aggregateType, rawDestination, rawKeys);
    }

}
