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
import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;

/**
 * Created by leon on 8/13/16.
 */
public class SetParser implements CommandParser<SetCommand> {

    @Override
    public SetCommand parse(CommandName cmdName, Object[] params) {
        String key = (String) params[0];
        String value = (String) params[1];
        int idx = 2;
        ExistType existType = ExistType.NONE;
        Integer ex = null;
        Long px = null;
        while (idx < params.length) {
            String param = (String) params[idx++];
            if (param.equalsIgnoreCase("NX")) {
                existType = ExistType.NX;
                break;
            } else if (param.equalsIgnoreCase("XX")) {
                existType = ExistType.XX;
                break;
            } else if (param.equalsIgnoreCase("EX")) {
                ex = Integer.valueOf((String) params[idx++]);
                break;
            } else if (param.equalsIgnoreCase("PX")) {
                px = Long.valueOf((String) params[idx++]);
                break;
            }
        }
        return new SetCommand(key, value, ex, px, existType);
    }

}
