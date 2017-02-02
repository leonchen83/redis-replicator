package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class SelectCommand implements Command {
    private final int index;

    public int getIndex() {
        return index;
    }

    public SelectCommand(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "SelectCommand{" +
                "index='" + index + '\'' +
                '}';
    }
}
