package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class SetBitCommand implements Command {
    private final String key;
    private final int offset;
    private final int value;

    public String getKey() {
        return key;
    }

    public int getOffset() {
        return offset;
    }

    public int getValue() {
        return value;
    }

    public SetBitCommand(String key, int offset, int value) {
        this.key = key;
        this.offset = offset;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SetBitCommand{" +
                "key='" + key + '\'' +
                ", offset=" + offset +
                ", value=" + value +
                '}';
    }
}
