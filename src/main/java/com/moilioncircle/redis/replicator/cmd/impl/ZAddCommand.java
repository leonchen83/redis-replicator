package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class ZAddCommand implements Command {
    private String key;
    private ExistType existType;
    private Boolean isCh;
    private Boolean isIncr;
    private ZSetEntry[] zSetEntries;

    public ZAddCommand() {
    }

    public ZAddCommand(String key, ExistType existType, Boolean isCh, Boolean isIncr, ZSetEntry[] zSetEntries) {
        this.key = key;
        this.existType = existType;
        this.isCh = isCh;
        this.isIncr = isIncr;
        this.zSetEntries = zSetEntries;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ExistType getExistType() {
        return existType;
    }

    public void setExistType(ExistType existType) {
        this.existType = existType;
    }

    public Boolean getCh() {
        return isCh;
    }

    public void setCh(Boolean ch) {
        isCh = ch;
    }

    public Boolean getIncr() {
        return isIncr;
    }

    public void setIncr(Boolean incr) {
        isIncr = incr;
    }

    public ZSetEntry[] getZSetEntries() {
        return zSetEntries;
    }

    public ZSetEntry[] getzSetEntries() {
        return zSetEntries;
    }

    public void setzSetEntries(ZSetEntry[] zSetEntries) {
        this.zSetEntries = zSetEntries;
    }

    @Override
    public String toString() {
        return "ZAddCommand{" +
                "key='" + key + '\'' +
                ", existType=" + existType +
                ", isCh=" + isCh +
                ", isIncr=" + isIncr +
                ", zSetEntries=" + Arrays.toString(zSetEntries) +
                '}';
    }

}
