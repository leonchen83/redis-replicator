/*
 * Copyright 2016-2018 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.online;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.slf4j.Logger;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisSocketReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.event.PreRdbSyncEvent;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.util.Strings;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class PsyncTest {

    @Test
    public void psync() throws IOException {
    
        try (Jedis jedis = new Jedis("127.0.0.1", 6380)) {
            jedis.auth("test");
            Pipeline pipeline = jedis.pipelined();
            for (int i = 0; i < 100; i++) {
                pipeline.set("pre-psync " + i, "pre-psync" + i);
            }
            pipeline.sync();
        }

        final Configuration configuration = Configuration.defaultSetting().
                setAuthPassword("test").
                setConnectionTimeout(3000).
                setReadTimeout(3000).
                setBufferSize(64).
                setAsyncCachedBytes(0).
                setHeartbeatPeriod(200).
                setReceiveBufferSize(0).
                setSendBufferSize(0).
                setRetryTimeInterval(1000).
                setUseDefaultExceptionListener(false);
        @SuppressWarnings("resource")
        TestRedisSocketReplicator r = new TestRedisSocketReplicator("127.0.0.1", 6380, configuration);
        final AtomicBoolean flag = new AtomicBoolean(false);
        final AtomicInteger acc = new AtomicInteger();
    
        final AtomicBoolean flag1 = new AtomicBoolean(false);
        final AtomicInteger acc1 = new AtomicInteger();
        r.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PreRdbSyncEvent) {
                    acc1.incrementAndGet();
                }
                
                if (event instanceof KeyValuePair) {
                    if (flag1.compareAndSet(false, true)) {
                        // will trigger full sync at this time
                        close(replicator);
                    }
                }
                
                if (event instanceof PostRdbSyncEvent) {
                    if (flag.compareAndSet(false, true)) {
                        // will trigger full sync at this time
                        close(replicator);
                        Thread thread = new Thread(new JRun());
                        thread.setDaemon(true);
                        thread.start();
                        replicator.removeCommandParser(CommandName.name("PING"));
                    }
                }
                if (event instanceof SetCommand && Strings.toString(((SetCommand) event).getKey()).startsWith("psync")) {
                    acc.incrementAndGet();
                    if (acc.get() == 500) {
                        //close current process port;
                        //that will auto trigger psync command
                        r.getLogger().info("id:{}, offset:{}", configuration.getReplId(), configuration.getReplOffset());
                        close(replicator);
                    }

                    if (acc.get() == 1010) {
                        //close current process port;
                        //that will auto trigger psync command
                        r.getLogger().info("id:{}, offset:{}", configuration.getReplId(), configuration.getReplOffset());
                        close(replicator);
                    }
                    if (acc.get() == 1480) {
                        configuration.setVerbose(true);
                    }
                    if (acc.get() == 1500) {
                        try {
                            replicator.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        });
        r.open();
        assertEquals(3, acc1.get());
        assertEquals(1500, acc.get());
    }

    private static void close(Replicator replicator) {
        try {
            ((TestRedisSocketReplicator) replicator).getOutputStream().close();
        } catch (IOException e) {
        }
        try {
            ((TestRedisSocketReplicator) replicator).getInputStream().close();
        } catch (IOException e) {
        }
        try {
            ((TestRedisSocketReplicator) replicator).getSocket().close();
        } catch (IOException e) {
        }
    }

    private static class JRun implements Runnable {

        @Override
        public void run() {
            try (Jedis jedis = new Jedis("127.0.0.1", 6380)) {
                jedis.auth("test");
                Pipeline pipeline = jedis.pipelined();
                for (int i = 0; i < 1500; i++) {
                    pipeline.set("psync " + i, "psync" + i);
                }
                pipeline.sync();
            }
        }
    }

    private static class TestRedisSocketReplicator extends RedisSocketReplicator {

        public TestRedisSocketReplicator(String host, int port, Configuration configuration) {
            super(host, port, configuration);
        }
        
        public Logger getLogger() {
            return TestRedisSocketReplicator.logger;
        }

        public Socket getSocket() {
            return super.socket;
        }

        public InputStream getInputStream() {
            return super.inputStream;
        }

        public OutputStream getOutputStream() {
            return super.outputStream;
        }
    }

}
