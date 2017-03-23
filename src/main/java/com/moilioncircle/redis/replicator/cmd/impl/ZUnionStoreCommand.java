package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class ZUnionStoreCommand implements Command {
    private String destination;
    private int numkeys;
    private String[] keys;
    private double[] weights;
    private AggregateType aggregateType;

    public ZUnionStoreCommand() {
    }

    public ZUnionStoreCommand(String destination, int numkeys, String[] keys, double[] weights, AggregateType aggregateType) {
        this.destination = destination;
        this.numkeys = numkeys;
        this.keys = keys;
        this.weights = weights;
        this.aggregateType = aggregateType;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getNumkeys() {
        return numkeys;
    }

    public void setNumkeys(int numkeys) {
        this.numkeys = numkeys;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public AggregateType getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(AggregateType aggregateType) {
        this.aggregateType = aggregateType;
    }

    @Override
    public String toString() {
        return "ZUnionStoreCommand{" +
                "destination='" + destination + '\'' +
                ", numkeys=" + numkeys +
                ", keys=" + Arrays.toString(keys) +
                ", weights=" + Arrays.toString(weights) +
                ", aggregateType=" + aggregateType +
                '}';
    }

}
