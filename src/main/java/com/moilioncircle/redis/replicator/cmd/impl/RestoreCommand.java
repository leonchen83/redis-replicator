package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class RestoreCommand implements Command {
    private String key;
    private int ttl;
    private String serializedValue;
    private Boolean isReplace;

    public RestoreCommand() {
    }

    public RestoreCommand(String key, int ttl, String serializedValue, Boolean isReplace) {
        this.key = key;
        this.ttl = ttl;
        this.serializedValue = serializedValue;
        this.isReplace = isReplace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getSerializedValue() {
        return serializedValue;
    }

    public void setSerializedValue(String serializedValue) {
        this.serializedValue = serializedValue;
    }

    public Boolean getReplace() {
        return isReplace;
    }

    public void setReplace(Boolean replace) {
        isReplace = replace;
    }

    @Override
    public String toString() {
        return "RestoreCommand{" +
                "key='" + key + '\'' +
                ", ttl=" + ttl +
                ", serializedValue='" + serializedValue + '\'' +
                ", isReplace=" + isReplace +
                '}';
    }
}
