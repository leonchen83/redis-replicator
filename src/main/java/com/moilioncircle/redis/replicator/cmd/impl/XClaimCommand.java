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
public class XClaimCommand implements Command {

    private static final long serialVersionUID = 1L;

    private String key;

    private String group;

    private String consumer;

    private long minIdle;

    private String[] ids;

    private Long idle;

    private Long time;

    private Long retryCount;
    
    private boolean force;

    private boolean justId;

    private byte[] rawKey;

    private byte[] rawGroup;

    private byte[] rawConsumer;

    private byte[][] rawIds;

    public XClaimCommand() {

    }
    
    public XClaimCommand(String key, String group, String consumer, long minIdle, String[] ids, Long idle, Long time, Long retryCount, boolean force, boolean justId) {
        this(key, group, consumer, minIdle, ids, idle, time, retryCount, force, justId, null, null, null, null);
    }
    
    public XClaimCommand(String key, String group, String consumer, long minIdle, String[] ids, Long idle, Long time, Long retryCount, boolean force, boolean justId, byte[] rawKey, byte[] rawGroup, byte[] rawConsumer, byte[][] rawIds) {
        this.key = key;
        this.group = group;
        this.consumer = consumer;
        this.minIdle = minIdle;
        this.ids = ids;
        this.idle = idle;
        this.time = time;
        this.retryCount = retryCount;
        this.force = force;
        this.justId = justId;
        this.rawKey = rawKey;
        this.rawGroup = rawGroup;
        this.rawConsumer = rawConsumer;
        this.rawIds = rawIds;
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

    public long getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(long minIdle) {
        this.minIdle = minIdle;
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public Long getIdle() {
        return idle;
    }

    public void setIdle(Long idle) {
        this.idle = idle;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Long retryCount) {
        this.retryCount = retryCount;
    }
    
    public boolean isForce() {
        return force;
    }
    
    public void setForce(boolean force) {
        this.force = force;
    }
    
    public boolean isJustId() {
        return justId;
    }

    public void setJustId(boolean justId) {
        this.justId = justId;
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

    public byte[][] getRawIds() {
        return rawIds;
    }

    public void setRawIds(byte[][] rawIds) {
        this.rawIds = rawIds;
    }

    @Override
    public String toString() {
        return "XClaimCommand{" +
                "key='" + key + '\'' +
                ", group='" + group + '\'' +
                ", consumer='" + consumer + '\'' +
                ", minIdle=" + minIdle +
                ", ids=" + Arrays.toString(ids) +
                ", idle=" + idle +
                ", time=" + time +
                ", retryCount=" + retryCount +
                ", force=" + force +
                ", justId=" + justId +
                '}';
    }
}
