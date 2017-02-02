package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class RPushXCommand implements Command {
    private final String key;
    private final String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public RPushXCommand(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "RPushXCommand{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
