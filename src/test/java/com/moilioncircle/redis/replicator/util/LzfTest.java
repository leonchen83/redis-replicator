package com.moilioncircle.redis.replicator.util;

import org.junit.Test;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 * Created by Baoyi Chen on 2017/2/9.
 */
public class LzfTest {
    @Test
    public void decode() throws Exception {
        {
            String str = "abcdsklafjslfjfd;sfdklafjlsafjslfjasl;fkjdsalfjasfjlas;dkfjalsvlasfkal;sj";
            byte[] out = compress(str.getBytes());
            ByteArray in = Lzf.decode(new ByteArray(out), str.getBytes().length);
            assertEquals(new String(in.first()),str);
        }

        {
            InputStream in = LzfTest.class.getClassLoader().getResourceAsStream("low-comp-120k.txt");
            byte[] bytes = new byte[121444];
            int len = in.read(bytes);
            byte[] out = compress(bytes);
            ByteArray bin = Lzf.decode(new ByteArray(out), len);
            byte[] oin = bin.first();
            for(int i=0;i<len;i++){
                assertEquals(oin[i],bytes[i]);
            }
        }

        {
            InputStream in = LzfTest.class.getClassLoader().getResourceAsStream("appendonly6.aof");
            byte[] bytes = new byte[3949];
            int len = in.read(bytes);
            byte[] out = compress(bytes);
            ByteArray bin = Lzf.decode(new ByteArray(out), len);
            byte[] oin = bin.first();
            for(int i=0;i<len;i++){
                assertEquals(oin[i],bytes[i]);
            }
        }

    }

    private byte[] compress(byte[] in) {
        CompressLZF c = new CompressLZF();
        byte[] compressed = new byte[in.length];
        int idx = c.compress(in, in.length, compressed, 0);
        byte[] out = new byte[idx];
        System.arraycopy(compressed, 0, out, 0, out.length);
        return out;
    }

}