package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by Adrian Yao on 2/2/17.
 */
public class SRemCommand implements Command {

    private String key;
    private String[] members;

    public SRemCommand() {
    }

    public SRemCommand(String key, String[] members) {
        this.key = key;
        this.members = members;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getMembers() {
        return members;
    }

    public void setMembers(String[] members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "SRemCommand{" +
                "key='" + key + '\'' +
                ", members=" + Arrays.toString(members) +
                '}';
    }
}
