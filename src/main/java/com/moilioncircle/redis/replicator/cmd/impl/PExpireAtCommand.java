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

import com.moilioncircle.redis.replicator.cmd.CommandSpec;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@CommandSpec(command = "PEXPIREAT")
public class PExpireAtCommand extends GenericKeyCommand {

    private static final long serialVersionUID = 1L;

    private long ex;
    
    private ExistType existType;
    
    private CompareType compareType;

    public PExpireAtCommand() {
    }

    public PExpireAtCommand(byte[] key, long ex) {
        super(key);
        this.ex = ex;
    }
    
    public PExpireAtCommand(byte[] key, long ex, ExistType existType, CompareType compareType) {
        this(key, ex);
        this.existType = existType;
        this.compareType = compareType;
    }

    public long getEx() {
        return ex;
    }

    public void setEx(long ex) {
        this.ex = ex;
    }
    
    public ExistType getExistType() {
        return existType;
    }
    
    public void setExistType(ExistType existType) {
        this.existType = existType;
    }
    
    public CompareType getCompareType() {
        return compareType;
    }
    
    public void setCompareType(CompareType compareType) {
        this.compareType = compareType;
    }
}
