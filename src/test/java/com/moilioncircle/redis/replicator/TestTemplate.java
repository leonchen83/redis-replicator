package com.moilioncircle.redis.replicator;

import junit.framework.TestCase;

import java.io.IOException;

/**
 * Created by leon on 8/28/16.
 */
public abstract class TestTemplate extends TestCase {
    public void testSocket(final String host, final int port, final Configuration configuration, final long sleep) throws IOException {
        final RedisReplicator replicator = new RedisReplicator(host,
                port,
                configuration);
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(sleep);
                    replicator.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        test(replicator);
        replicator.open();
    }

    protected abstract void test(RedisReplicator replicator);
}
