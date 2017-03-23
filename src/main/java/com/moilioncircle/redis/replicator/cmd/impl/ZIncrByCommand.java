package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class ZIncrByCommand implements Command {
    private String key;
    private int increment;
    private String member;

    public ZIncrByCommand() {
    }

    public ZIncrByCommand(String key, int increment, String member) {
        this.key = key;
        this.increment = increment;
        this.member = member;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
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
