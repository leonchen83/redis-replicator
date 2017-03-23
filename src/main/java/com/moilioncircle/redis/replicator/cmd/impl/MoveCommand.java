package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class MoveCommand implements Command {
    private String key;
    private int db;

    public MoveCommand() {
    }

    public MoveCommand(String key, int db) {
        this.key = key;
        this.db = db;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getDb() {
        return db;
    }

    public void setDb(int db) {
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
