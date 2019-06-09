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
public class BitOpCommand extends AbstractCommand {

    private static final long serialVersionUID = 1L;

    private Op op;
    private byte[] destkey;
    private byte[][] keys;

    public BitOpCommand() {
    }

    public BitOpCommand(Op op, byte[] destkey, byte[][] keys) {
        this.op = op;
        this.destkey = destkey;
        this.keys = keys;
    }

    public Op getOp() {
        return op;
    }

    public void setOp(Op op) {
        this.op = op;
    }

    public byte[] getDestkey() {
        return destkey;
    }

    public void setDestkey(byte[] destkey) {
        this.destkey = destkey;
    }

    public byte[][] getKeys() {
        return keys;
    }

    public void setKeys(byte[][] keys) {
        this.keys = keys;
    }
}
