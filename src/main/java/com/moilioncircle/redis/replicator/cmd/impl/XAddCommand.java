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

import java.util.Map;

import com.moilioncircle.redis.replicator.cmd.CommandSpec;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
@CommandSpec(command = "XADD")
public class XAddCommand extends GenericKeyCommand {

    private static final long serialVersionUID = 1L;
    
    private MaxLen maxLen;
    private MinId minId;
    private Limit limit;
    private boolean nomkstream = false;
    private byte[] id;
    private Map<byte[], byte[]> fields;
    
    public XAddCommand() {
        
    }
    
    public XAddCommand(byte[] key, MaxLen maxLen, byte[] id, Map<byte[], byte[]> fields) {
        this(key, maxLen, false, id, fields);
    }
    
    /**
     * @param key key
     * @param maxLen maxlen
     * @param nomkstream nomkstream since redis 6.2-rc1
     * @param id id or *
     * @param fields fields
     * @since 3.5.0
     */
    public XAddCommand(byte[] key, MaxLen maxLen, boolean nomkstream, byte[] id, Map<byte[], byte[]> fields) {
        this(key, maxLen, null, null, nomkstream, id, fields);
    }
    
    /**
     * @param key key
     * @param maxLen maxlen
     * @param minId minId
     * @param limit limit
     * @param nomkstream nomkstream since redis 6.2-rc2
     * @param id id or *
     * @param fields fields
     * @since 3.5.2
     */
    public XAddCommand(byte[] key, MaxLen maxLen, MinId minId, Limit limit, boolean nomkstream, byte[] id, Map<byte[], byte[]> fields) {
        super(key);
        this.maxLen = maxLen;
        this.minId = minId;
        this.limit = limit;
        this.nomkstream = nomkstream;
        this.id = id;
        this.fields = fields;
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
    
    /**
     * @return nomkstream
     * @since 3.5.0
     */
    public boolean isNomkstream() {
        return nomkstream;
    }
    
    /**
     * @param nomkstream nomkstream since redis 6.2
     * @since 3.5.0
     */
    public void setNomkstream(boolean nomkstream) {
        this.nomkstream = nomkstream;
    }
    
    public byte[] getId() {
        return id;
    }
    
    public void setId(byte[] id) {
        this.id = id;
    }
    
    public Map<byte[], byte[]> getFields() {
        return fields;
    }

    public void setFields(Map<byte[], byte[]> fields) {
        this.fields = fields;
    }
}
