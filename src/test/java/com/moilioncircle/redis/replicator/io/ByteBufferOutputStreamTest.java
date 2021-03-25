package com.moilioncircle.redis.replicator.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * @author Leon Chen
 * @since 3.5.5
 */
public class ByteBufferOutputStreamTest {
    
    @Test
    public void test() throws IOException {
        try (ByteBufferOutputStream out = new ByteBufferOutputStream(32)) {
            byte[] hello = "hello".getBytes();
            out.writeBytes(hello);
            ByteBuffer buf = out.toByteBuffer();
            assertEquals(32, buf.capacity());
            assertEquals(5, buf.limit());
            assertEquals(0, buf.position());
            byte[] r = new byte[hello.length];
            int i = 0;
            while (buf.hasRemaining()) {
                r[i++] = buf.get();
            }
            
            assertArrayEquals(hello, r);
        }
        
    }
}