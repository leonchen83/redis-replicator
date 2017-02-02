package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class ZRemCommand implements Command {
    private final String key;
    private final String[] members;

    public String getKey() {
        return key;
    }

    public String[] getMembers() {
        return members;
    }

    public ZRemCommand(String key, String... members) {
        this.key = key;
        this.members = members;
    }

    @Override
    public String toString() {
        return "ZRemCommand{" +
                "key='" + key + '\'' +
                ", members=" + Arrays.toString(members) +
                '}';
    }

}
