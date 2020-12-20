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
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toInt;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.ByBox;
import com.moilioncircle.redis.replicator.cmd.impl.ByRadius;
import com.moilioncircle.redis.replicator.cmd.impl.Count;
import com.moilioncircle.redis.replicator.cmd.impl.FromLonLat;
import com.moilioncircle.redis.replicator.cmd.impl.FromMember;
import com.moilioncircle.redis.replicator.cmd.impl.GeoSearchStoreCommand;
import com.moilioncircle.redis.replicator.cmd.impl.OrderType;
import com.moilioncircle.redis.replicator.cmd.impl.UnitType;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class GeoSearchStoreParser implements CommandParser<GeoSearchStoreCommand> {
    
    @Override
    public GeoSearchStoreCommand parse(Object[] command) {
        int idx = 1;
        byte[] destination = toBytes(command[idx++]);
        byte[] source = toBytes(command[idx++]);
        boolean withCoord = false;
        boolean withDist = false;
        boolean withHash = false;
        boolean storeDist = false;
        FromMember fromMember = null;
        FromLonLat fromLonLat = null;
        ByRadius byRadius = null;
        ByBox byBox = null;
        OrderType orderType = OrderType.NONE;
        Count count = null;
        for (int i = idx; i < command.length; i++) {
            String token = toRune(command[i]);
            if (isEquals(token, "FROMMEMBER")) {
                i++;
                fromMember = new FromMember(toBytes(command[i]));
            } else if (isEquals(token, "FROMLONLAT")) {
                i++;
                double longitude = toDouble(command[i]);
                i++;
                double latitude = toDouble(command[i]);
                fromLonLat = new FromLonLat(longitude, latitude);
            } else if (isEquals(token, "BYRADIUS")) {
                i++;
                double radius = toDouble(command[i]);
                i++;
                UnitType unit = parseUnit(toRune(command[i]));
                byRadius = new ByRadius(radius, unit);
            } else if (isEquals(token, "BYBOX")) {
                i++;
                double width = toDouble(command[i]);
                i++;
                double height = toDouble(command[i]);
                i++;
                UnitType unit = parseUnit(toRune(command[i]));
                byBox = new ByBox(width, height, unit);
            } else if (isEquals(token, "ASC")) {
                orderType = OrderType.ASC;
            } else if (isEquals(token, "DESC")) {
                orderType = OrderType.DESC;
            } else if (isEquals(token, "COUNT")) {
                i++;
                count = new Count(toInt(command[i]));
            } else if (isEquals(token, "WITHCOORD")) {
                withCoord = true;
            } else if (isEquals(token, "WITHDIST")) {
                withDist = true;
            } else if (isEquals(token, "WITHHASH")) {
                withHash = true;
            } else if (isEquals(token, "STOREDIST")) {
                storeDist = true;
            } else {
                throw new AssertionError("parse [GEOSEARCHSTORE] command error." + token);
            }
        }
        return new GeoSearchStoreCommand(destination, source, fromMember, fromLonLat, byRadius, byBox, count, orderType, withCoord, withDist, withHash, storeDist);
    }
    
    private UnitType parseUnit(String token) {
        if (isEquals(token, "M")) {
            return UnitType.M;
        } else if (isEquals(token, "KM")) {
            return UnitType.KM;
        } else if (isEquals(token, "FT")) {
            return UnitType.FT;
        } else if (isEquals(token, "MI")) {
            return UnitType.MI;
        } else {
            throw new AssertionError("parse [GEOSEARCHSTORE] command error." + token);
        }
    }
}
