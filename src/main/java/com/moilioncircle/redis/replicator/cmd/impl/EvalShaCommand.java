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

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * @author Leon Chen
 * @since 2.5.0
 */
public class EvalShaCommand implements Command {

    private static final long serialVersionUID = 1L;

    private String sha;
    private int numkeys;
    private String[] keys;
    private String[] args;
    private byte[] rawSha;
    private byte[][] rawKeys;
    private byte[][] rawArgs;

    public EvalShaCommand() {
    }

    public EvalShaCommand(String sha, int numkeys, String[] keys, String[] args) {
        this(sha, numkeys, keys, args, null, null, null);
    }

    public EvalShaCommand(String sha, int numkeys, String[] keys, String[] args, byte[] rawSha, byte[][] rawKeys, byte[][] rawArgs) {
        this.sha = sha;
        this.numkeys = numkeys;
        this.keys = keys;
        this.args = args;
        this.rawSha = rawSha;
        this.rawKeys = rawKeys;
        this.rawArgs = rawArgs;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public int getNumkeys() {
        return numkeys;
    }

    public void setNumkeys(int numkeys) {
        this.numkeys = numkeys;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public byte[] getRawSha() {
        return rawSha;
    }

    public void setRawSha(byte[] rawSha) {
        this.rawSha = rawSha;
    }

    public byte[][] getRawKeys() {
        return rawKeys;
    }

    public void setRawKeys(byte[][] rawKeys) {
        this.rawKeys = rawKeys;
    }

    public byte[][] getRawArgs() {
        return rawArgs;
    }

    public void setRawArgs(byte[][] rawArgs) {
        this.rawArgs = rawArgs;
    }

    @Override
    public String toString() {
        return "EvalShaCommand{" +
                "sha='" + sha + '\'' +
                ", numkeys=" + numkeys +
                ", keys=" + Arrays.toString(keys) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
