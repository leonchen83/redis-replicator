package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class LPushXCommand implements Command {
    private final String key;
    private final String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public LPushXCommand(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "LPushXCommand{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
