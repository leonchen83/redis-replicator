package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class FlushDBCommand implements Command {
    private final Boolean isAsync;

    public FlushDBCommand(final Boolean isAsync) {
        this.isAsync = isAsync;
    }

    public Boolean isAsync() {
        return isAsync;
    }

    @Override
    public String toString() {
        return "FlushDBCommand{" +
                "isAsync=" + isAsync +
                '}';
    }
}
