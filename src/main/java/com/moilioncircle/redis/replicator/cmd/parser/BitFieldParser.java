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

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.BitFieldCommand;
import com.moilioncircle.redis.replicator.cmd.impl.GetTypeOffset;
import com.moilioncircle.redis.replicator.cmd.impl.IncrByTypeOffsetIncrement;
import com.moilioncircle.redis.replicator.cmd.impl.OverFlow;
import com.moilioncircle.redis.replicator.cmd.impl.OverFlowType;
import com.moilioncircle.redis.replicator.cmd.impl.SetTypeOffsetValue;
import com.moilioncircle.redis.replicator.cmd.impl.Statement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToBytes;
import static com.moilioncircle.redis.replicator.cmd.parser.CommandParsers.objToString;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class BitFieldParser implements CommandParser<BitFieldCommand> {

    @Override
    public BitFieldCommand parse(Object[] command) {
        int idx = 1;
        String key = objToString(command[idx]);
        byte[] rawKey = objToBytes(command[idx]);
        idx++;
        List<Statement> list = new ArrayList<>();
        if (idx < command.length) {
            String token;
            do {
                idx = parseStatement(idx, command, list);
                if (idx >= command.length) break;
                token = objToString(command[idx]);
            }
            while (token != null && (token.equalsIgnoreCase("GET") || token.equalsIgnoreCase("SET") || token.equalsIgnoreCase("INCRBY")));
        }
        List<OverFlow> overFlowList = null;
        if (idx < command.length) {
            overFlowList = new ArrayList<>();
            do {
                OverFlow overFlow = new OverFlow();
                idx = parseOverFlow(idx, command, overFlow);
                overFlowList.add(overFlow);
                if (idx >= command.length) break;
            } while ("OVERFLOW".equalsIgnoreCase(objToString(command[idx])));
        }

        return new BitFieldCommand(key, list, overFlowList, rawKey);
    }

    private int parseOverFlow(int i, Object[] params, OverFlow overFlow) {
        int idx = i;
        accept(objToString(params[idx++]), "OVERFLOW");
        OverFlowType overFlowType;
        String keyWord = objToString(params[idx++]);
        if ("WRAP".equalsIgnoreCase(keyWord)) {
            overFlowType = OverFlowType.WRAP;
        } else if ("SAT".equalsIgnoreCase(keyWord)) {
            overFlowType = OverFlowType.SAT;
        } else if ("FAIL".equalsIgnoreCase(keyWord)) {
            overFlowType = OverFlowType.FAIL;
        } else {
            throw new AssertionError("parse [BITFIELD] command error." + keyWord);
        }
        List<Statement> list = new ArrayList<>();
        if (idx < params.length) {
            String token;
            do {
                idx = parseStatement(idx, params, list);
                if (idx >= params.length) break;
                token = objToString(params[idx]);
            }
            while (token != null && (token.equalsIgnoreCase("GET") || token.equalsIgnoreCase("SET") || token.equalsIgnoreCase("INCRBY")));
        }
        overFlow.setOverFlowType(overFlowType);
        overFlow.setStatements(list);
        return idx;
    }

    private int parseStatement(int i, Object[] params, List<Statement> list) {
        int idx = i;
        String keyWord = objToString(params[idx++]);
        Statement statement;
        if ("GET".equalsIgnoreCase(keyWord)) {
            GetTypeOffset getTypeOffset = new GetTypeOffset();
            idx = parseGet(idx - 1, params, getTypeOffset);
            statement = getTypeOffset;
        } else if ("SET".equalsIgnoreCase(keyWord)) {
            SetTypeOffsetValue setTypeOffsetValue = new SetTypeOffsetValue();
            idx = parseSet(idx - 1, params, setTypeOffsetValue);
            statement = setTypeOffsetValue;
        } else if ("INCRBY".equalsIgnoreCase(keyWord)) {
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
        accept(objToString(params[idx++]), "INCRBY");
        String type = objToString(params[idx]);
        byte[] rawType = objToBytes(params[idx]);
        idx++;
        String offset = objToString(params[idx]);
        byte[] rawOffset = objToBytes(params[idx]);
        idx++;
        long increment = new BigDecimal(objToString(params[idx++])).longValueExact();
        incrByTypeOffsetIncrement.setType(type);
        incrByTypeOffsetIncrement.setOffset(offset);
        incrByTypeOffsetIncrement.setIncrement(increment);
        incrByTypeOffsetIncrement.setRawType(rawType);
        incrByTypeOffsetIncrement.setRawOffset(rawOffset);
        return idx;
    }

    private int parseSet(int i, Object[] params, SetTypeOffsetValue setTypeOffsetValue) {
        int idx = i;
        accept(objToString(params[idx++]), "SET");
        String type = objToString(params[idx]);
        byte[] rawType = objToBytes(params[idx]);
        idx++;
        String offset = objToString(params[idx]);
        byte[] rawOffset = objToBytes(params[idx]);
        idx++;
        long value = new BigDecimal(objToString(params[idx++])).longValueExact();
        setTypeOffsetValue.setType(type);
        setTypeOffsetValue.setOffset(offset);
        setTypeOffsetValue.setValue(value);
        setTypeOffsetValue.setRawType(rawType);
        setTypeOffsetValue.setRawOffset(rawOffset);
        return idx;
    }

    private int parseGet(int i, Object[] params, GetTypeOffset getTypeOffset) {
        int idx = i;
        accept(objToString(params[idx++]), "GET");
        String type = objToString(params[idx]);
        byte[] rawType = objToBytes(params[idx]);
        idx++;
        String offset = objToString(params[idx]);
        byte[] rawOffset = objToBytes(params[idx]);
        idx++;
        getTypeOffset.setType(type);
        getTypeOffset.setOffset(offset);
        getTypeOffset.setRawType(rawType);
        getTypeOffset.setRawOffset(rawOffset);
        return idx;
    }

    private void accept(String actual, String expect) {
        if (actual.equalsIgnoreCase(expect)) return;
        throw new AssertionError("expect " + expect + " but actual " + actual);
    }

}
