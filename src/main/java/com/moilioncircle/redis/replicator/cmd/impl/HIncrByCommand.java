package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class HIncrByCommand implements Command {
    private final String key;
    private final String field;
    private final int increment;

    public String getKey() {
        return key;
    }

    public String getField() {
        return field;
    }

    public int getIncrement() {
        return increment;
    }

    public HIncrByCommand(String key, String field, int increment) {
        this.key = key;
        this.field = field;
        this.increment = increment;
    }

    @Override
    public String toString() {
        return "HIncrByCommand{" +
                "key='" + key + '\'' +
                ", field='" + field + '\'' +
                ", increment='" + increment + '\'' +
                '}';
    }
}
