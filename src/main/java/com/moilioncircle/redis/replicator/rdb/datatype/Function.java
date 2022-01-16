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

package com.moilioncircle.redis.replicator.rdb.datatype;

import com.moilioncircle.redis.replicator.event.AbstractEvent;

/**
 * @author Leon Chen
 * @Since 3.6.0
 */
public class Function extends AbstractEvent {
    private static final long serialVersionUID = 1L;
    
    private byte[] name;
    private byte[] engineName;
    private byte[] description;
    private byte[] code;
    
    public byte[] getName() {
        return name;
    }
    
    public void setName(byte[] name) {
        this.name = name;
    }
    
    public byte[] getEngineName() {
        return engineName;
    }
    
    public void setEngineName(byte[] engineName) {
        this.engineName = engineName;
    }
    
    public byte[] getDescription() {
        return description;
    }
    
    public void setDescription(byte[] description) {
        this.description = description;
    }
    
    public byte[] getCode() {
        return code;
    }
    
    public void setCode(byte[] code) {
        this.code = code;
    }
}
