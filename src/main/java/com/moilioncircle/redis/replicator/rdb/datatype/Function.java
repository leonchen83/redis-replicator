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
    
    @Deprecated
    private byte[] name;
    
    @Deprecated
    private byte[] engineName;
    
    @Deprecated
    private byte[] description;
    
    private byte[] code;
    
    /**
     * @deprecated since redis-7.0
     * @return name
     */
    @Deprecated
    public byte[] getName() {
        return name;
    }
    
    /**
     * @deprecated since redis-7.0
     * @param name name
     */
    @Deprecated
    public void setName(byte[] name) {
        this.name = name;
    }
    
    /**
     * @deprecated since redis-7.0
     * @return engine name
     */
    @Deprecated
    public byte[] getEngineName() {
        return engineName;
    }
    
    /**
     * @deprecated since redis-7.0
     * @param engineName engine name
     */
    @Deprecated
    public void setEngineName(byte[] engineName) {
        this.engineName = engineName;
    }
    
    /**
     * @deprecated since redis-7.0
     * @return description
     */
    @Deprecated
    public byte[] getDescription() {
        return description;
    }
    
    /**
     * @deprecated since redis-7.0
     * @param description description
     */
    @Deprecated
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
