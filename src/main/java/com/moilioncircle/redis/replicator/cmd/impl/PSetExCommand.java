package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class PSetExCommand implements Command {
    private String key;
    private long ex;
    private String value;

    public PSetExCommand() {
    }

    public PSetExCommand(String key, long ex, String value) {
        this.key = key;
        this.value = value;
        this.ex = ex;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getEx() {
        return ex;
    }

    public void setEx(long ex) {
        this.ex = ex;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PSetExCommand{" +
                "key='" + key + '\'' +
                ", ex=" + ex +
                ", value='" + value + '\'' +
                '}';
    }
}
