package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class DelCommand implements Command {
    private String[] keys;

    public DelCommand() {
    }

    public DelCommand(String... keys) {
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
        return "DelCommand{" +
                "keys=" + Arrays.toString(keys) +
                '}';
    }
}
