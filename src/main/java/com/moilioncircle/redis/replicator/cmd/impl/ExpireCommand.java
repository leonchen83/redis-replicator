package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class ExpireCommand implements Command {
    private final String key;
    private final int ex;

    public String getKey() {
        return key;
    }

    public int getEx() {
        return ex;
    }

    public ExpireCommand(String key, int ex) {
        this.key = key;
        this.ex = ex;
    }

    @Override
    public String toString() {
        return "ExpireCommand{" +
                "key='" + key + '\'' +
                ", ex=" + ex +
                '}';
    }
}
