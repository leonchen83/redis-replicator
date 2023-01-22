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

import static com.moilioncircle.redis.replicator.Constants.COLON;
import static com.moilioncircle.redis.replicator.Constants.DOLLAR;
import static com.moilioncircle.redis.replicator.Constants.MINUS;
import static com.moilioncircle.redis.replicator.Constants.PLUS;
import static com.moilioncircle.redis.replicator.Constants.STAR;

import java.io.IOException;

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.io.RedisOutputStream;
import com.moilioncircle.redis.replicator.util.ByteArray;
import com.moilioncircle.redis.replicator.util.ByteBuilder;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
public class RESP2 {
    
    private final RedisInputStream in;
    private final RedisOutputStream out;
    
    RESP2(RedisInputStream in, RedisOutputStream out) {
        this.in = in;
        this.out = out;
    }
    
    void emit(byte[]... command) throws IOException {
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
    
    Node parse() throws IOException {
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
    
    public static enum Type {
        ARRAY, NUMBER, STRING, ERROR, NULL;
    }
    
    public static class Node {
        public final RESP2.Type type;
        public final Object value;
        
        private Node(RESP2.Type type, Object value) {
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
}

