package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class RestoreCommand implements Command {
    private final String key;
    private final int ttl;
    private final String serializedValue;
    private final Boolean isReplace;

    public String getKey() {
        return key;
    }

    public int getTtl() {
        return ttl;
    }

    public String getSerializedValue() {
        return serializedValue;
    }

    public Boolean getReplace() {
        return isReplace;
    }

    public RestoreCommand(String key, int ttl, String serializedValue, Boolean isReplace) {
        this.key = key;
        this.ttl = ttl;
        this.serializedValue = serializedValue;
        this.isReplace = isReplace;
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
