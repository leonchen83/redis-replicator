package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class LInsertCommand implements Command {
    private String key;
    private LInsertType lInsertType;
    private String pivot;
    private String value;

    public LInsertCommand() {
    }

    public LInsertCommand(String key, LInsertType lInsertType, String pivot, String value) {
        this.key = key;
        this.pivot = pivot;
        this.value = value;
        this.lInsertType = lInsertType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LInsertType getlInsertType() {
        return lInsertType;
    }

    public void setlInsertType(LInsertType lInsertType) {
        this.lInsertType = lInsertType;
    }

    public String getPivot() {
        return pivot;
    }

    public void setPivot(String pivot) {
        this.pivot = pivot;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
