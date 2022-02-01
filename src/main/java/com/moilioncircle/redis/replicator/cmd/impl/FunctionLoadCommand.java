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
@CommandSpec(command = "FUNCTION", subCommand = "LOAD")
public class FunctionLoadCommand extends FunctionCommand {

    private static final long serialVersionUID = 1L;

    private byte[] engineName;
    private byte[] libraryName;
    private boolean replace;
    private byte[] description;
    private byte[] functionCode;
    
    public FunctionLoadCommand() {
    }
    
    public FunctionLoadCommand(byte[] engineName, byte[] libraryName, boolean replace, byte[] description, byte[] functionCode) {
        this.engineName = engineName;
        this.libraryName = libraryName;
        this.replace = replace;
        this.description = description;
        this.functionCode = functionCode;
    }
    
    public byte[] getEngineName() {
        return engineName;
    }
    
    public void setEngineName(byte[] engineName) {
        this.engineName = engineName;
    }
    
    public byte[] getLibraryName() {
        return libraryName;
    }
    
    public void setLibraryName(byte[] libraryName) {
        this.libraryName = libraryName;
    }
    
    public boolean isReplace() {
        return replace;
    }
    
    public void setReplace(boolean replace) {
        this.replace = replace;
    }
    
    public byte[] getDescription() {
        return description;
    }
    
    public void setDescription(byte[] description) {
        this.description = description;
    }
    
    public byte[] getFunctionCode() {
        return functionCode;
    }
    
    public void setFunctionCode(byte[] functionCode) {
        this.functionCode = functionCode;
    }
}
