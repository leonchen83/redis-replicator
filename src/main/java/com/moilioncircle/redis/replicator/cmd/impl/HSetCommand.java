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

import java.util.Map;

import com.moilioncircle.redis.replicator.util.ByteArrayMap;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class HSetCommand extends GenericKeyCommand {

    private static final long serialVersionUID = 1L;

    private Map<byte[], byte[]> fields = new ByteArrayMap();
    private byte[] field;
    private byte[] value;

    public HSetCommand() {
    }

    public HSetCommand(byte[] key, byte[] field, byte[] value) {
        super(key);
        this.field = field;
        this.value = value;
        this.fields.put(field, value);
    }

    public HSetCommand(byte[] key, Map<byte[], byte[]> fields) {
        super(key);
        this.fields = fields;
    }

    /**
     * @deprecated Use {@link #getFields()} instead. will remove this method in 4.0.0
     */
    @Deprecated
    public byte[] getField() {
        return field;
    }

    /**
     * @deprecated Use {@link #setFields(Map)} instead. will remove this method in 4.0.0
     */
    @Deprecated
    public void setField(byte[] field) {
        this.field = field;
    }

    /**
     * @deprecated Use {@link #getFields()} instead. will remove this method in 4.0.0
     */
    @Deprecated
    public byte[] getValue() {
        return value;
    }

    /**
     * @deprecated Use {@link #setFields(Map)} instead. will remove this method in 4.0.0
     */
    @Deprecated
    public void setValue(byte[] value) {
        this.value = value;
    }

    public Map<byte[], byte[]> getFields() {
        return fields;
    }

    public void setFields(Map<byte[], byte[]> fields) {
        this.fields = fields;
    }
}
