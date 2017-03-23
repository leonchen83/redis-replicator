package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class BRPopLPushCommand implements Command {
    private String source;
    private String destination;
    private int timeout;

    public BRPopLPushCommand() {
    }

    public BRPopLPushCommand(String source, String destination, int timeout) {
        this.source = source;
        this.destination = destination;
        this.timeout = timeout;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
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
