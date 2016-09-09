package com.moilioncircle.redis.replicator.cmd.impl;

import java.io.Serializable;

/**
 * Created by leon on 8/20/16.
 */
public class GEO implements Serializable {
    public final String member;
    public final double longitude;
    public final double latitude;

    public GEO(String member, double longitude, double latitude) {
        this.member = member;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "GEO{" +
                "member='" + member + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
