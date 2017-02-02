package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class PFAddCommand implements Command {
    private final String key;
    private final String elements[];

    public String getKey() {
        return key;
    }

    public String[] getElements() {
        return elements;
    }

    public PFAddCommand(String key, String... elements) {
        this.key = key;
        this.elements = elements;
    }

    @Override
    public String toString() {
        return "PFAddCommand{" +
                "key='" + key + '\'' +
                ", element=" + Arrays.toString(elements) +
                '}';
    }
}
