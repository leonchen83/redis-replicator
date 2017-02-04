package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by Adrian Yao on 2/2/17.
 */
public class SRemCommand implements Command {

    private final String key;
    private final String[] members;

    public SRemCommand(String key, String[] members) {
        this.key = key;
        this.members = members;
    }

    public String getKey() {
        return key;
    }

    public String[] getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "SRemCommand{" +
                "key='" + key + '\'' +
                ", members=" + Arrays.toString(members) +
                '}';
    }
}
