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

import java.io.Serializable;
import java.util.List;

/**
 * Created by leon on 10/7/16.
 */
public class OverFlow implements Serializable {
    private OverFlowType overFlowType;
    private List<Statement> statements;

    public OverFlow() {
    }

    public OverFlow(OverFlowType overFlowType, List<Statement> statements) {
        this.overFlowType = overFlowType;
        this.statements = statements;
    }

    public OverFlowType getOverFlowType() {
        return overFlowType;
    }

    public void setOverFlowType(OverFlowType overFlowType) {
        this.overFlowType = overFlowType;
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
                "overFlowType=" + overFlowType +
                ", statements=" + statements +
                '}';
    }
}