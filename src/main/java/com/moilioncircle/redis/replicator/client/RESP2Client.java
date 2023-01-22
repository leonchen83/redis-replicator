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

import static java.util.Arrays.stream;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.io.RedisOutputStream;
import com.moilioncircle.redis.replicator.net.RedisSocketFactory;
import com.moilioncircle.redis.replicator.util.Strings;
import com.moilioncircle.redis.replicator.util.Tuples;
import com.moilioncircle.redis.replicator.util.type.Tuple2;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
public class RESP2Client implements Closeable {
    
    public static interface Function<T, R> {
        R apply(T t) throws IOException;
    }
    
    public static interface NodeConsumer {
        void accept(RESP2.Node node) throws IOException;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(RESP2Client.class);
    
    private final RESP2 resp2;
    private final String host;
    private final int port;
    
    private final Socket socket;
    private final RedisInputStream is;
    private final RedisOutputStream os;
    private final Configuration configuration;
    
    public RESP2Client(String host, int port, Configuration configuration) throws IOException {
        this.host = host;
        this.port = port;
        this.configuration = configuration;
        RedisSocketFactory socketFactory = new RedisSocketFactory(configuration);
        this.socket = socketFactory.createSocket(host, port, configuration.getConnectionTimeout());
        this.os = new RedisOutputStream(socket.getOutputStream());
        this.is = new RedisInputStream(socket.getInputStream(), configuration.getBufferSize());
        this.resp2 = new RESP2(is, os);
        
        final String user = configuration.getAuthUser();
        final String pswd = configuration.getAuthPassword();
        
        if (pswd != null) {
            RESP2.Node auth = null;
            if (user != null) {
                auth = newCommand().invoke("auth", user, pswd);
            } else {
                auth = newCommand().invoke("auth", pswd);
            }
            if (auth.type == RESP2.Type.ERROR) {
                String reply = auth.getError();
                String mask = "#" + Strings.mask(pswd);
                if (reply.contains("no password") || reply.contains("without any password")) {
                    if (user == null) {
                        logger.warn("[AUTH {}] failed. {}", mask, reply);
                    } else {
                        logger.warn("[AUTH {} {}] failed. {}", user, mask, reply);
                    }
                } else if (user == null) {
                    throw new AssertionError("[AUTH " + mask + "] failed. " + reply);
                } else {
                    throw new AssertionError("[AUTH " + user + " " + mask + "] failed. " + reply);
                }
            }
        } else {
            RESP2.Node ping = newCommand().invoke("ping");
            if (ping.type == RESP2.Type.ERROR) {
                String reply = ping.getError();
                if (reply.contains("NOAUTH")) throw new AssertionError(reply);
                if (reply.contains("NOPERM")) throw new AssertionError(reply);
                if (reply.contains("operation not permitted")) throw new AssertionError("-NOAUTH Authentication required.");
                throw new IOException(reply);
            }
        }
        logger.info("connected to redis-server[{}:{}]", host, port);
    }
    
    public Response newCommand() {
        return new Response(this.resp2);
    }
    
    @Override
    public void close() throws IOException {
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
    
    public static RESP2Client valueOf(RESP2Client prev, int db, IOException reason, int attempts) throws IOException {
        if (reason != null) {
            logger.error("[redis-replicator] socket error. redis-server[{}:{}]", prev.host, prev.port, reason);
        }
        prev.close();
        if (reason != null) {
            logger.info("reconnecting to redis-server[{}:{}]. retry times:{}", prev.host, prev.port, attempts);
        }
        RESP2Client next = new RESP2Client(prev.host, prev.port, prev.configuration);
        RESP2.Node select = next.newCommand().invoke("select", String.valueOf(db));
        if (select.type == RESP2.Type.ERROR) {
            throw new IOException(select.getError());
        }
        return next;
    }
    
    public static class Response {
        private RESP2 resp2;
        private Queue<Tuple2<NodeConsumer, byte[][]>> responses;
        
        private Response(RESP2 resp2) {
            this.resp2 = resp2;
            this.responses = new LinkedList<>();
        }
    
        public Queue<Tuple2<NodeConsumer, byte[][]>> getResponses() {
            return new LinkedList<>(this.responses);
        }
    
        public void get() throws IOException {
            while (!this.responses.isEmpty()) {
                NodeConsumer consumer = this.responses.peek().getV1();
                consumer.accept(this.resp2.parse());
                this.responses.poll();
            }
        }
    
        public RESP2.Node invoke(byte[]... command) throws IOException {
            this.resp2.emit(command);
            return this.resp2.parse();
        }
        
        public RESP2.Node invoke(String... command) throws IOException {
            return invoke(stream(command).map(e -> e.getBytes()).toArray(byte[][]::new));
        }
        
        public Response post(NodeConsumer handler, byte[]... command) throws IOException {
            this.resp2.emit(command);
            this.responses.offer(Tuples.of(handler, command));
            return this;
        }
        
        public Response post(NodeConsumer handler, String... command) throws IOException {
            return post(handler, stream(command).map(e -> e.getBytes()).toArray(byte[][]::new));
        }
    }
}
