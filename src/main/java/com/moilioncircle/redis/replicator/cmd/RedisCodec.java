package com.moilioncircle.redis.replicator.cmd;

import com.moilioncircle.redis.replicator.util.ByteBuilder;

import static java.lang.Integer.parseInt;

/**
 * @author Baoyi Chen
 */
public class RedisCodec {

    private static final char[] NUMERALS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

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
            } else if (b > 32 && b < 127) {
                s.put((byte) b); // printable
            } else {
                // encode
                s.put((byte) '\\');
                s.put((byte) 'x');
                int ma = b / 16;
                int mi = b % 16;
                s.put((byte) NUMERALS[ma]);
                s.put((byte) NUMERALS[mi]);
            }
        }
        return s.array();
    }

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
                            case 'f':
                                s.put((byte) '\f');
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
                                s.put(bytes[i]);
                                break;
                        }
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
