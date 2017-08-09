package com.moilioncircle.redis.replicator.io;

import com.moilioncircle.redis.replicator.util.ByteArray;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Leon Chen
 * @since 1.0.0
 */
public class RateLimitInputStreamTest {
    @Test
    public void read() throws Exception {
        String str = "sfajfklfjkljflsfjs;djfsldfjsklfjsdfjkdsfjksdjfdskfjdfsdfsfff";
        RateLimitInputStream in = new RateLimitInputStream(new ByteArrayInputStream(new ByteArray(str.getBytes())), 10);
        byte[] b = new byte[50];
        long st = System.currentTimeMillis();
        in.read(b);
        assertEquals(10, in.available());
        long ed = System.currentTimeMillis();
        System.out.println(ed - st);
        assertEquals(true, (ed - st) > 3900 && (ed - st) < 4100);
        in.close();
    }

    @Test
    public void read1() throws Exception {
        String str = "sfajfklfjkljflsfjs;djfsldfjsklfjsdfjkdsfjksdjfdskfjdfsdfsfff";
        RateLimitInputStream in = new RateLimitInputStream(new ByteArrayInputStream(new ByteArray(str.getBytes())), 10);
        long st = System.currentTimeMillis();
        in.read();
        assertEquals(59, in.available());
        long ed = System.currentTimeMillis();
        System.out.println(ed - st);
        assertEquals(true, (ed - st) >= 0 && (ed - st) <= 1);
        in.close();
    }

    @Test
    public void read2() throws Exception {
        String str = "sfajfklfjkljflsfjs;djfsldfjsklfjsdfjkdsfjksdjfdskfjdfsdfsfff";
        RateLimitInputStream in = new RateLimitInputStream(new ByteArrayInputStream(new ByteArray(str.getBytes())), 10);
        long st = System.currentTimeMillis();
        in.skip(60);
        assertEquals(0, in.available());
        long ed = System.currentTimeMillis();
        System.out.println(ed - st);
        assertEquals(true, (ed - st) > 4900 && (ed - st) < 5100);
        in.close();
    }

}