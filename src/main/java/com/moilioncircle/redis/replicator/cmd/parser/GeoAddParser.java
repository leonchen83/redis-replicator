/*
 * Copyright 2016-2018 Leon Chen
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

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toDouble;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

import java.util.ArrayList;
import java.util.List;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.ExistType;
import com.moilioncircle.redis.replicator.cmd.impl.Geo;
import com.moilioncircle.redis.replicator.cmd.impl.GeoAddCommand;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class GeoAddParser implements CommandParser<GeoAddCommand> {
    @Override
    public GeoAddCommand parse(Object[] command) {
        int idx = 1;
        byte[] key = toBytes(command[idx]);
        idx++;
        List<Geo> list = new ArrayList<>();
        ExistType existType = ExistType.NONE;
        boolean ch = false;
        for (; idx < command.length; idx++) {
            String token = toRune(command[idx]);
            if (isEquals(token, "NX")) {
                existType = ExistType.NX;
            } else if (isEquals(token, "XX")) {
                existType = ExistType.XX;
            } else if (isEquals(token, "CH")) {
                ch = true;
            } else {
                double longitude = toDouble(command[idx++]);
                double latitude = toDouble(command[idx++]);
                byte[] member = toBytes(command[idx]);
                list.add(new Geo(member, longitude, latitude));
            }
        }
        Geo[] geos = new Geo[list.size()];
        list.toArray(geos);
        return new GeoAddCommand(key, geos, existType, ch);
    }

}
