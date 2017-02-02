package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class PSetExCommand implements Command {
    private final String key;
    private final long ex;
    private final String value;

    public String getKey() {
        return key;
    }

    public long getEx() {
        return ex;
    }

    public String getValue() {
        return value;
    }

    public PSetExCommand(String key, long ex, String value) {
        this.key = key;
        this.value = value;
        this.ex = ex;
    }

    @Override
    public String toString() {
        return "PSetExCommand{" +
                "key='" + key + '\'' +
                ", ex=" + ex +
                ", value='" + value + '\'' +
                '}';
    }
}
