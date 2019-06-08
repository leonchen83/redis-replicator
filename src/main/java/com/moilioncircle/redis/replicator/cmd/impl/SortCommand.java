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

/**
 * @author Leon Chen
 * @since 2.3.1
 */
public class SortCommand extends GenericKeyCommand {

    private static final long serialVersionUID = 1L;

    private byte[] byPattern;
    private Limit limit;
    private byte[][] getPatterns;
    private OrderType order;
    private boolean alpha;
    private byte[] destination;

    public SortCommand() {
    }

    public SortCommand(byte[] key, byte[] byPattern, Limit limit, byte[][] getPatterns, OrderType order, boolean alpha, byte[] destination) {
        super(key);
        this.byPattern = byPattern;
        this.limit = limit;
        this.getPatterns = getPatterns;
        this.order = order;
        this.alpha = alpha;
        this.destination = destination;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public byte[] getByPattern() {
        return byPattern;
    }

    public void setByPattern(byte[] byPattern) {
        this.byPattern = byPattern;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public byte[][] getGetPatterns() {
        return getPatterns;
    }

    public void setGetPatterns(byte[][] getPatterns) {
        this.getPatterns = getPatterns;
    }

    public OrderType getOrder() {
        return order;
    }

    public void setOrder(OrderType order) {
        this.order = order;
    }

    public boolean isAlpha() {
        return alpha;
    }

    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }

    public byte[] getDestination() {
        return destination;
    }

    public void setDestination(byte[] destination) {
        this.destination = destination;
    }
}