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

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ZIncrByCommand extends GenericKeyCommand {

    private static final long serialVersionUID = 1L;

    private double increment;
    private byte[] member;

    public ZIncrByCommand() {
    }

    public ZIncrByCommand(byte[] key, double increment, byte[] member) {
        super(key);
        this.increment = increment;
        this.member = member;
    }

    public double getIncrement() {
        return increment;
    }

    public void setIncrement(double increment) {
        this.increment = increment;
    }

    public byte[] getMember() {
        return member;
    }

    public void setMember(byte[] member) {
        this.member = member;
    }
}
