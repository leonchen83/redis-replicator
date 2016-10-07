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

package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;

import java.util.Arrays;

/**
 * Created by leon on 8/19/16.
 */
public class ZUnionStoreParser implements CommandParser<ZUnionStoreParser.ZUnionStoreCommand> {
    @Override
    public ZUnionStoreCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        AggregateType aggregateType = null;
        String destination = (String) params[idx++];
        int numkeys = Integer.parseInt((String) params[idx++]);
        String[] keys = new String[numkeys];
        for (int i = 0; i < numkeys; i++) {
            keys[i] = (String) params[idx++];
        }
        double[] weights = null;
        while (idx < params.length) {
            String param = (String) params[idx];
            if (param.equalsIgnoreCase("WEIGHTS")) {
                idx++;
                weights = new double[numkeys];
                for (int i = 0; i < numkeys; i++) {
                    weights[i] = Double.parseDouble((String) params[idx++]);
                }
            } else if (param.equalsIgnoreCase("AGGREGATE")) {
                idx++;
                String next = (String) params[idx++];
                if (next.equalsIgnoreCase("SUM")) {
                    aggregateType = AggregateType.SUM;
                } else if (next.equalsIgnoreCase("MIN")) {
                    aggregateType = AggregateType.MIN;
                } else if (next.equalsIgnoreCase("MAX")) {
                    aggregateType = AggregateType.MAX;
                }
            }
        }
        return new ZUnionStoreCommand(destination, numkeys, keys, weights, aggregateType);
    }

    public static class ZUnionStoreCommand implements Command {
        public final String destination;
        public final int numkeys;
        public final String[] keys;
        public final double[] weights;
        public final AggregateType aggregateType;

        public ZUnionStoreCommand(String destination, int numkeys, String[] keys, double[] weights, AggregateType aggregateType) {
            this.destination = destination;
            this.numkeys = numkeys;
            this.keys = keys;
            this.weights = weights;
            this.aggregateType = aggregateType;
        }

        @Override
        public String toString() {
            return "ZUnionStoreCommand{" +
                    "destination='" + destination + '\'' +
                    ", numkeys=" + numkeys +
                    ", keys=" + Arrays.toString(keys) +
                    ", weights=" + Arrays.toString(weights) +
                    ", aggregateType=" + aggregateType +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("ZUNIONSTORE");
        }
    }
}
