package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class SetNxCommand implements Command {
    private final String key;
    private final String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public SetNxCommand(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SetNxCommand{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
