package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class RenameNxCommand implements Command {
    private final String key;
    private final String newKey;

    public String getKey() {
        return key;
    }

    public String getNewKey() {
        return newKey;
    }

    public RenameNxCommand(String key, String newKey) {
        this.key = key;
        this.newKey = newKey;
    }

    @Override
    public String toString() {
        return "RenameNxCommand{" +
                "key='" + key + '\'' +
                ", newKey=" + newKey +
                '}';
    }
}
