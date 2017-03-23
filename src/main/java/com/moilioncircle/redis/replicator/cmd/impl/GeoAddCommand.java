package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class GeoAddCommand implements Command {
    private String key;
    private Geo[] geos;

    public GeoAddCommand() {
    }

    public GeoAddCommand(String key, Geo[] geos) {
        this.key = key;
        this.geos = geos;
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

    @Override
    public String toString() {
        return "GeoAddCommand{" +
                "key='" + key + '\'' +
                ", geos=" + Arrays.toString(geos) +
                '}';
    }
}
