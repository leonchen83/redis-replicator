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

import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ZAddCommand extends GenericKeyCommand {

    private static final long serialVersionUID = 1L;

    private ExistType existType;
    private boolean ch;
    private boolean incr;
    private ZSetEntry[] zSetEntries;

    public ZAddCommand() {
    }
    
    public ZAddCommand(byte[] key, ExistType existType, boolean ch, boolean incr, ZSetEntry[] zSetEntries) {
        super(key);
        this.existType = existType;
        this.ch = ch;
        this.incr = incr;
        this.zSetEntries = zSetEntries;
    }

    public ExistType getExistType() {
        return existType;
    }

    public void setExistType(ExistType existType) {
        this.existType = existType;
    }

    public boolean isCh() {
        return ch;
    }

    public void setCh(boolean ch) {
        this.ch = ch;
    }

    public boolean isIncr() {
        return incr;
    }

    public void setIncr(boolean incr) {
        this.incr = incr;
    }

    public ZSetEntry[] getZSetEntries() {
        return zSetEntries;
    }

    public ZSetEntry[] getzSetEntries() {
        return zSetEntries;
    }

    public void setzSetEntries(ZSetEntry[] zSetEntries) {
        this.zSetEntries = zSetEntries;
    }
}
