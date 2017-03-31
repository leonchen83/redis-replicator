package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class IncrByCommand implements Command {
    private String key;
    private long value;

    public IncrByCommand() {
    }

    public IncrByCommand(String key, long value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
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
