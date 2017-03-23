package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.List;

/**
 * Created by leon on 2/2/17.
 */
public class BitFieldCommand implements Command {
    private String key;
    private List<Statement> statements;
    private List<OverFlow> overFlows;

    public BitFieldCommand() {
    }

    public BitFieldCommand(String key,
                           List<Statement> statements,
                           List<OverFlow> overFlows) {
        this.key = key;
        this.statements = statements;
        this.overFlows = overFlows;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public List<OverFlow> getOverFlows() {
        return overFlows;
    }

    public void setOverFlows(List<OverFlow> overFlows) {
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
