package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class IncrCommand implements Command {
    private String key;

    public IncrCommand() {
    }

    public IncrCommand(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "IncrCommand{" +
                "key='" + key + '\'' +
                '}';
    }
}
