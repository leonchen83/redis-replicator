package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class HSetCommand implements Command {
    private final String key;
    private final String field;
    private final String value;

    public String getKey() {
        return key;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public HSetCommand(String key, String field, String value) {
        this.key = key;
        this.field = field;
        this.value = value;
    }

    @Override
    public String toString() {
        return "HSetCommand{" +
                "key='" + key + '\'' +
                ", field='" + field + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
