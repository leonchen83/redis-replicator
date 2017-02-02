package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class RPushCommand implements Command {
    private final String key;
    private final String[] values;

    public String getKey() {
        return key;
    }

    public String[] getValues() {
        return values;
    }

    public RPushCommand(String key, String... values) {
        this.key = key;
        this.values = values;
    }

    @Override
    public String toString() {
        return "RPushCommand{" +
                "key='" + key + '\'' +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}
