package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class EvalCommand implements Command {
    private final String script;
    private final int numkeys;
    private final String[] keys;
    private final String[] args;

    public String getScript() {
        return script;
    }

    public int getNumkeys() {
        return numkeys;
    }

    public String[] getKeys() {
        return keys;
    }

    public String[] getArgs() {
        return args;
    }

    public EvalCommand(String script, int numkeys, String[] keys, String[] args) {
        this.script = script;
        this.numkeys = numkeys;
        this.keys = keys;
        this.args = args;
    }

    @Override
    public String toString() {
        return "EvalCommand{" +
                "script='" + script + '\'' +
                ", numkeys=" + numkeys +
                ", keys=" + Arrays.toString(keys) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
