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

package com.moilioncircle.redis.replicator.cmd;

import com.moilioncircle.redis.replicator.util.ByteBuilder;

import static java.lang.Integer.parseInt;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class RedisCodec {

    private static final byte[] NUMERALS = new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * @param bytes bytes
     * @return encoded bytes
     * @see <a href="https://github.com/antirez/redis/blob/4.0/src/sds.c">sds.c sdscatrepr</a> sdssplitargs
     */
    public byte[] encode(byte[] bytes) {
        ByteBuilder s = ByteBuilder.allocate(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xFF;
            if (b == '\n') {
                s.put((byte) '\\');
                s.put((byte) 'n');
            } else if (b == '\r') {
                s.put((byte) '\\');
                s.put((byte) 'r');
            } else if (b == '\t') {
                s.put((byte) '\\');
                s.put((byte) 't');
            } else if (b == '\b') {
                s.put((byte) '\\');
                s.put((byte) 'b');
            } else if (b == 7) {
                s.put((byte) '\\');
                s.put((byte) 'a');
            } else if (b == '\\') {
                s.put((byte) '\\');
                s.put((byte) '\\');
            } else if (b == '"') {
                s.put((byte) '\\');
                s.put((byte) '"');
            } else if (b > 32 && b < 127) {
                s.put((byte) b); // printable
            } else {
                // encode
                s.put((byte) '\\');
                s.put((byte) 'x');
                int ma = b >>> 4;
                int mi = b & 0xF;
                s.put(NUMERALS[ma]);
                s.put(NUMERALS[mi]);
            }
        }
        return s.array();
    }

    /**
     * @param bytes bytes
     * @return decoded bytes
     * @see <a href="https://github.com/antirez/redis/blob/4.0/src/sds.c">sds.c sdssplitargs</a> sdssplitargs
     */
    public byte[] decode(byte[] bytes) {
        ByteBuilder s = ByteBuilder.allocate(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            switch (bytes[i]) {
                case '\\':
                    i++;
                    if (i < bytes.length) {
                        switch (bytes[i]) {
                            case 'n':
                                s.put((byte) '\n');
                                break;
                            case 'r':
                                s.put((byte) '\r');
                                break;
                            case 't':
                                s.put((byte) '\t');
                                break;
                            case 'b':
                                s.put((byte) '\b');
                                break;
                            case 'a':
                                s.put((byte) 7);
                                break;
                            case 'x':
                                if (i + 2 >= bytes.length) {
                                    s.put((byte) '\\');
                                    s.put((byte) 'x');
                                } else {
                                    char hig = (char) bytes[++i];
                                    char low = (char) bytes[++i];
                                    try {
                                        s.put((byte) parseInt(new String(new char[]{hig, low}), 16));
                                    } catch (Exception e) {
                                        s.put((byte) '\\');
                                        s.put((byte) 'x');
                                        s.put((byte) hig);
                                        s.put((byte) low);
                                    }
                                }
                                break;
                            default:
                                // s.put((byte)'\\'); 
                                s.put(bytes[i]);
                                break;
                        }
                    } else {
                        // s.put((byte)'\\');
                    }
                    break;
                default:
                    s.put(bytes[i]);
                    break;
            }
        }
        return s.array();
    }
}
