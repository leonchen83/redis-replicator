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
@CommandSpec(command = "LMOVE")
public class LMoveCommand extends AbstractCommand {

    private static final long serialVersionUID = 1L;
    
    private byte[] source;
    private byte[] destination;
    private DirectionType from;
    private DirectionType to;
    
    public LMoveCommand() {
    }
    
    public LMoveCommand(byte[] source, byte[] destination, DirectionType from, DirectionType to) {
        this.source = source;
        this.destination = destination;
        this.from = from;
        this.to = to;
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
    
    public DirectionType getFrom() {
        return from;
    }
    
    public void setFrom(DirectionType from) {
        this.from = from;
    }
    
    public DirectionType getTo() {
        return to;
    }
    
    public void setTo(DirectionType to) {
        this.to = to;
    }
}
