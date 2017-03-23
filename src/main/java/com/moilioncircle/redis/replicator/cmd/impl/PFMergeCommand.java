package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class PFMergeCommand implements Command {
    private String destkey;
    private String sourcekeys[];

    public PFMergeCommand() {
    }

    public PFMergeCommand(String destkey, String... sourcekeys) {
        this.destkey = destkey;
        this.sourcekeys = sourcekeys;
    }

    public String getDestkey() {
        return destkey;
    }

    public void setDestkey(String destkey) {
        this.destkey = destkey;
    }

    public String[] getSourcekeys() {
        return sourcekeys;
    }

    public void setSourcekeys(String[] sourcekeys) {
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
