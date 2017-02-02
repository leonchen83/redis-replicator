package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class PublishCommand implements Command {
    private final String channel;
    private final String message;

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public PublishCommand(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    @Override
    public String toString() {
        return "PublishCommand{" +
                "channel='" + channel + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
