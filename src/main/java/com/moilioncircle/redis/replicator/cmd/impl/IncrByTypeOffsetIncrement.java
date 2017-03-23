/*
 * Copyright 2016 leon chen
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

/**
 * Created by leon on 10/7/16.
 */
public class IncrByTypeOffsetIncrement implements Statement {
    private String type;
    private String offset;
    private int increment;

    public IncrByTypeOffsetIncrement() {
    }

    public IncrByTypeOffsetIncrement(String type, String offset, int increment) {
        this.type = type;
        this.offset = offset;
        this.increment = increment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    @Override
    public String toString() {
        return "IncrByTypeOffsetIncrement{" +
                "type='" + type + '\'' +
                ", offset='" + offset + '\'' +
                ", increment=" + increment +
                '}';
    }
}