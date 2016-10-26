package com.moilioncircle.redis.replicator.cmd.impl;

import java.io.Serializable;

/**
 * Created by leon on 8/20/16.
 */
public class Geo implements Serializable {
    private final String member;
    private final double longitude;
    private final double latitude;

    public String getMember() {
        return member;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

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
