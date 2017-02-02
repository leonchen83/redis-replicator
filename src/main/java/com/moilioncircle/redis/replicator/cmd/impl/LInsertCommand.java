package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class LInsertCommand implements Command {
    private final String key;
    private final LInsertType lInsertType;
    private final String pivot;
    private final String value;

    public String getKey() {
        return key;
    }

    public LInsertType getlInsertType() {
        return lInsertType;
    }

    public String getPivot() {
        return pivot;
    }

    public String getValue() {
        return value;
    }

    public LInsertCommand(String key, LInsertType lInsertType, String pivot, String value) {
        this.key = key;
        this.pivot = pivot;
        this.value = value;
        this.lInsertType = lInsertType;
    }

    @Override
    public String toString() {
        return "LInsertCommand{" +
                "key='" + key + '\'' +
                ", lInsertType=" + lInsertType +
                ", pivot='" + pivot + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
