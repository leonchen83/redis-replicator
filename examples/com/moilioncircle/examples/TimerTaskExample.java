package com.moilioncircle.examples;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by leon on 10/8/16.
 */
public class TimerTaskExample {
    public static void main(String[] args) throws IOException {
        final Timer timer = new Timer("sync");
        timer.schedule(new Task(), 30000, 30000);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                timer.cancel();
            }
        });
        System.in.read();
    }

    private static class Task extends TimerTask {
        @Override
        public void run() {
            RedisReplicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
            replicator.addRdbListener(new RdbListener() {
                @Override
                public void preFullSync(Replicator replicator) {
                    System.out.println("data sync started");
                }

                @Override
                public void handle(Replicator replicator, KeyValuePair<?> kv) {
                    //shard kv.getKey to different thread so that speed up save process.
                    save(kv);
                }

                @Override
                public void postFullSync(Replicator replicator, long checksum) {
                    try {
                        replicator.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("data sync done");
                }
            });
        }
    }

    private static void save(KeyValuePair<?> kv) {
        //save kv to mysql or to anywhere.
    }
}
