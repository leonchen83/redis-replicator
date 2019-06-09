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
 * @since 2.4.7
 */
public class EvalShaCommand extends AbstractCommand {

    private static final long serialVersionUID = 1L;

    private byte[] sha;
    private int numkeys;
    private byte[][] keys;
    private byte[][] args;

    public EvalShaCommand() {
    }

    public EvalShaCommand(byte[] sha, int numkeys, byte[][] keys, byte[][] args) {
        this.sha = sha;
        this.numkeys = numkeys;
        this.keys = keys;
        this.args = args;
    }

    public byte[] getSha() {
        return sha;
    }

    public void setSha(byte[] sha) {
        this.sha = sha;
    }

    public int getNumkeys() {
        return numkeys;
    }

    public void setNumkeys(int numkeys) {
        this.numkeys = numkeys;
    }

    public byte[][] getKeys() {
        return keys;
    }

    public void setKeys(byte[][] keys) {
        this.keys = keys;
    }

    public byte[][] getArgs() {
        return args;
    }

    public void setArgs(byte[][] args) {
        this.args = args;
    }
}
