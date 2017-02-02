package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class ZInterStoreCommand implements Command {
    private final String destination;
    private final int numkeys;
    private final String[] keys;
    private final double[] weights;
    private final AggregateType aggregateType;

    public String getDestination() {
        return destination;
    }

    public int getNumkeys() {
        return numkeys;
    }

    public String[] getKeys() {
        return keys;
    }

    public double[] getWeights() {
        return weights;
    }

    public AggregateType getAggregateType() {
        return aggregateType;
    }

    public ZInterStoreCommand(String destination, int numkeys, String[] keys, double[] weights, AggregateType aggregateType) {
        this.destination = destination;
        this.numkeys = numkeys;
        this.keys = keys;
        this.weights = weights;
        this.aggregateType = aggregateType;
    }

    @Override
    public String toString() {
        return "ZInterStoreCommand{" +
                "destination='" + destination + '\'' +
                ", numkeys=" + numkeys +
                ", keys=" + Arrays.toString(keys) +
                ", weights=" + Arrays.toString(weights) +
                ", aggregateType=" + aggregateType +
                '}';
    }

}
