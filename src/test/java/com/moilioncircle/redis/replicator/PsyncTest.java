package com.moilioncircle.redis.replicator;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.rdb.AuxFieldListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.AuxField;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Baoyi Chen on 2017/2/9.
 */
public class PsyncTest {

    @Test
    public void psync() throws IOException {

        final Configuration configuration = Configuration.defaultSetting().
                setAuthPassword("test").
                setConnectionTimeout(1000).
                setReadTimeout(1000).
                setBufferSize(64).
                setAsyncCachedBytes(0).
                setHeartBeatPeriod(200).
                setReceiveBufferSize(0).
                setSendBufferSize(0).
                setDiscardRdbEvent(true).
                setRetryTimeInterval(1000);
        System.out.println(configuration);
        Replicator replicator = new TestRedisSocketReplicator("127.0.0.1", 6380, configuration);
        replicator.removeCommandParser(CommandName.name("PING"));
        final AtomicBoolean flag = new AtomicBoolean(false);
        final Set<AuxField> set = new LinkedHashSet<>();
        replicator.addAuxFieldListener(new AuxFieldListener() {
            @Override
            public void handle(Replicator replicator, AuxField auxField) {
                set.add(auxField);
            }
        });
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {

            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                if (flag.compareAndSet(false, true)) {
                    Thread thread = new Thread(new JRun());
                    thread.setDaemon(true);
                    thread.start();
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
                        System.out.println("close for psync");
                        //close current process port;
                        //that will auto trigger psync command
                        close(replicator);
                    }
                    if (acc.get() == 980) {
                        configuration.setVerbose(true);
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
                for (AuxField auxField : set) {
                    System.out.println(auxField.getAuxKey() + "=" + auxField.getAuxValue());
                }
            }
        });
        replicator.open();
    }

    private static void close(Replicator replicator) {
        try {
            ((TestRedisSocketReplicator) replicator).getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ((TestRedisSocketReplicator) replicator).getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ((TestRedisSocketReplicator) replicator).getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class JRun implements Runnable {

        @Override
        public void run() {
            System.out.println("start jedis insert");
            Jedis jedis = new Jedis("127.0.0.1", 6380);
            jedis.auth("test");
            for (int i = 0; i < 1000; i++) {
                jedis.set("psync " + i, "psync" + i);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            jedis.close();
            System.out.println("stop jedis insert");
        }
    }

    private static class TestRedisSocketReplicator extends RedisSocketReplicator {

        public TestRedisSocketReplicator(String host, int port, Configuration configuration) {
            super(host, port, configuration);
        }

        public Socket getSocket(){
            return super.socket;
        }

        public InputStream getInputStream(){
            return super.inputStream;
        }

        public OutputStream getOutputStream(){
            return super.outputStream;
        }
    }

}
