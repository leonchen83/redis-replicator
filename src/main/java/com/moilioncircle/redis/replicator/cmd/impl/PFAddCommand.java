package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class PFAddCommand implements Command {
    private String key;
    private String elements[];

    public PFAddCommand() {
    }

    public PFAddCommand(String key, String... elements) {
        this.key = key;
        this.elements = elements;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getElements() {
        return elements;
    }

    public void setElements(String[] elements) {
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
