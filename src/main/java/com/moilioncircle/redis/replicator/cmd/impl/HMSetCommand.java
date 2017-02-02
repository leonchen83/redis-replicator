package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Map;

/**
 * Created by leon on 2/2/17.
 */
public class HMSetCommand implements Command {
    private final String key;
    private final Map<String, String> fields;

    public String getKey() {
        return key;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public HMSetCommand(String key, Map<String, String> fields) {
        this.key = key;
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "HMSetCommand{" +
                "key='" + key + '\'' +
                ", fields=" + fields +
                '}';
    }
}
