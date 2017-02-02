package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class PFMergeCommand implements Command {
    private final String destkey;
    private final String sourcekeys[];

    public String getDestkey() {
        return destkey;
    }

    public String[] getSourcekeys() {
        return sourcekeys;
    }

    public PFMergeCommand(String destkey, String... sourcekeys) {
        this.destkey = destkey;
        this.sourcekeys = sourcekeys;
    }

    @Override
    public String toString() {
        return "PFMergeCommand{" +
                "destkey='" + destkey + '\'' +
                ", sourcekey=" + Arrays.toString(sourcekeys) +
                '}';
    }
}
