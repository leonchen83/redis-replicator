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

import java.util.Map;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class XAddCommand implements Command {

    private static final long serialVersionUID = 1L;

    private String key;

    private MaxLen maxLen;

    private String id;

    private Map<String, String> fields;

    private byte[] rawKey;

    private byte[] rawId;

    private Map<byte[], byte[]> rawFields;

    public XAddCommand() {

    }

    public XAddCommand(String key, MaxLen maxLen, String id, Map<String, String> fields) {
        this(key, maxLen, id, fields, null, null, null);
    }

    public XAddCommand(String key, MaxLen maxLen, String id, Map<String, String> fields, byte[] rawKey, byte[] rawId, Map<byte[], byte[]> rawFields) {
        this.key = key;
        this.maxLen = maxLen;
        this.id = id;
        this.fields = fields;
        this.rawKey = rawKey;
        this.rawId = rawId;
        this.rawFields = rawFields;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MaxLen getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(MaxLen maxLen) {
        this.maxLen = maxLen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public byte[] getRawKey() {
        return rawKey;
    }

    public void setRawKey(byte[] rawKey) {
        this.rawKey = rawKey;
    }

    public byte[] getRawId() {
        return rawId;
    }

    public void setRawId(byte[] rawId) {
        this.rawId = rawId;
    }

    public Map<byte[], byte[]> getRawFields() {
        return rawFields;
    }

    public void setRawFields(Map<byte[], byte[]> rawFields) {
        this.rawFields = rawFields;
    }

    @Override
    public String toString() {
        return "XAddCommand{" +
                "key='" + key + '\'' +
                ", maxLen=" + maxLen +
                ", id='" + id + '\'' +
                ", fields=" + fields +
                '}';
    }
}
