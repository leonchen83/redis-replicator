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

package com.moilioncircle.redis.replicator.cmd.impl;

import java.util.List;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class BitFieldCommand extends GenericKeyCommand {

    private static final long serialVersionUID = 1L;

    private List<Statement> statements;
    private List<OverFlow> overFlows;

    public BitFieldCommand() {
    }

    public BitFieldCommand(byte[] key, List<Statement> statements, List<OverFlow> overFlows) {
        super(key);
        this.statements = statements;
        this.overFlows = overFlows;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public List<OverFlow> getOverFlows() {
        return overFlows;
    }

    public void setOverFlows(List<OverFlow> overFlows) {
        this.overFlows = overFlows;
    }
}
