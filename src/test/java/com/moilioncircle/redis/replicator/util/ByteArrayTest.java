package com.moilioncircle.redis.replicator.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/10/17.
 */
public class ByteArrayTest {
    @Test
    public void test() throws Exception {

        String str = "sdajkl;jlqwjqejqweq89080c中jlxczksaouwq9823djadj";
        ByteArray bytes = new ByteArray(str.getBytes().length, 10);
        byte[] b1 = str.getBytes();
        int i = 0;
        for (byte b : b1) {
            bytes.set(i, b);
            assertEquals(b, bytes.get(i));
            i++;
        }
        ByteArray bytes1 = new ByteArray(str.getBytes().length - 10, 10);
        ByteArray.arraycopy(bytes, 10, bytes1, 0, bytes.length - 10);
        assertEquals(str.substring(10), new String(bytes1.first()));

        str = "sdajk";
        ByteArray bytes2 = new ByteArray(str.getBytes().length, 10);
        b1 = str.getBytes();
        i = 0;
        for (byte b : b1) {
            bytes2.set(i, b);
            assertEquals(b, bytes2.get(i));
            i++;
        }
        assertEquals(new String(bytes2.first()), "sdajk");

        ByteArray bytes3 = new ByteArray(bytes2.length() - 1, 10);
        ByteArray.arraycopy(bytes2, 1, bytes3, 0, bytes2.length() - 1);
        assertEquals(str.substring(1), new String(bytes3.first()));
    }

}