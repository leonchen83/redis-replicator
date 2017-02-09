package com.moilioncircle.redis.replicator;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Baoyi Chen on 2017/2/9.
 */
public class PsyncTest {

    //@Test
    public void psync() throws IOException {

        Replicator replicator = new RedisReplicator("127.0.0.1", 6380, Configuration.defaultSetting().setAuthPassword("test").setConnectionTimeout(2000));
        final AtomicBoolean flag = new AtomicBoolean(false);
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {

            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                if (!flag.get()) {
                    Thread thread = new Thread(new JRun());
                    thread.setDaemon(true);
                    thread.start();
                    flag.compareAndSet(false, true);
                }
            }
        });
        final AtomicInteger acc = new AtomicInteger();
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof SetCommand && ((SetCommand) command).getKey().startsWith("psync")) {
                    SetCommand setCommand = (SetCommand) command;
                    int num = Integer.parseInt(setCommand.getKey().split(" ")[1]);
                    acc.incrementAndGet();
                    if (acc.get() == 200) {
                        //close current process port;
                        //that will auto trigger psync command
                        //TODO how to get the current process port and kill that port using java?
                    }
                    if (acc.get() == 1000) {
                        try {
                            replicator.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                assertEquals(1000, acc.get());
            }
        });
        replicator.open();
    }

    private static class JRun implements Runnable {

        @Override
        public void run() {
            Jedis jedis = new Jedis("127.0.0.1", 6380);
            jedis.auth("test");
            for (int i = 0; i < 1000; i++) {
                jedis.set("psync " + i, "psync" + i);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            jedis.close();
        }
    }

}
