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

import java.io.Serializable;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class Geo implements Serializable {
    private String member;
    private double longitude;
    private double latitude;
    private byte[] rawMember;

    public Geo() {
    }

    public Geo(String member, double longitude, double latitude) {
        this(member, longitude, latitude, null);
    }

    public Geo(String member, double longitude, double latitude, byte[] rawMember) {
        this.member = member;
        this.longitude = longitude;
        this.latitude = latitude;
        this.rawMember = rawMember;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public byte[] getRawMember() {
        return rawMember;
    }

    public void setRawMember(byte[] rawMember) {
        this.rawMember = rawMember;
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
