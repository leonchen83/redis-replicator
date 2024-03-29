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

/**
 * @author Leon Chen
 * @since 3.5.0
 */
@CommandSpec(command = "COPY")
public class CopyCommand extends AbstractCommand {
    private static final long serialVersionUID = 1L;
    
    private byte[] source;
    private byte[] destination;
    private Integer db;
    private boolean replace;
    
    public CopyCommand() {
    }
    
    public CopyCommand(byte[] source, byte[] destination, Integer db, boolean replace) {
        this.source = source;
        this.destination = destination;
        this.db = db;
        this.replace = replace;
    }
    
    public byte[] getSource() {
        return source;
    }
    
    public void setSource(byte[] source) {
        this.source = source;
    }
    
    public byte[] getDestination() {
        return destination;
    }
    
    public void setDestination(byte[] destination) {
        this.destination = destination;
    }
    
    public Integer getDb() {
        return db;
    }
    
    public void setDb(Integer db) {
        this.db = db;
    }
    
    public boolean isReplace() {
        return replace;
    }
    
    public void setReplace(boolean replace) {
        this.replace = replace;
    }
}
