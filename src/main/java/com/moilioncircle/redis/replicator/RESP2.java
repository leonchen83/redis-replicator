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

package com.moilioncircle.redis.replicator;

import static com.moilioncircle.redis.replicator.Constants.COLON;
import static com.moilioncircle.redis.replicator.Constants.DOLLAR;
import static com.moilioncircle.redis.replicator.Constants.MINUS;
import static com.moilioncircle.redis.replicator.Constants.PLUS;
import static com.moilioncircle.redis.replicator.Constants.STAR;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.io.RedisOutputStream;
import com.moilioncircle.redis.replicator.net.RedisSocketFactory;
import com.moilioncircle.redis.replicator.util.ByteArray;
import com.moilioncircle.redis.replicator.util.ByteBuilder;
import com.moilioncircle.redis.replicator.util.Strings;
import com.moilioncircle.redis.replicator.util.Tuples;
import com.moilioncircle.redis.replicator.util.type.Tuple2;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
public class RESP2 {
    
    private RedisInputStream in;
    private RedisOutputStream out;
    
    public RESP2(RedisInputStream in, RedisOutputStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void emit(byte[]... command) throws IOException {
        out.write(STAR);
        out.write(String.valueOf(command.length).getBytes());
        out.writeCrLf();
        for (final byte[] element : command) {
            out.write(DOLLAR);
            out.write(String.valueOf(element.length).getBytes());
            out.writeCrLf();
            out.write(element);
            out.writeCrLf();
        }
        out.flush();
    }
    
    private Node parse() throws IOException {
        while (true) {
            int c = in.read();
            switch (c) {
                case DOLLAR:
                    // RESP Bulk Strings
                    ByteBuilder builder = ByteBuilder.allocate(32);
                    while (true) {
                        while ((c = in.read()) != '\r') {
                            builder.put((byte) c);
                        }
                        if ((c = in.read()) == '\n') {
                            break;
                        } else {
                            builder.put((byte) c);
                        }
                    }
                    long len = Long.parseLong(builder.toString());
                    if (len == -1) return new Node(RESP2.Type.NULL, null);
                    Node r = new Node(RESP2.Type.STRING, in.readBytes(len));
                    
                    if ((c = in.read()) != '\r') throw new AssertionError("expect '\\r' but :" + (char) c);
                    if ((c = in.read()) != '\n') throw new AssertionError("expect '\\n' but :" + (char) c);
                    return r;
                case COLON:
                    // RESP Integers
                    builder = ByteBuilder.allocate(128);
                    while (true) {
                        while ((c = in.read()) != '\r') {
                            builder.put((byte) c);
                        }
                        if ((c = in.read()) == '\n') {
                            break;
                        } else {
                            builder.put((byte) c);
                        }
                    }
                    // As integer
                    return new Node(RESP2.Type.NUMBER, Long.parseLong(builder.toString()));
                case STAR:
                    // RESP Arrays
                    builder = ByteBuilder.allocate(128);
                    while (true) {
                        while ((c = in.read()) != '\r') {
                            builder.put((byte) c);
                        }
                        if ((c = in.read()) == '\n') {
                            break;
                        } else {
                            builder.put((byte) c);
                        }
                    }
                    len = Long.parseLong(builder.toString());
                    if (len == -1) return new Node(RESP2.Type.NULL, null);
                    Node[] ary = new Node[(int) len];
                    for (int i = 0; i < len; i++) {
                        Node obj = parse();
                        ary[i] = obj;
                    }
                    return new Node(RESP2.Type.ARRAY, ary);
                case PLUS:
                    // RESP Simple Strings
                    builder = ByteBuilder.allocate(128);
                    while (true) {
                        while ((c = in.read()) != '\r') {
                            builder.put((byte) c);
                        }
                        if ((c = in.read()) == '\n') {
                            return new Node(RESP2.Type.STRING, new ByteArray(builder.array()));
                        } else {
                            builder.put((byte) c);
                        }
                    }
                case MINUS:
                    // RESP Errors
                    builder = ByteBuilder.allocate(32);
                    while (true) {
                        while ((c = in.read()) != '\r') {
                            builder.put((byte) c);
                        }
                        if ((c = in.read()) == '\n') {
                            return new Node(RESP2.Type.ERROR, new ByteArray(builder.array()));
                        } else {
                            builder.put((byte) c);
                        }
                    }
                default:
                    throw new AssertionError("expect [$,:,*,+,-] but: " + (char) c);
                
            }
        }
    }
    
    public static class Node {
        public RESP2.Type type;
        public Object value;
        
        public Node(RESP2.Type type, Object value) {
            this.type = type;
            this.value = value;
        }
    
        public Long getNumber() {
            return type == Type.NUMBER ? (Long) value : null;
        }
    
        public Node[] getArray() {
            return type == Type.ARRAY ? (Node[]) value : null;
        }
    
        public ByteArray getBytes() {
            return type == Type.STRING ? (ByteArray) value : null;
        }
    
        public String getError() {
            return type == Type.ERROR ? Strings.toString(((ByteArray) value).first()) : null;
        }
    
        public String getString() {
            return type == Type.STRING ? Strings.toString(((ByteArray) value).first()) : null;
        }
    }
    
    public static enum Type {
        ARRAY, NUMBER, STRING, ERROR, NULL;
    }
    
    public static class Client implements Closeable {
        
        private Socket socket;
        private RedisInputStream is;
        private RedisOutputStream os;
        
        private final RESP2 resp2;
        private final String host;
        private final int port;
        private final Configuration configuration;
        
        public Client(String host, int port, Configuration configuration) throws IOException {
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
                if (auth.type == Type.ERROR) {
                    throw new AssertionError(auth.getError());
                }
            } else {
                RESP2.Node ping = newCommand().invoke("ping");
                if (ping.type == Type.ERROR) {
                    throw new IOException(ping.getError());
                }
            }
        }
        
        public static Client valueOf(Client prev, int db) throws IOException {
            prev.close();
            Client next = new Client(prev.host, prev.port, prev.configuration);
            RESP2.Node select = next.newCommand().invoke("select", String.valueOf(db));
            if (select.type == Type.ERROR) {
                throw new IOException(select.getError());
            }
            return next;
        }
        
        public RESP2.Response newCommand() {
            return new RESP2.Response(resp2);
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
        }
    }
    
