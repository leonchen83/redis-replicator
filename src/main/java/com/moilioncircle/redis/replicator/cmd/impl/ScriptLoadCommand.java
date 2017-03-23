package com.moilioncircle.redis.replicator.cmd.impl;

/**
 * Created by leon on 2/2/17.
 */
public class ScriptLoadCommand extends ScriptCommand {
    private String script;

    public ScriptLoadCommand() {
    }

    public ScriptLoadCommand(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public String toString() {
        return "ScriptLoadCommand{" +
                "script='" + script + '\'' +
                '}';
    }
}
