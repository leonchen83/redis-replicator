package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class BitOpCommand implements Command {
    private Op op;
    private String destkey;
    private String keys[];

    public BitOpCommand() {
    }

    public BitOpCommand(Op op, String destkey, String[] keys) {
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

    public String getDestkey() {
        return destkey;
    }

    public void setDestkey(String destkey) {
        this.destkey = destkey;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "BitOpCommand{" +
                "op=" + op +
                ", destkey='" + destkey + '\'' +
                ", keys=" + Arrays.toString(keys) +
                '}';
    }
}
