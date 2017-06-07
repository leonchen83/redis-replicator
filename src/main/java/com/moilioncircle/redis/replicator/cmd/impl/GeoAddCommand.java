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

import java.util.Arrays;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class GeoAddCommand implements Command {
    private String key;
    private Geo[] geos;
    private byte[] rawKey;

    public GeoAddCommand() {
    }

    public GeoAddCommand(String key, Geo[] geos) {
        this(key, geos, null);
    }

    public GeoAddCommand(String key, Geo[] geos, byte[] rawKey) {
        this.key = key;
        this.geos = geos;
        this.rawKey = rawKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Geo[] getGeos() {
        return geos;
    }

    public void setGeos(Geo[] geos) {
        this.geos = geos;
    }

    public byte[] getRawKey() {
        return rawKey;
    }

    public void setRawKey(byte[] rawKey) {
        this.rawKey = rawKey;
    }

    @Override
    public String toString() {
        return "GeoAddCommand{" +
                "key='" + key + '\'' +
                ", geos=" + Arrays.toString(geos) +
                '}';
    }
}
