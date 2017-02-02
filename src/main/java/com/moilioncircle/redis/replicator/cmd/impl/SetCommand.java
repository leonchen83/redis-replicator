package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class SetCommand implements Command {
    private final String key;
    private final String value;
    private final Integer ex;
    private final Long px;
    private final ExistType existType;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Integer getEx() {
        return ex;
    }

    public Long getPx() {
        return px;
    }

    public ExistType getExistType() {
        return existType;
    }

    public SetCommand(String key, String value, Integer ex, Long px, ExistType existType) {
        this.key = key;
        this.value = value;
        this.ex = ex;
        this.px = px;
        this.existType = existType;
    }

    @Override
    public String toString() {
        return "SetCommand{" +
                "name='" + key + '\'' +
                ", value='" + value + '\'' +
                ", ex=" + ex +
                ", px=" + px +
                ", existType=" + existType +
                '}';
    }
}
