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
public class XGroupDelConsumerCommand extends XGroupCommand {

    private static final long serialVersionUID = 1L;

    private String key;

    private String group;

    private String consumer;

    private byte[] rawKey;

    private byte[] rawGroup;

    private byte[] rawConsumer;

    public XGroupDelConsumerCommand() {

    }

    public XGroupDelConsumerCommand(String key, String group, String consumer) {
        this(key, group, consumer, null, null, null);
    }

    public XGroupDelConsumerCommand(String key, String group, String consumer, byte[] rawKey, byte[] rawGroup, byte[] rawConsumer) {
        this.key = key;
        this.group = group;
        this.consumer = consumer;
        this.rawKey = rawKey;
        this.rawGroup = rawGroup;
        this.rawConsumer = rawConsumer;
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

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
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

    public byte[] getRawConsumer() {
        return rawConsumer;
    }

    public void setRawConsumer(byte[] rawConsumer) {
        this.rawConsumer = rawConsumer;
    }

    @Override
    public String toString() {
        return "XGroupDelConsumerCommand{" +
                "key='" + key + '\'' +
                ", group='" + group + '\'' +
                ", consumer='" + consumer + '\'' +
                '}';
    }
}
