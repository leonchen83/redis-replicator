package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class ExpireAtCommand implements Command {
    private final String key;
    private final long ex;

    public String getKey() {
        return key;
    }

    public long getEx() {
        return ex;
    }

    public ExpireAtCommand(String key, long ex) {
        this.key = key;
        this.ex = ex;
    }

    @Override
    public String toString() {
        return "ExpireAtCommand{" +
                "key='" + key + '\'' +
                ", ex=" + ex +
                '}';
    }
}
