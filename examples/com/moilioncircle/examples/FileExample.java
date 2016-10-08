package com.moilioncircle.examples;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

import java.io.File;
import java.io.IOException;

/**
 * Created by leon on 10/8/16.
 */
public class FileExample {
    public static void main(String[] args) throws IOException {
        final RedisReplicator replicator = new RedisReplicator(
                new File("dumpV7.rdb"),
                Configuration.defaultSetting());

        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
            }
        });

        replicator.open();

        System.in.read();
    }
}
