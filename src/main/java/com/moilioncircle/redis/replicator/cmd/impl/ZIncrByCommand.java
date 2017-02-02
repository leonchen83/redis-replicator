package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class ZIncrByCommand implements Command {
    private final String key;
    private final int increment;
    private final String member;

    public String getKey() {
        return key;
    }

    public int getIncrement() {
        return increment;
    }

    public String getMember() {
        return member;
    }

    public ZIncrByCommand(String key, int increment, String member) {
        this.key = key;
        this.increment = increment;
        this.member = member;
    }

    @Override
    public String toString() {
        return "ZIncrByCommand{" +
                "key='" + key + '\'' +
                ", increment='" + increment + '\'' +
                ", member='" + member + '\'' +
                '}';
    }

}
