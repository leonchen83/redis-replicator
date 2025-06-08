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
 * @since 3.9.0
 */
@CommandSpec(command = "HPERSIST")
public class HPersistCommand extends GenericKeyCommand {
    private static final long serialVersionUID = 1L;
    
    private byte[][] fields;
    
    public HPersistCommand() {
    }
    
    public HPersistCommand(byte[] key, byte[][] fields) {
        super(key);
        this.fields = fields;
    }
    
    public byte[][] getFields() {
        return fields;
    }
    
    public void setFields(byte[][] fields) {
        this.fields = fields;
    }
}
