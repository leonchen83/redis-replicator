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

import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.eq;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.toLong;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.toRune;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class BitFieldParser implements CommandParser<BitFieldCommand> {

    @Override
    public BitFieldCommand parse(Object[] command) {
        int idx = 1;
        String key = toRune(command[idx]);
        byte[] rawKey = toBytes(command[idx]);
        idx++;
        List<Statement> list = new ArrayList<>();
        if (idx < command.length) {
            String token;
            do {
                idx = parseStatement(idx, command, list);
                if (idx >= command.length) break;
                token = toRune(command[idx]);
            }
            while (token != null && (eq(token, "GET") || eq(token, "SET") || eq(token, "INCRBY")));
        }
        List<OverFlow> overflows = null;
        if (idx < command.length) {
            overflows = new ArrayList<>();
            do {
                OverFlow overFlow = new OverFlow();
                idx = parseOverFlow(idx, command, overFlow);
                overflows.add(overFlow);
                if (idx >= command.length) break;
            } while (eq(toRune(command[idx]), "OVERFLOW"));
        }

        return new BitFieldCommand(key, list, overflows, rawKey);
    }

    private int parseOverFlow(int i, Object[] params, OverFlow overFlow) {
        int idx = i;
        accept(toRune(params[idx++]), "OVERFLOW");
        OverFlowType overflow;
        String keyword = toRune(params[idx++]);
        if (eq(keyword, "WRAP")) {
            overflow = OverFlowType.WRAP;
        } else if (eq(keyword, "SAT")) {
            overflow = OverFlowType.SAT;
        } else if (eq(keyword, "FAIL")) {
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
            while (token != null && (eq(token, "GET") || eq(token, "SET") || eq(token, "INCRBY")));
        }
        overFlow.setOverFlowType(overflow);
        overFlow.setStatements(list);
        return idx;
    }

    private int parseStatement(int i, Object[] params, List<Statement> list) {
        int idx = i;
        String keyword = toRune(params[idx++]);
        Statement statement;
        if (eq(keyword, "GET")) {
            GetTypeOffset getTypeOffset = new GetTypeOffset();
            idx = parseGet(idx - 1, params, getTypeOffset);
            statement = getTypeOffset;
        } else if (eq(keyword, "SET")) {
            SetTypeOffsetValue setTypeOffsetValue = new SetTypeOffsetValue();
            idx = parseSet(idx - 1, params, setTypeOffsetValue);
            statement = setTypeOffsetValue;
        } else if (eq(keyword, "INCRBY")) {
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
        String type = toRune(params[idx]);
        byte[] rawType = toBytes(params[idx]);
        idx++;
        String offset = toRune(params[idx]);
        byte[] rawOffset = toBytes(params[idx]);
        idx++;
        long increment = toLong(params[idx++]);
        incrByTypeOffsetIncrement.setType(type);
        incrByTypeOffsetIncrement.setOffset(offset);
        incrByTypeOffsetIncrement.setIncrement(increment);
        incrByTypeOffsetIncrement.setRawType(rawType);
        incrByTypeOffsetIncrement.setRawOffset(rawOffset);
        return idx;
    }

    private int parseSet(int i, Object[] params, SetTypeOffsetValue setTypeOffsetValue) {
        int idx = i;
        accept(toRune(params[idx++]), "SET");
        String type = toRune(params[idx]);
        byte[] rawType = toBytes(params[idx]);
        idx++;
        String offset = toRune(params[idx]);
        byte[] rawOffset = toBytes(params[idx]);
        idx++;
        long value = toLong(params[idx++]);
        setTypeOffsetValue.setType(type);
        setTypeOffsetValue.setOffset(offset);
        setTypeOffsetValue.setValue(value);
        setTypeOffsetValue.setRawType(rawType);
        setTypeOffsetValue.setRawOffset(rawOffset);
        return idx;
    }

    private int parseGet(int i, Object[] params, GetTypeOffset getTypeOffset) {
        int idx = i;
        accept(toRune(params[idx++]), "GET");
        String type = toRune(params[idx]);
        byte[] rawType = toBytes(params[idx]);
        idx++;
        String offset = toRune(params[idx]);
        byte[] rawOffset = toBytes(params[idx]);
        idx++;
        getTypeOffset.setType(type);
        getTypeOffset.setOffset(offset);
        getTypeOffset.setRawType(rawType);
        getTypeOffset.setRawOffset(rawOffset);
        return idx;
    }

    private void accept(String actual, String expect) {
        if (eq(actual, expect)) return;
        throw new AssertionError("expect " + expect + " but actual " + actual);
    }

}
