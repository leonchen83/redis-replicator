package com.moilioncircle.redis.replicator.rdb.dump;

import com.moilioncircle.redis.replicator.util.ByteBuilder;

import static java.lang.Integer.parseInt;

public class ByteParser {
    public static byte[] toBytes(String str) {
        char[] ary = str.toCharArray();
        ByteBuilder s = ByteBuilder.allocate(ary.length);
        for (int i = 0; i < ary.length; i++) {
            switch (ary[i]) {
                case '\\':
                    i++;
                    if (i < ary.length) {
                        switch (ary[i]) {
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
                                if (i + 2 >= ary.length) {
                                    s.put((byte) '\\');
                                    s.put((byte) 'x');
                                } else {
                                    char high = ary[++i];
                                    char low = ary[++i];
                                    try {
                                        s.put((byte) parseInt(new String(new char[]{high, low}), 16));
                                    } catch (Exception e) {
                                        s.put((byte) '\\');
                                        s.put((byte) 'x');
                                        s.put((byte) high);
                                        s.put((byte) low);
                                    }
                                }
                                break;
                            default:
                                s.put((byte) ary[i]);
                                break;
                        }
                    }
                    break;
                default:
                    s.put((byte) ary[i]);
                    break;
            }
        }
        return s.array();
    }
}
