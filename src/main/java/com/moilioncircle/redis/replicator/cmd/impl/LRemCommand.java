package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class LRemCommand implements Command {
    private final String key;
    private final int index;
    private final String value;

    public String getKey() {
        return key;
    }

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    public LRemCommand(String key, int index, String value) {
        this.key = key;
        this.index = index;
        this.value = value;
    }

    @Override
    public String toString() {
        return "LRemCommand{" +
                "key='" + key + '\'' +
                ", index=" + index +
                ", value='" + value + '\'' +
                '}';
    }
}
