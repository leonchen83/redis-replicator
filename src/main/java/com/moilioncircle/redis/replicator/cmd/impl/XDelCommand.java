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

import java.util.Arrays;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class XDelCommand implements Command {

    private static final long serialVersionUID = 1L;

    private String key;

    private String[] ids;

    private byte[] rawKey;

    private byte[][] rawIds;

    public XDelCommand() {

    }

    public XDelCommand(String key, String[] ids) {
        this(key, ids, null, null);
    }

    public XDelCommand(String key, String[] ids, byte[] rawKey, byte[][] rawIds) {
        this.key = key;
        this.ids = ids;
        this.rawKey = rawKey;
        this.rawIds = rawIds;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public byte[] getRawKey() {
        return rawKey;
    }

    public void setRawKey(byte[] rawKey) {
        this.rawKey = rawKey;
    }

    public byte[][] getRawIds() {
        return rawIds;
    }

    public void setRawIds(byte[][] rawIds) {
        this.rawIds = rawIds;
    }

    @Override
    public String toString() {
        return "XDelCommand{" +
                "key='" + key + '\'' +
                ", ids=" + Arrays.toString(ids) +
                '}';
    }
}
