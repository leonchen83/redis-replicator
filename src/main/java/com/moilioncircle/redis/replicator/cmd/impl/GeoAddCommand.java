package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class GeoAddCommand implements Command {
    private final String key;
    private final Geo[] geos;

    public String getKey() {
        return key;
    }

    public Geo[] getGeos() {
        return geos;
    }

    public GeoAddCommand(String key, Geo[] geos) {
        this.key = key;
        this.geos = geos;
    }

    @Override
    public String toString() {
        return "GeoAddCommand{" +
                "key='" + key + '\'' +
                ", geos=" + Arrays.toString(geos) +
                '}';
    }
}
