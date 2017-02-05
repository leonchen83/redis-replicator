package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/5/17.
 */
public class DecrByCommand implements Command {
    private final String key;
    private final int value;

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public DecrByCommand(String key, int value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "DecrByCommand{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
