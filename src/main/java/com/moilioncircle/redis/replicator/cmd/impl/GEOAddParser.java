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

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leon on 8/20/16.
 */
public class GEOAddParser implements CommandParser<GEOAddParser.GEOAddCommand> {
    @Override
    public GEOAddCommand parse(CommandName cmdName, Object[] params) {
        int idx = 0;
        String key = (String) params[idx++];
        List<GEO> list = new ArrayList<>();
        while (idx < params.length) {
            double longitude = Double.parseDouble((String) params[idx++]);
            double latitude = Double.parseDouble((String) params[idx++]);
            String member = (String) params[idx++];
            list.add(new GEO(member, longitude, latitude));
        }
        GEO[] geos = new GEO[list.size()];
        list.toArray(geos);
        return new GEOAddCommand(key, geos);
    }

    public static class GEOAddCommand implements Command {
        public final String key;
        public final GEO[] geos;

        public GEOAddCommand(String key, GEO[] geos) {
            this.key = key;
            this.geos = geos;
        }

        @Override
        public String toString() {
            return "GEOAddCommand{" +
                    "key='" + key + '\'' +
                    ", geos=" + Arrays.toString(geos) +
                    '}';
        }

        @Override
        public CommandName name() {
            return CommandName.name("GEOADD");
        }
    }
}
