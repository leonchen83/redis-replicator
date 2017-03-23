package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Map;

/**
 * Created by leon on 2/2/17.
 */
public class HMSetCommand implements Command {
    private String key;
    private Map<String, String> fields;

    public HMSetCommand() {
    }

    public HMSetCommand(String key, Map<String, String> fields) {
        this.key = key;
        this.fields = fields;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
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
