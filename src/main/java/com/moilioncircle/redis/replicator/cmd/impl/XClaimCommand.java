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
public class XClaimCommand extends GenericKeyCommand {

    private static final long serialVersionUID = 1L;

    private byte[] group;
    private byte[] consumer;
    private long minIdle;
    private byte[][] ids;
    private Long idle;
    private Long time;
    private Long retryCount;
    private boolean force;
    private boolean justId;
    private byte[] lastId;

    public XClaimCommand() {
    }
    
    public XClaimCommand(byte[] key, byte[] group, byte[] consumer, long minIdle, byte[][] ids, Long idle, Long time, Long retryCount, boolean force, boolean justId, byte[] lastId) {
        super(key);
        this.group = group;
        this.consumer = consumer;
        this.minIdle = minIdle;
        this.ids = ids;
        this.idle = idle;
        this.time = time;
        this.retryCount = retryCount;
        this.force = force;
        this.justId = justId;
        this.lastId = lastId;
    }

    public byte[] getGroup() {
        return group;
    }

    public void setGroup(byte[] group) {
        this.group = group;
    }

    public byte[] getConsumer() {
        return consumer;
    }

    public void setConsumer(byte[] consumer) {
        this.consumer = consumer;
    }

    public long getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(long minIdle) {
        this.minIdle = minIdle;
    }

    public byte[][] getIds() {
        return ids;
    }

    public void setIds(byte[][] ids) {
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
    
    public byte[] getLastId() {
        return lastId;
    }
    
    public void setLastId(byte[] lastId) {
        this.lastId = lastId;
    }
}
