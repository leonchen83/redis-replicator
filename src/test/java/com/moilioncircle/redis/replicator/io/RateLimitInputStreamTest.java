package com.moilioncircle.redis.replicator.io;

import com.moilioncircle.redis.replicator.util.ByteArray;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Leon Chen
 * @since 1.0.0
 */
public class RateLimitInputStreamTest {
    @Test
    public void read() throws Exception {
        byte[] bytes = new byte[9000];
        Arrays.fill(bytes, (byte)100);
        RateLimitInputStream in = new RateLimitInputStream(new ByteArrayInputStream(new ByteArray(bytes)), 2000);
        byte[] b = new byte[bytes.length - 1000];
        long st = System.currentTimeMillis();
        assertEquals(8000, in.read(b));
        assertEquals(1000, in.available());
        long ed = System.currentTimeMillis();
        System.out.println(ed - st);
        assertEquals(true, (ed - st) > 2900 && (ed - st) < 3100);
        in.close();
    }

    @Test
    public void read1() throws Exception {
        byte[] bytes = new byte[9000];
        Arrays.fill(bytes, (byte)100);
        RateLimitInputStream in = new RateLimitInputStream(new ByteArrayInputStream(new ByteArray(bytes)), 10);
        long st = System.currentTimeMillis();
        in.read();
        assertEquals(8999, in.available());
        long ed = System.currentTimeMillis();
        System.out.println(ed - st);
        assertEquals(true, (ed - st) >= 0 && (ed - st) <= 1);
        in.close();
    }

    @Test
    public void read2() throws Exception {
        byte[] bytes = new byte[9000];
        Arrays.fill(bytes, (byte)100);
        RateLimitInputStream in = new RateLimitInputStream(new ByteArrayInputStream(new ByteArray(bytes)), 10);
        long st = System.currentTimeMillis();
        assertEquals(9000, in.skip(bytes.length));
        assertEquals(0, in.skip(0));
        assertEquals(0, in.available());
        long ed = System.currentTimeMillis();
        System.out.println(ed - st);
        assertEquals(true, (ed - st) > 7900 && (ed - st) < 8100);
        in.close();
    }

}