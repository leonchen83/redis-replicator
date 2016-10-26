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
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leon on 8/19/16.
 */
public class ZAddParser implements CommandParser<ZAddParser.ZAddCommand> {

    @Override
    public ZAddCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        Boolean isCh = null, isIncr = null;
        ExistType existType = ExistType.NONE;
        List<ZSetEntry> list = new ArrayList<>();
        String key = (String) params[idx++];
        while (idx < params.length) {
            String param = (String) params[idx];
            if (param.equalsIgnoreCase("NX")) {
                existType = ExistType.NX;
            } else if (param.equalsIgnoreCase("XX")) {
                existType = ExistType.XX;
            } else if (param.equalsIgnoreCase("CH")) {
                isCh = true;
            } else if (param.equalsIgnoreCase("INCR")) {
                isIncr = true;
            } else {
                double score = Double.parseDouble(param);
                idx++;
                String member = (String) params[idx];
                list.add(new ZSetEntry(member, score));
            }
            idx++;
        }
        ZSetEntry[] zSetEntries = new ZSetEntry[list.size()];
        list.toArray(zSetEntries);
        return new ZAddCommand(key, existType, isCh, isIncr, zSetEntries);
    }

    public static class ZAddCommand implements Command {
        private final String key;
        private final ExistType existType;
        private final Boolean isCh;
        private final Boolean isIncr;
        private final ZSetEntry[] zSetEntries;

        public String getKey() {
            return key;
        }

        public ExistType getExistType() {
            return existType;
        }

        public Boolean getCh() {
            return isCh;
        }

        public Boolean getIncr() {
            return isIncr;
        }

        public ZSetEntry[] getZSetEntries() {
            return zSetEntries;
        }

        public ZAddCommand(String key, ExistType existType, Boolean isCh, Boolean isIncr, ZSetEntry[] zSetEntries) {
            this.key = key;
            this.existType = existType;
            this.isCh = isCh;
            this.isIncr = isIncr;
            this.zSetEntries = zSetEntries;
        }

        @Override
        public String toString() {
            return "ZAddCommand{" +
                    "key='" + key + '\'' +
                    ", existType=" + existType +
                    ", isCh=" + isCh +
                    ", isIncr=" + isIncr +
                    ", zSetEntries=" + Arrays.toString(zSetEntries) +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("ZADD");
        }
    }
}
