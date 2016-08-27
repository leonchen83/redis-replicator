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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leon on 8/27/16.
 */
public class BitFieldParser implements CommandParser<BitFieldParser.BitFieldCommand> {

    @Override
    public BitFieldCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String key = (String) params[idx++];
        List<Statement> list = new ArrayList<>();
        if (idx < params.length) {
            String token;
            do {
                idx = parseStatement(idx, params, list);
                if (idx >= params.length) break;
                token = (String) params[idx];
            }
            while (token != null && (token.equalsIgnoreCase("GET") || token.equalsIgnoreCase("SET") || token.equalsIgnoreCase("INCRBY")));
        }
        List<OverFlow> overFlowList = null;
        if (idx < params.length) {
            overFlowList = new ArrayList<>();
            do {
                OverFlow overFlow = new OverFlow();
                idx = parseOverFlow(idx, params, overFlow);
                overFlowList.add(overFlow);
                if (idx >= params.length) break;
            } while (((String) params[idx]).equalsIgnoreCase("OVERFLOW"));
        }

        return new BitFieldCommand(key, list, overFlowList);
    }

    private int parseOverFlow(int i, Object[] params, OverFlow overFlow) {
        int idx = i;
        accept((String) params[idx++], "OVERFLOW");
        Boolean overFlowWrap = null;
        Boolean overFlowSat = null;
        Boolean overFlowFail = null;
        String keyWord = (String) params[idx++];
        if (keyWord.equalsIgnoreCase("WRAP")) {
            overFlowWrap = true;
        } else if (keyWord.equalsIgnoreCase("SAT")) {
            overFlowSat = true;
        } else if (keyWord.equalsIgnoreCase("FAIL")) {
            overFlowFail = true;
        } else {
            throw new AssertionError("parse [BITFIELD] command error." + keyWord);
        }
        List<Statement> list = new ArrayList<>();
        if (idx < params.length) {
            String token;
            do {
                idx = parseStatement(idx, params, list);
                if (idx >= params.length) break;
                token = (String) params[idx];
            }
            while (token != null && (token.equalsIgnoreCase("GET") || token.equalsIgnoreCase("SET") || token.equalsIgnoreCase("INCRBY")));
        }
        overFlow.setOverFlowFail(overFlowFail);
        overFlow.setOverFlowSat(overFlowSat);
        overFlow.setOverFlowWrap(overFlowWrap);
        overFlow.setStatements(list);
        return idx;
    }

    private int parseStatement(int i, Object[] params, List<Statement> list) {
        int idx = i;
        String keyWord = (String) params[idx++];
        Statement statement = null;
        if (keyWord.equalsIgnoreCase("GET")) {
            GetTypeOffset getTypeOffset = new GetTypeOffset();
            idx = parseGet(idx - 1, params, getTypeOffset);
            statement = getTypeOffset;
        } else if (keyWord.equalsIgnoreCase("SET")) {
            SetTypeOffsetValue setTypeOffsetValue = new SetTypeOffsetValue();
            idx = parseSet(idx - 1, params, setTypeOffsetValue);
            statement = setTypeOffsetValue;
        } else if (keyWord.equalsIgnoreCase("INCRBY")) {
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
        accept((String) params[idx++], "INCRBY");
        String type = (String) params[idx++];
        String offset = (String) params[idx++];
        int increment = Integer.parseInt((String) params[idx++]);
        incrByTypeOffsetIncrement.setType(type);
        incrByTypeOffsetIncrement.setOffset(offset);
        incrByTypeOffsetIncrement.setIncrement(increment);
        return idx;
    }

    private int parseSet(int i, Object[] params, SetTypeOffsetValue setTypeOffsetValue) {
        int idx = i;
        accept((String) params[idx++], "SET");
        String type = (String) params[idx++];
        String offset = (String) params[idx++];
        int value = Integer.parseInt((String) params[idx++]);
        setTypeOffsetValue.setType(type);
        setTypeOffsetValue.setOffset(offset);
        setTypeOffsetValue.setValue(value);
        return idx;
    }

    private int parseGet(int i, Object[] params, GetTypeOffset getTypeOffset) {
        int idx = i;
        accept((String) params[idx++], "GET");
        String type = (String) params[idx++];
        String offset = (String) params[idx++];
        getTypeOffset.setType(type);
        getTypeOffset.setOffset(offset);
        return idx;
    }

    private void accept(String actual, String expect) {
        if (actual.equalsIgnoreCase(expect)) return;
        throw new AssertionError("Expect " + expect + " but actual " + actual);
    }

    public static class BitFieldCommand implements Command {
        public final String key;
        public final List<Statement> statements;
        public final List<OverFlow> overFlows;

        public BitFieldCommand(String key,
                               List<Statement> statements,
                               List<OverFlow> overFlows) {
            this.key = key;
            this.statements = statements;
            this.overFlows = overFlows;
        }

        @Override
        public String toString() {
            return "BitFieldCommand{" +
                    "key='" + key + '\'' +
                    ", statements=" + statements +
                    ", overFlows=" + overFlows +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("BITFIELD");
        }
    }

    public interface Statement {

    }

    public static class GetTypeOffset implements Statement {
        public String type;
        public String offset;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOffset() {
            return offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }

        @Override
        public String toString() {
            return "GetTypeOffset{" +
                    "type='" + type + '\'' +
                    ", offset=" + offset +
                    '}';
        }
    }

    public static class SetTypeOffsetValue implements Statement {
        public String type;
        public String offset;
        public int value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOffset() {
            return offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "SetTypeOffsetValue{" +
                    "type='" + type + '\'' +
                    ", offset='" + offset + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

    public static class IncrByTypeOffsetIncrement implements Statement {
        public String type;
        public String offset;
        public int increment;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOffset() {
            return offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }

        public int getIncrement() {
            return increment;
        }

        public void setIncrement(int increment) {
            this.increment = increment;
        }

        @Override
        public String toString() {
            return "IncrByTypeOffsetIncrement{" +
                    "type='" + type + '\'' +
                    ", offset='" + offset + '\'' +
                    ", increment=" + increment +
                    '}';
        }
    }

    public static class OverFlow {
        public Boolean overFlowWrap;
        public Boolean overFlowSat;
        public Boolean overFlowFail;
        public List<Statement> statements;

        public Boolean getOverFlowWrap() {
            return overFlowWrap;
        }

        public void setOverFlowWrap(Boolean overFlowWrap) {
            this.overFlowWrap = overFlowWrap;
        }

        public Boolean getOverFlowSat() {
            return overFlowSat;
        }

        public void setOverFlowSat(Boolean overFlowSat) {
            this.overFlowSat = overFlowSat;
        }

        public Boolean getOverFlowFail() {
            return overFlowFail;
        }

        public void setOverFlowFail(Boolean overFlowFail) {
            this.overFlowFail = overFlowFail;
        }

        public List<Statement> getStatements() {
            return statements;
        }

        public void setStatements(List<Statement> statements) {
            this.statements = statements;
        }

        @Override
        public String toString() {
            return "OverFlow{" +
                    "overFlowWrap=" + overFlowWrap +
                    ", overFlowSat=" + overFlowSat +
                    ", overFlowFail=" + overFlowFail +
                    ", statements=" + statements +
                    '}';
        }
    }
}
