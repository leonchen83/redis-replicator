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

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class XGroupCreateCommand extends XGroupCommand {

    private static final long serialVersionUID = 1L;

    private String key;

    private String group;

    private String id;
    
    private boolean mkStream;

    private byte[] rawKey;

    private byte[] rawGroup;

    private byte[] rawId;

    public XGroupCreateCommand() {

    }
    
    public XGroupCreateCommand(String key, String group, String id, boolean mkStream) {
        this(key, group, id, mkStream, null, null, null);
    }
    
    public XGroupCreateCommand(String key, String group, String id, boolean mkStream, byte[] rawKey, byte[] rawGroup, byte[] rawId) {
        this.key = key;
        this.group = group;
        this.id = id;
        this.mkStream = mkStream;
        this.rawKey = rawKey;
        this.rawGroup = rawGroup;
        this.rawId = rawId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public boolean isMkStream() {
        return mkStream;
    }
    
    public void setMkStream(boolean mkStream) {
        this.mkStream = mkStream;
    }
    
    public byte[] getRawKey() {
        return rawKey;
    }

    public void setRawKey(byte[] rawKey) {
        this.rawKey = rawKey;
    }

    public byte[] getRawGroup() {
        return rawGroup;
    }

    public void setRawGroup(byte[] rawGroup) {
        this.rawGroup = rawGroup;
    }

    public byte[] getRawId() {
        return rawId;
    }

    public void setRawId(byte[] rawId) {
        this.rawId = rawId;
    }

    @Override
    public String toString() {
        return "XGroupCreateCommand{" +
                "key='" + key + '\'' +
                ", group='" + group + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
