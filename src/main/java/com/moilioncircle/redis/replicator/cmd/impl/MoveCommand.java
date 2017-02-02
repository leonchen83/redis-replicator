package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class MoveCommand implements Command {
    private final String key;
    private final int db;

    public String getKey() {
        return key;
    }

    public int getDb() {
        return db;
    }

    public MoveCommand(String key, int db) {
        this.key = key;
        this.db = db;
    }

    @Override
    public String toString() {
        return "MoveCommand{" +
                "key='" + key + '\'' +
                ", db=" + db +
                '}';
    }
}
