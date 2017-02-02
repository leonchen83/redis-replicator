package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class BitOpCommand implements Command {
    private final Op op;
    private final String destkey;
    private final String keys[];

    public Op getOp() {
        return op;
    }

    public String getDestkey() {
        return destkey;
    }

    public String[] getKeys() {
        return keys;
    }

    public BitOpCommand(Op op, String destkey, String[] keys) {
        this.op = op;
        this.destkey = destkey;
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
