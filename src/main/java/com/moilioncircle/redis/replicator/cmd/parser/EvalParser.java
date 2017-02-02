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

package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.EvalCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leon on 8/14/16.
 */
public class EvalParser implements CommandParser<EvalCommand> {
    @Override
    public EvalCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String script = (String) params[idx++];
        int numkeys = Integer.parseInt((String) params[idx++]);
        String[] keys = new String[numkeys];
        for (int i = 0; i < numkeys; i++) {
            keys[i] = (String) params[idx++];
        }
        List<String> list = new ArrayList<>();
        while (idx < params.length) {
            list.add((String) params[idx++]);
        }
        String[] args = new String[list.size()];
        list.toArray(args);
        return new EvalCommand(script, numkeys, keys, args);
    }

}
