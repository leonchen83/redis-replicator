package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class RenameNxCommand implements Command {
    private String key;
    private String newKey;

    public RenameNxCommand() {
    }

    public RenameNxCommand(String key, String newKey) {
        this.key = key;
        this.newKey = newKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNewKey() {
        return newKey;
    }

    public void setNewKey(String newKey) {
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
