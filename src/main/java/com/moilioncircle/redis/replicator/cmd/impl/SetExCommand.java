package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class SetExCommand implements Command {
    private final String key;
    private final int ex;
    private final String value;

    public String getKey() {
        return key;
    }

    public int getEx() {
        return ex;
    }

    public String getValue() {
        return value;
    }

    public SetExCommand(String key, int ex, String value) {
        this.key = key;
        this.value = value;
        this.ex = ex;
    }

    @Override
    public String toString() {
        return "SetExCommand{" +
                "key='" + key + '\'' +
                ", ex=" + ex +
                ", value='" + value + '\'' +
                '}';
    }
}
