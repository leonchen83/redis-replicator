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

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class LInsertCommand implements Command {

    private static final long serialVersionUID = 1L;

    private byte[] key;
    private LInsertType lInsertType;
    private byte[] pivot;
    private byte[] value;

    public LInsertCommand() {
    }

    public LInsertCommand(byte[] key, LInsertType lInsertType, byte[] pivot, byte[] value) {
        this.key = key;
        this.lInsertType = lInsertType;
        this.pivot = pivot;
        this.value = value;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public LInsertType getlInsertType() {
        return lInsertType;
    }

    public void setlInsertType(LInsertType lInsertType) {
        this.lInsertType = lInsertType;
    }

    public byte[] getPivot() {
        return pivot;
    }

    public void setPivot(byte[] pivot) {
        this.pivot = pivot;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
