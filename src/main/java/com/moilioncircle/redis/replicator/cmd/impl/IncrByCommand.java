package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class IncrByCommand implements Command {
    private final String key;
    private final int value;

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public IncrByCommand(String key, int value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "IncrByCommand{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
