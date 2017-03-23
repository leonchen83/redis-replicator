package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class HDelCommand implements Command {
    private String key;
    private String fields[];

    public HDelCommand() {
    }

    public HDelCommand(String key, String... fields) {
        this.key = key;
        this.fields = fields;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
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
