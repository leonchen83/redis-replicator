package com.moilioncircle.redis.replicator.cmd.impl;

/**
 * Created by leon on 2/2/17.
 */
public class ScriptLoadCommand extends ScriptCommand {
    private final String script;

    public String getScript() {
        return script;
    }

    public ScriptLoadCommand(String script) {
        this.script = script;
    }

    @Override
    public String toString() {
        return "ScriptLoadCommand{" +
                "script='" + script + '\'' +
                '}';
    }
}
