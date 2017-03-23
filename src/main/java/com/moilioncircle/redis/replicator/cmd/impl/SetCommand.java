package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class SetCommand implements Command {
    private String key;
    private String value;
    private Integer ex;
    private Long px;
    private ExistType existType;

    public SetCommand() {
    }

    public SetCommand(String key, String value, Integer ex, Long px, ExistType existType) {
        this.key = key;
        this.value = value;
        this.ex = ex;
        this.px = px;
        this.existType = existType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getEx() {
        return ex;
    }

    public void setEx(Integer ex) {
        this.ex = ex;
    }

    public Long getPx() {
        return px;
    }

    public void setPx(Long px) {
        this.px = px;
    }

    public ExistType getExistType() {
        return existType;
    }

    public void setExistType(ExistType existType) {
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
