package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class SUnionStoreCommand implements Command {
    private final String destination;
    private final String keys[];

    public String getDestination() {
        return destination;
    }

    public String[] getKeys() {
        return keys;
    }

    public SUnionStoreCommand(String destination, String... keys) {
        this.destination = destination;
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "SUnionStoreCommand{" +
                "destination='" + destination + '\'' +
                ", key=" + Arrays.toString(keys) +
                '}';
    }

}
