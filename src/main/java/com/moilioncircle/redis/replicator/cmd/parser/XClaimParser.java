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
import com.moilioncircle.redis.replicator.cmd.impl.XClaimCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toLong;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class XClaimParser implements CommandParser<XClaimCommand> {
    @Override
    public XClaimCommand parse(Object[] command) {
        int idx = 1;
        byte[] key = toBytes(command[idx]);
        idx++;
        byte[] group = toBytes(command[idx]);
        idx++;
        byte[] consumer = toBytes(command[idx]);
        idx++;
        long minIdle = toLong(command[idx++]);
        List<byte[]> ids = new ArrayList<>();
        for (; idx < command.length; idx++) {
            byte[] id = toBytes(command[idx]);
            if (!validId(id)) break;
            ids.add(id);
        }
        Long idle = null;
        Long time = null;
        Long retryCount = null;
        boolean force = false;
        boolean justId = false;
        byte[] lastId = null;
        while (idx < command.length) {
            String next = toRune(command[idx]);
            if (isEquals(next, "IDLE")) {
                idx++;
                idle = toLong(command[idx]);
                idx++;
            } else if (isEquals(next, "TIME")) {
                idx++;
                time = toLong(command[idx]);
                idx++;
            } else if (isEquals(next, "RETRYCOUNT")) {
                idx++;
                retryCount = toLong(command[idx]);
                idx++;
            } else if (isEquals(next, "FORCE")) {
                idx++;
                force = true;
            } else if (isEquals(next, "JUSTID")) {
                idx++;
                justId = true;
            } else if (isEquals(next, "LASTID")) {
                idx++;
                lastId = toBytes(command[idx]);
                idx++;
            } else {
                throw new UnsupportedOperationException(next);
            }
        }
        return new XClaimCommand(key, group, consumer, minIdle, ids.toArray(new byte[0][]), idle, time, retryCount, force, justId, lastId);
    }

    private boolean validId(byte[] bid) {
        if (bid == null) return false;
        String id = toRune(bid);
        if (Objects.equals(id, "+") || Objects.equals(id, "-")) return true;
        int idx = id.indexOf('-');
        try {
            Long.parseLong(id.substring(0, idx)); // ms
            Long.parseLong(id.substring(idx + 1, id.length())); // seq
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
