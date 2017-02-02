package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class BRPopLPushCommand implements Command {
    private final String source;
    private final String destination;
    private final int timeout;

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getTimeout() {
        return timeout;
    }

    public BRPopLPushCommand(String source, String destination, int timeout) {
        this.source = source;
        this.destination = destination;
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "BRPopLPushCommand{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
