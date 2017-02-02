package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class ZAddCommand implements Command {
    private final String key;
    private final ExistType existType;
    private final Boolean isCh;
    private final Boolean isIncr;
    private final ZSetEntry[] zSetEntries;

    public String getKey() {
        return key;
    }

    public ExistType getExistType() {
        return existType;
    }

    public Boolean getCh() {
        return isCh;
    }

    public Boolean getIncr() {
        return isIncr;
    }

    public ZSetEntry[] getZSetEntries() {
        return zSetEntries;
    }

    public ZAddCommand(String key, ExistType existType, Boolean isCh, Boolean isIncr, ZSetEntry[] zSetEntries) {
        this.key = key;
        this.existType = existType;
        this.isCh = isCh;
        this.isIncr = isIncr;
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
