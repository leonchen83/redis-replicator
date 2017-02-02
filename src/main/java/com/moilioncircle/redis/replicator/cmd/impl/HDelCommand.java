package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class HDelCommand implements Command {
    private final String key;
    private final String fields[];

    public String getKey() {
        return key;
    }

    public String[] getFields() {
        return fields;
    }

    public HDelCommand(String key, String... fields) {
        this.key = key;
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "HDelCommand{" +
                "key='" + key + '\'' +
                ", fields=" + Arrays.toString(fields) +
                '}';
    }
}
