/*
 * Copyright 2016-2017 Leon Chen
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

package com.moilioncircle.redis.replicator.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.io.RedisOutputStream;
import com.moilioncircle.redis.replicator.net.RedisSocketFactory;
import com.moilioncircle.redis.replicator.util.XFuture;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
public class RESP2AsyncClient implements Closeable {
    
    private static final Logger logger = LoggerFactory.getLogger(RESP2AsyncClient.class);
    
    private final RESP2 resp2;
    private final String host;
    private final int port;
    
    private final Socket socket;
    private final Reader reader;
    private final RedisInputStream is;
    private final RedisOutputStream os;
    private final Configuration configuration;
    private final Queue<XFuture<RESP2.Node>> queue = new LinkedBlockingQueue<>();
    
    public RESP2AsyncClient(String host, int port, Configuration configuration) throws IOException {
        this.host = host;
        this.port = port;
        this.configuration = configuration;
        RedisSocketFactory socketFactory = new RedisSocketFactory(configuration);
        this.socket = socketFactory.createSocket(host, port, configuration.getConnectionTimeout());
        this.os = new RedisOutputStream(socket.getOutputStream());
        this.is = new RedisInputStream(socket.getInputStream(), configuration.getBufferSize());
        this.resp2 = new RESP2(is, os);
        
        this.reader = new Reader();
        this.reader.open();
        
        final String user = configuration.getAuthUser();
        final String pswd = configuration.getAuthPassword();
        
        if (pswd != null) {
            RESP2.Node auth = null;
            try {
                if (user != null) {
                    auth = invoke("auth", user, pswd).get();
                } else {
                    auth = invoke("auth", pswd).get();
                }
                if (auth.type == RESP2.Type.ERROR) {
                    throw new AssertionError(auth.getError());
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new IOException(e);
            }
        } else {
            try {
                RESP2.Node ping = invoke("ping").get();
                if (ping.type == RESP2.Type.ERROR) {
                    throw new IOException(ping.getError());
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new IOException(e);
            }
        }
        logger.info("connected to redis-server[{}:{}]", host, port);
    }
    
    public XFuture<RESP2.Node> invoke(byte[]... command) {
        XFuture<RESP2.Node> future = new XFuture<>();
        future.setCookie("$command", command);
        queue.offer(future);
        return future;
    }
    
    public XFuture<RESP2.Node> invoke(String... command) {
        XFuture<RESP2.Node> future = new XFuture<>();
        byte[][] bc = Arrays.stream(command).map(e -> e.getBytes()).toArray(byte[][]::new);
        future.setCookie("$command", bc);
        queue.offer(future);
        return future;
    }
    
    public static RESP2AsyncClient valueOf(RESP2AsyncClient prev, int db, IOException reason, int attempts) throws IOException {
        if (reason != null) {
            logger.error("[redis-replicator] socket error. redis-server[{}:{}]", prev.host, prev.port, reason);
        }
        prev.close();
        if (reason != null) {
            logger.info("reconnecting to redis-server[{}:{}]. retry times:{}", prev.host, prev.port, attempts);
        }
        RESP2AsyncClient next = new RESP2AsyncClient(prev.host, prev.port, prev.configuration);
        try {
            RESP2.Node select = next.invoke("select", String.valueOf(db)).get();
            if (select.type == RESP2.Type.ERROR) {
                throw new IOException(select.getError());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new IOException(e);
        }
        return next;
    }
    
    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            // NOP
        }
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            // NOP
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // NOP
        }
        logger.info("socket closed. redis-server[{}:{}]", host, port);
    }
    
    private class Reader implements Runnable, Closeable {
    
        private XFuture<Void> close = new XFuture<>();
        private AtomicBoolean closed = new AtomicBoolean(false);
        private ThreadFactory tf = Executors.defaultThreadFactory();
        
        public void open() {
            tf.newThread(this).start();
        }
        
        @Override
        public void run() {
            while (!closed.get()) {
                while (!queue.isEmpty()) {
                    XFuture<RESP2.Node> future = queue.peek();
                    try {
                        RESP2.Node node = resp2.parse();
                        future.complete(node);
                    } catch (IOException e) {
                        future.completeExceptionally(e);
                    } finally {
                        queue.poll();
                    }
                }
            }
            close.complete(null);
        }
        
        @Override
        public void close() throws IOException {
            if (closed.compareAndSet(false, true)) {
                try {
                    close.get();
                } catch (InterruptedException e) {
                    throw new IOException(e);
                } catch (ExecutionException e) {
                    throw new IOException(e);
                }
            }
        }
    }
}
