package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class SetExCommand implements Command {
    private String key;
    private int ex;
    private String value;

    public SetExCommand() {
    }

    public SetExCommand(String key, int ex, String value) {
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

    public int getEx() {
        return ex;
    }

    public void setEx(int ex) {
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
        return "SetExCommand{" +
                "key='" + key + '\'' +
                ", ex=" + ex +
                ", value='" + value + '\'' +
                '}';
    }
}
