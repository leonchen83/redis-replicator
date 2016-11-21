package com.moilioncircle.examples;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;

import java.io.File;
import java.io.IOException;

/**
 * Created by leon on 10/8/16.
 */
public class AofFileExample {
    public static void main(String[] args) throws IOException {
        final Replicator replicator = new RedisReplicator(
                new File("appendonly.aof"),
                Configuration.defaultSetting(), false);
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                System.out.println(command);
            }
        });

        replicator.open();
    }
}
