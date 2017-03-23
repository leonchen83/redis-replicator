package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class FlushAllCommand implements Command {
    private Boolean isAsync;

    public FlushAllCommand() {
    }

    public FlushAllCommand(final Boolean isAsync) {
        this.isAsync = isAsync;
    }

    public Boolean isAsync() {
        return isAsync;
    }

    public Boolean getAsync() {
        return isAsync;
    }

    public void setAsync(Boolean async) {
        isAsync = async;
    }

    @Override
    public String toString() {
        return "FlushAllCommand{" +
                "isAsync=" + isAsync +
                '}';
    }
}
