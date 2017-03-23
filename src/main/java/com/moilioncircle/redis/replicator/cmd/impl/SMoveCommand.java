package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class SMoveCommand implements Command {
    private String source;
    private String destination;
    private String member;

    public SMoveCommand() {
    }

    public SMoveCommand(String source, String destination, String member) {
        this.source = source;
        this.destination = destination;
        this.member = member;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    @Override
    public String toString() {
        return "SMoveCommand{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", member='" + member + '\'' +
                '}';
    }
}
