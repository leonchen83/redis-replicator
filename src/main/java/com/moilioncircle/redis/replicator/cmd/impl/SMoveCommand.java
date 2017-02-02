package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class SMoveCommand implements Command {
    private final String source;
    private final String destination;
    private final String member;

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getMember() {
        return member;
    }

    public SMoveCommand(String source, String destination, String member) {
        this.source = source;
        this.destination = destination;
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
