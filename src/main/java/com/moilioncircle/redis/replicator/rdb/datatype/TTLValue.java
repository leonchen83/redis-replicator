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

import java.io.Serializable;
import java.util.Arrays;

import com.moilioncircle.redis.replicator.util.Strings;

/**
 * @author Leon Chen
 * @since 3.9.0
 */
public class TTLValue implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long expires;
    private byte[] value;

    public TTLValue() {
    }
    
    public TTLValue(byte[] value) {
        this(null, value);
    }

    public TTLValue(Long expires, byte[] value) {
        this.expires = expires;
        this.value = value;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TTLValue that = (TTLValue) o;
        return Arrays.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
    
    @Override
    public String toString() {
        return "[" + Strings.toString(value) + ", " + expires + "]";
    }
}
