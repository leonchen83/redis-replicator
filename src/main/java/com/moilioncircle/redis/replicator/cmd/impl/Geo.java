package com.moilioncircle.redis.replicator.cmd.impl;

import java.io.Serializable;

/**
 * Created by leon on 8/20/16.
 */
public class Geo implements Serializable {
    public final String member;
    public final double longitude;
    public final double latitude;

    public Geo(String member, double longitude, double latitude) {
        this.member = member;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Geo{" +
                "member='" + member + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
