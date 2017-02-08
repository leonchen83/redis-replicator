package com.moilioncircle.redis.replicator.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/8/17.
 */
public class ByteBuilderTest {
    @Test
    public void put() throws Exception {
        ByteBuilder bytes = ByteBuilder.allocate(20);
        String test = "fjdsklafjsdklafjaklfdsjkfsdkjakjdkasjdkajdkajqwieuiqwueiqweqwieuqidasjkasjkajdkcnxzcnzxasjdksadasiuqwieuqwiejaskdajskcxnzcznczkxnasdjasjdjadqweiqwueidjdskdjaskdjskajdakjcncnzxknczxjkasdjaskdjqwieuqwiuakdakncxzkjsakdasjdiqwueijcnkdasjdiuewiqeqdijqdsahdiadiwqueqiwqidjaskdjaskdjqwjieuqfhhaksjduqiwehcaskdjasdaiqwewiqdhashdadashjqweyqwuh";
        test.getBytes();
        for (byte b : test.getBytes()) {
            bytes.put(b);
        }
        String s = bytes.toString();
        assertEquals(test, s);
        assertEquals(test.getBytes().length, bytes.length());
    }
}