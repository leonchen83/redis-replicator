/*
 * Copyright 2016 leon chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.cmd.impl;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ScriptLoadCommand extends ScriptCommand {
    private String script;
    private byte[] rawScript;

    public ScriptLoadCommand() {
    }

    public ScriptLoadCommand(String script) {
        this(script, null);
    }

    public ScriptLoadCommand(String script, byte[] rawScript) {
        this.script = script;
        this.rawScript = rawScript;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public byte[] getRawScript() {
        return rawScript;
    }

    public void setRawScript(byte[] rawScript) {
        this.rawScript = rawScript;
    }

    @Override
    public String toString() {
        return "ScriptLoadCommand{" +
                "script='" + script + '\'' +
                '}';
    }
}
