package com.moilioncircle.redis.replicator.util;

import static com.moilioncircle.redis.replicator.util.CRC64.crc64;
import static com.moilioncircle.redis.replicator.util.CRC64.longToByteArray;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * @since 3.5.5
 * @author Leon Chen
 */
public class CRC64Test {
    
    @Test
    public void test() {
        String test = "fjdsklafjsdklafjaklfdsjkfsdkjakjdkasjdkajdkajqwieuiqwueiqweqwieuqidasjkasjkajdkcnxzcnzxasjdksadasiuqwieuqwiejaskdajskcxnzcznczkxnasdjasjdjadqweiqwueidjdskdjaskdjskajdakjcncnzxknczxjkasdjaskdjqwieuqwiuakdakncxzkjsakdasjdiqwueijcnkdasjdiuewiqeqdijqdsahdiadiwqueqiwqidjaskdjaskdjqwjieuqfhhaksjduqiwehcaskdjasdaiqwewiqdhashdadashjqweyqwuh";
        ByteBuilder builder = ByteBuilder.allocate(20);
        builder.put(test.getBytes());
        byte[] v = builder.array();
        long v1 = crc64(v);
        long v2 = crc64(builder.buffers());
        assertEquals(v1, v2);
        byte[] v3 = longToByteArray(v2);
        builder.put(v3);
        assertEquals(test.getBytes().length + 8, builder.array().length);
        byte[] v4 = builder.array();
        
        assertArrayEquals(test.getBytes(), Arrays.copyOfRange(v4, 0, test.getBytes().length));
        assertArrayEquals(v3, Arrays.copyOfRange(v4, test.getBytes().length, v4.length));
    }
    
}