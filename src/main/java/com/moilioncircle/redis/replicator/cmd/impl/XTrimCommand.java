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
 * @since 2.6.0
 */
@CommandSpec(command = "XTRIM")
public class XTrimCommand extends GenericKeyCommand {

    private static final long serialVersionUID = 1L;

    private MaxLen maxLen;
    private MinId minId;
    private Limit limit;

    public XTrimCommand() {
    }

    public XTrimCommand(byte[] key, MaxLen maxLen) {
        this(key, maxLen, null, null);
    }
    
    /**
     * @since 3.5.2
     * @param key key
     * @param maxLen maxLen
     * @param minId minId
     * @param limit limit
     */
    public XTrimCommand(byte[] key, MaxLen maxLen, MinId minId, Limit limit) {
        super(key);
        this.maxLen = maxLen;
        this.minId = minId;
        this.limit = limit;
    }

    public MaxLen getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(MaxLen maxLen) {
        this.maxLen = maxLen;
    }
    
    /**
     * @return min id
     * @since 3.5.2
     */
    public MinId getMinId() {
        return minId;
    }
    
    /**
     * @param minId minId
     * @since 3.5.2
     */
    public void setMinId(MinId minId) {
        this.minId = minId;
    }
    
    /**
     * @return limit
     * @since 3.5.2
     */
    public Limit getLimit() {
        return limit;
    }
    
    /**
     * @param limit limit
     * @since 3.5.2
     */
    public void setLimit(Limit limit) {
        this.limit = limit;
    }
}
