/*
 * Copyright 2016-2017 Leon Chen
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
 * @since 3.9.0
 */
@CommandSpec(command = "HSETEX")
public class HSetExCommand extends GenericKeyCommand {
    
    private static final long serialVersionUID = 1L;
    
    private boolean keepTtl;
    private ExpiredType expiredType;
    private Long expiredValue;
    private XATType xatType;
    private Long xatValue;
    private FieldExistType existType;
    
    private byte[][] fields;
    private byte[][] values;
    
    public HSetExCommand() {
    }
    
    public HSetExCommand(byte[] key, byte[][] fields, byte[][] values, boolean keepTtl, ExpiredType expiredType, Long expiredValue, XATType xatType, Long xatValue, FieldExistType existType) {
        super(key);
        this.fields = fields;
        this.values = values;
        this.keepTtl = keepTtl;
        this.expiredType = expiredType;
        this.expiredValue = expiredValue;
        this.xatType = xatType;
        this.xatValue = xatValue;
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
    
    public FieldExistType getExistType() {
        return existType;
    }
    
    public void setExistType(FieldExistType existType) {
        this.existType = existType;
    }
    
    public XATType getXatType() {
        return xatType;
    }
    
    public void setXatType(XATType xatType) {
        this.xatType = xatType;
    }
    
    public Long getXatValue() {
        return xatValue;
    }
    
    public void setXatValue(Long xatValue) {
        this.xatValue = xatValue;
    }
    
    public byte[][] getFields() {
        return fields;
    }
    
    public void setFields(byte[][] fields) {
        this.fields = fields;
    }
    
    public byte[][] getValues() {
        return values;
    }
    
    public void setValues(byte[][] values) {
        this.values = values;
    }
}
