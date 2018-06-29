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
import com.moilioncircle.redis.replicator.cmd.impl.BitFieldCommand;
import com.moilioncircle.redis.replicator.cmd.impl.GetTypeOffset;
import com.moilioncircle.redis.replicator.cmd.impl.IncrByTypeOffsetIncrement;
import com.moilioncircle.redis.replicator.cmd.impl.OverFlow;
import com.moilioncircle.redis.replicator.cmd.impl.OverFlowType;
import com.moilioncircle.redis.replicator.cmd.impl.SetTypeOffsetValue;
import com.moilioncircle.redis.replicator.cmd.impl.Statement;

import java.util.ArrayList;
import java.util.List;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toLong;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class BitFieldParser implements CommandParser<BitFieldCommand> {

    @Override
    public BitFieldCommand parse(Object[] command) {
        int idx = 1;
        byte[] key = toBytes(command[idx]);
        idx++;
        List<Statement> list = new ArrayList<>();
        if (idx < command.length) {
            String token;
            do {
                idx = parseStatement(idx, command, list);
                if (idx >= command.length) break;
                token = toRune(command[idx]);
            }
            while (token != null && (isEquals(token, "GET") || isEquals(token, "SET") || isEquals(token, "INCRBY")));
        }
        List<OverFlow> overflows = null;
        if (idx < command.length) {
            overflows = new ArrayList<>();
            do {
                OverFlow overFlow = new OverFlow();
                idx = parseOverFlow(idx, command, overFlow);
                overflows.add(overFlow);
                if (idx >= command.length) break;
            } while (isEquals(toRune(command[idx]), "OVERFLOW"));
        }

        return new BitFieldCommand(key, list, overflows);
    }

    private int parseOverFlow(int i, Object[] params, OverFlow overFlow) {
        int idx = i;
        accept(toRune(params[idx++]), "OVERFLOW");
        OverFlowType overflow;
        String keyword = toRune(params[idx++]);
        if (isEquals(keyword, "WRAP")) {
            overflow = OverFlowType.WRAP;
        } else if (isEquals(keyword, "SAT")) {
            overflow = OverFlowType.SAT;
        } else if (isEquals(keyword, "FAIL")) {
            overflow = OverFlowType.FAIL;
        } else {
            throw new AssertionError("parse [BITFIELD] command error." + keyword);
        }
        List<Statement> list = new ArrayList<>();
        if (idx < params.length) {
            String token;
            do {
                idx = parseStatement(idx, params, list);
                if (idx >= params.length) break;
                token = toRune(params[idx]);
            }
            while (token != null && (isEquals(token, "GET") || isEquals(token, "SET") || isEquals(token, "INCRBY")));
        }
        overFlow.setOverFlowType(overflow);
        overFlow.setStatements(list);
        return idx;
    }

    private int parseStatement(int i, Object[] params, List<Statement> list) {
        int idx = i;
        String keyword = toRune(params[idx++]);
        Statement statement;
        if (isEquals(keyword, "GET")) {
            GetTypeOffset getTypeOffset = new GetTypeOffset();
            idx = parseGet(idx - 1, params, getTypeOffset);
            statement = getTypeOffset;
        } else if (isEquals(keyword, "SET")) {
            SetTypeOffsetValue setTypeOffsetValue = new SetTypeOffsetValue();
            idx = parseSet(idx - 1, params, setTypeOffsetValue);
            statement = setTypeOffsetValue;
        } else if (isEquals(keyword, "INCRBY")) {
            IncrByTypeOffsetIncrement incrByTypeOffsetIncrement = new IncrByTypeOffsetIncrement();
            idx = parseIncrBy(idx - 1, params, incrByTypeOffsetIncrement);
            statement = incrByTypeOffsetIncrement;
        } else {
            return i;
        }
        list.add(statement);
        return idx;
    }

    private int parseIncrBy(int i, Object[] params, IncrByTypeOffsetIncrement incrByTypeOffsetIncrement) {
        int idx = i;
        accept(toRune(params[idx++]), "INCRBY");
        byte[] type = toBytes(params[idx]);
        idx++;
        byte[] offset = toBytes(params[idx]);
        idx++;
        long increment = toLong(params[idx++]);
        incrByTypeOffsetIncrement.setType(type);
        incrByTypeOffsetIncrement.setOffset(offset);
        incrByTypeOffsetIncrement.setIncrement(increment);
        return idx;
    }

    private int parseSet(int i, Object[] params, SetTypeOffsetValue setTypeOffsetValue) {
        int idx = i;
        accept(toRune(params[idx++]), "SET");
        byte[] type = toBytes(params[idx]);
        idx++;
        byte[] offset = toBytes(params[idx]);
        idx++;
        long value = toLong(params[idx++]);
        setTypeOffsetValue.setType(type);
        setTypeOffsetValue.setOffset(offset);
        setTypeOffsetValue.setValue(value);
        return idx;
    }

    private int parseGet(int i, Object[] params, GetTypeOffset getTypeOffset) {
        int idx = i;
        accept(toRune(params[idx++]), "GET");
        byte[] type = toBytes(params[idx]);
        idx++;
        byte[] offset = toBytes(params[idx]);
        idx++;
        getTypeOffset.setType(type);
        getTypeOffset.setOffset(offset);
        return idx;
    }

    private void accept(String actual, String expect) {
        if (isEquals(actual, expect)) return;
        throw new AssertionError("expect " + expect + " but actual " + actual);
    }

}
