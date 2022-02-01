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
import com.moilioncircle.redis.replicator.rdb.datatype.ExpiredType;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@CommandSpec(command = "SET")
public class SetCommand extends GenericKeyValueCommand {

    private static final long serialVersionUID = 1L;
    
    private boolean keepTtl;
    private ExpiredType expiredType;
    private Long expiredValue;
    private XATType xatType;
    private Long xatValue;
    private ExistType existType;
    private boolean get = false;
    
    public SetCommand() {
    }
    
    public SetCommand(byte[] key, byte[] value, boolean keepTtl, ExpiredType expiredType, Long expiredValue, ExistType existType) {
        this(key, value, keepTtl, expiredType, expiredValue, existType, false);
    }
    
    /**
     * @param key key
     * @param value value
     * @param keepTtl keepttl since redis 6.0
     * @param expiredType expiredType
     * @param expiredValue expiredValue
     * @param existType existType
     * @param get get since redis 6.2
     * @since 3.5.0
     */
    public SetCommand(byte[] key, byte[] value, boolean keepTtl, ExpiredType expiredType, Long expiredValue, ExistType existType, boolean get) {
        this(key, value, keepTtl, expiredType, expiredValue, XATType.NONE, null, existType, get);
    }
    
    /**
     * @param key key
     * @param value value
     * @param keepTtl keepttl since redis 6.0
     * @param expiredType expiredType
     * @param expiredValue expiredValue
     * @param xatType xatType
     * @param xatValue xatValue
     * @param existType existType
     * @param get get since redis 6.2
     * @since 3.5.2
     */
    public SetCommand(byte[] key, byte[] value, boolean keepTtl, ExpiredType expiredType, Long expiredValue, XATType xatType, Long xatValue, ExistType existType, boolean get) {
        super(key, value);
        this.keepTtl = keepTtl;
        this.expiredType = expiredType;
        this.expiredValue = expiredValue;
        this.xatType = xatType;
        this.xatValue = xatValue;
        this.existType = existType;
        this.get = get;
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
    
    /**
     * @return get is set
     * @since 3.5.0
     */
    public boolean isGet() {
        return get;
    }
    
    /**
     * @param get set get parameter
     * @since 3.5.0
     */
    public void setGet(boolean get) {
        this.get = get;
    }
    
    /**
     * @return xatType
     * @since 3.5.2
     */
    public XATType getXatType() {
        return xatType;
    }
    
    /**
     * @since 3.5.2
     * @param xatType xatType
     */
    public void setXatType(XATType xatType) {
        this.xatType = xatType;
    }
    
    /**
     * @return xatValue
     * @since 3.5.2
     */
    public Long getXatValue() {
        return xatValue;
    }
    
    /**
     * @since 3.5.2
     * @param xatValue xatValue
     */
    public void setXatValue(Long xatValue) {
        this.xatValue = xatValue;
    }
}
