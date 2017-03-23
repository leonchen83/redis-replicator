package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class PFCountCommand implements Command {
    private String[] keys;

    public PFCountCommand() {
    }

    public PFCountCommand(String... keys) {
        this.keys = keys;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "PFCountCommand{" +
                "keys=" + Arrays.toString(keys) +
                '}';
    }
}
