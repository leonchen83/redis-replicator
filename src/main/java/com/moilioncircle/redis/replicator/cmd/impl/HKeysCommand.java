package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class HKeysCommand implements Command {
    private final String key;

    public String getKey() {
        return key;
    }

    public HKeysCommand(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "HKeysCommand{" +
                "key='" + key + '\'' +
                '}';
    }
}
