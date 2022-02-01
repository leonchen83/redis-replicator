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
 * @since 3.6.0
 */
@CommandSpec(command = "FUNCTION", subCommand = "RESTORE")
public class FunctionRestoreCommand extends FunctionCommand {

    private static final long serialVersionUID = 1L;
    
    private byte[] serializedValue;
    private boolean replace;
    private boolean flush;
    private boolean append;
    
    public FunctionRestoreCommand() {
    }
    
    public FunctionRestoreCommand(byte[] serializedValue, boolean replace, boolean flush, boolean append) {
        this.serializedValue = serializedValue;
        this.replace = replace;
        this.flush = flush;
        this.append = append;
    }
    
    public byte[] getSerializedValue() {
        return serializedValue;
    }
    
    public void setSerializedValue(byte[] serializedValue) {
        this.serializedValue = serializedValue;
    }
    
    public boolean isReplace() {
        return replace;
    }
    
    public void setReplace(boolean replace) {
        this.replace = replace;
    }
    
    public boolean isFlush() {
        return flush;
    }
    
    public void setFlush(boolean flush) {
        this.flush = flush;
    }
    
    public boolean isAppend() {
        return append;
    }
    
    public void setAppend(boolean append) {
        this.append = append;
    }
}