    public static class Response {
        private RESP2 resp2;
        private Queue<Tuple2<NodeConsumer, byte[][]>> responses;
        
        public Response(RESP2 resp2) {
            this.resp2 = resp2;
            this.responses = new LinkedList<>();
        }
        
        public RESP2.Node invoke(byte[]... command) throws IOException {
            this.resp2.emit(command);
            return this.resp2.parse();
        }
        
        public RESP2.Node invoke(String... command) throws IOException {
            return invoke(Arrays.stream(command).map(e -> e.getBytes()).toArray(byte[][]::new));
        }
        
        public RESP2.Response post(NodeConsumer handler, byte[]... command) throws IOException {
            this.resp2.emit(command);
            this.responses.offer(Tuples.of(handler, command));
            return this;
        }
        
        public RESP2.Response post(NodeConsumer handler, String... command) throws IOException {
            byte[][] bc = Arrays.stream(command).map(e -> e.getBytes()).toArray(byte[][]::new);
            this.resp2.emit(bc);
            this.responses.offer(Tuples.of(handler, bc));
            return this;
        }
        
        public void get() throws IOException {
            while (!responses.isEmpty()) {
                NodeConsumer consumer = responses.peek().getV1();
                consumer.accept(resp2.parse());
                responses.poll();
            }
        }
        
        public Queue<Tuple2<NodeConsumer, byte[][]>> responses() {
            return new LinkedList<>(this.responses);
        }
    }
    
    public static interface Function<T, R> {
        R apply(T t) throws IOException;
    }
    
    public static interface NodeConsumer {
        void accept(Node node) throws IOException;
    }
}

