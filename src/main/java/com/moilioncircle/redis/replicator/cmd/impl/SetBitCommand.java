package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class SetBitCommand implements Command {
    private String key;
    private int offset;
    private int value;

    public SetBitCommand() {
    }

    public SetBitCommand(String key, int offset, int value) {
        this.key = key;
        this.offset = offset;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
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
