package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class UnLinkCommand implements Command {
    private String[] keys;

    public UnLinkCommand() {
    }

    public UnLinkCommand(String... keys) {
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
        return "UnLinkCommand{" +
                "keys=" + Arrays.toString(keys) +
                '}';
    }
}
