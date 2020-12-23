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

import java.io.Serializable;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class FromMember implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private byte[] member;
    
    public FromMember() {
    }
    
    public FromMember(byte[] member) {
        this.member = member;
    }
    
    public byte[] getMember() {
        return member;
    }
    
    public void setMember(byte[] member) {
        this.member = member;
    }
}
