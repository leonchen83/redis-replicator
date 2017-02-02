package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.List;

/**
 * Created by leon on 2/2/17.
 */
public class BitFieldCommand implements Command {
    private final String key;
    private final List<Statement> statements;
    private final List<OverFlow> overFlows;

    public String getKey() {
        return key;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public List<OverFlow> getOverFlows() {
        return overFlows;
    }

    public BitFieldCommand(String key,
                           List<Statement> statements,
                           List<OverFlow> overFlows) {
        this.key = key;
        this.statements = statements;
        this.overFlows = overFlows;
    }

    @Override
    public String toString() {
        return "BitFieldCommand{" +
                "key='" + key + '\'' +
                ", statements=" + statements +
                ", overFlows=" + overFlows +
                '}';
    }
}
