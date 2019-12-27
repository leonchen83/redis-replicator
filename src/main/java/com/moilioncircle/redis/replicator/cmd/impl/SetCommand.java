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

import com.moilioncircle.redis.replicator.rdb.datatype.ExpiredType;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class SetCommand extends GenericKeyValueCommand {

    private static final long serialVersionUID = 1L;

    private boolean keepTtl;
    private ExpiredType expiredType;
    private Long expiredValue;
    private ExistType existType;

    public SetCommand() {
    }

    public SetCommand(byte[] key, byte[] value, boolean keepTtl, ExpiredType expiredType, Long expiredValue, ExistType existType) {
        super(key, value);
        this.keepTtl = keepTtl;
        this.expiredType = expiredType;
        this.expiredValue = expiredValue;
        this.existType = existType;
    }

    public boolean getKeepTtl() {
        return keepTtl;
    }

    public void setKeepTtl(boolean keepTtl) {
        this.keepTtl = keepTtl;
    }

    public ExpiredType getExpiredType() {
        return expiredType;
    }

    public void setExpiredType(ExpiredType expiredType) {
        this.expiredType = expiredType;
    }

    public Long getExpiredValue() {
        return expiredValue;
    }

    public void setExpiredValue(Long expiredValue) {
        this.expiredValue = expiredValue;
    }

    public ExistType getExistType() {
        return existType;
    }

    public void setExistType(ExistType existType) {
        this.existType = existType;
    }
}
