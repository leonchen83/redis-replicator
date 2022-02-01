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

package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.CommandSpec;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
@CommandSpec(command = "GEOSEARCHSTORE")
public class GeoSearchStoreCommand extends AbstractCommand {
    
    private static final long serialVersionUID = 1L;
    
    private byte[] destination;
    private byte[] source;
    private FromMember fromMember;
    private FromLonLat fromLonLat;
    private ByRadius byRadius;
    private ByBox byBox;
    private Count count;
    private OrderType orderType = OrderType.NONE;
    private boolean withCoord;
    private boolean withDist;
    private boolean withHash;
    private boolean storeDist;
    
    public GeoSearchStoreCommand() {
    }
    
    public GeoSearchStoreCommand(byte[] destination, byte[] source, FromMember fromMember, FromLonLat fromLonLat,
                                 ByRadius byRadius, ByBox byBox, Count count, OrderType orderType,
                                 boolean withCoord, boolean withDist, boolean withHash, boolean storeDist) {
        this.destination = destination;
        this.source = source;
        this.fromMember = fromMember;
        this.fromLonLat = fromLonLat;
        this.byRadius = byRadius;
        this.byBox = byBox;
        this.count = count;
        this.orderType = orderType;
        this.withCoord = withCoord;
        this.withDist = withDist;
        this.withHash = withHash;
        this.storeDist = storeDist;
    }
    
    public byte[] getDestination() {
        return destination;
    }
    
    public void setDestination(byte[] destination) {
        this.destination = destination;
    }
    
    public byte[] getSource() {
        return source;
    }
    
    public void setSource(byte[] source) {
        this.source = source;
    }
    
    public FromMember getFromMember() {
        return fromMember;
    }
    
    public void setFromMember(FromMember fromMember) {
        this.fromMember = fromMember;
    }
    
    public FromLonLat getFromLonLat() {
        return fromLonLat;
    }
    
    public void setFromLonLat(FromLonLat fromLonLat) {
        this.fromLonLat = fromLonLat;
    }
    
    public ByRadius getByRadius() {
        return byRadius;
    }
    
    public void setByRadius(ByRadius byRadius) {
        this.byRadius = byRadius;
    }
    
    public ByBox getByBox() {
        return byBox;
    }
    
    public void setByBox(ByBox byBox) {
        this.byBox = byBox;
    }
    
    public Count getCount() {
        return count;
    }
    
    public void setCount(Count count) {
        this.count = count;
    }
    
    public OrderType getOrderType() {
        return orderType;
    }
    
    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }
    
    public boolean isWithCoord() {
        return withCoord;
    }
    
    public void setWithCoord(boolean withCoord) {
        this.withCoord = withCoord;
    }
    
    public boolean isWithDist() {
        return withDist;
    }
    
    public void setWithDist(boolean withDist) {
        this.withDist = withDist;
    }
    
    public boolean isWithHash() {
        return withHash;
    }
    
    public void setWithHash(boolean withHash) {
        this.withHash = withHash;
    }
    
    public boolean isStoreDist() {
        return storeDist;
    }
    
    public void setStoreDist(boolean storeDist) {
        this.storeDist = storeDist;
    }
}
