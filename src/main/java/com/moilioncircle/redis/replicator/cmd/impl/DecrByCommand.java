package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/5/17.
 */
public class DecrByCommand implements Command {
    private String key;
    private int value;

    public DecrByCommand() {
    }

    public DecrByCommand(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
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
