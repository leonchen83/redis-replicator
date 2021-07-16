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

package com.moilioncircle.redis.replicator.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ByteBuilderTest {
    @Test
    public void put() {
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
    
    @Test
    public void put0() {
        ByteBuilder bytes = ByteBuilder.allocate(20);
        String test = "fjdsklafjsdklafjaklfdsjkfsdkjakjdkasjdkajdkajqwieuiqwueiqweqwieuqidasjkasjkajdkcnxzcnzxasjdksadasiuqwieuqwiejaskdajskcxnzcznczkxnasdjasjdjadqweiqwueidjdskdjaskdjskajdakjcncnzxknczxjkasdjaskdjqwieuqwiuakdakncxzkjsakdasjdiqwueijcnkdasjdiuewiqeqdijqdsahdiadiwqueqiwqidjaskdjaskdjqwjieuqfhhaksjduqiwehcaskdjasdaiqwewiqdhashdadashjqweyqwuh";
        bytes.put(test.getBytes());
        String s = bytes.toString();
        assertEquals(test, s);
        assertEquals(test.getBytes().length, bytes.length());
    }
    
    @Test
    public void put1() {
        ByteBuilder bytes = ByteBuilder.allocate(20);
        String test = "fjdsklafjsdklafjaklfdsjkfsdkjakjdkasjdkajdkajqwieuiqwueiqweqwieuqidasjkasjkajdkcnxzcnzxasjdksadasiuqwieuqwiejaskdajskcxnzcznczkxnasdjasjdjadqweiqwueidjdskdjaskdjskajdakjcncnzxknczxjkasdjaskdjqwieuqwiuakdakncxzkjsakdasjdiqwueijcnkdasjdiuewiqeqdijqdsahdiadiwqueqiwqidjaskdjaskdjqwjieuqfhhaksjduqiwehcaskdjasdaiqwewiqdhashdadashjqweyqwuh";
        bytes.put(ByteBuffer.wrap(test.getBytes()));
        String s = bytes.toString();
        assertEquals(test, s);
        assertEquals(test.getBytes().length, bytes.length());
    }
    
    @Test
    public void put2() {
        ByteBuilder bytes = ByteBuilder.allocate(20);
        String test = "fjdsklafjsdklafjaklfdsjkfsdkjakjdkasjdkajdkajqwieuiqwueiqweqwieuqidasjkasjkajdkcnxzcnzxasjdksadasiuqwieuqwiejaskdajskcxnzcznczkxnasdjasjdjadqweiqwueidjdskdjaskdjskajdakjcncnzxknczxjkasdjaskdjqwieuqwiuakdakncxzkjsakdasjdiqwueijcnkdasjdiuewiqeqdijqdsahdiadiwqueqiwqidjaskdjaskdjqwjieuqfhhaksjduqiwehcaskdjasdaiqwewiqdhashdadashjqweyqwuh";
        bytes.put(ByteBuffer.wrap(test.getBytes()));
        String s = bytes.toString();
        assertEquals(test, s);
        ByteBuilder bytes1 = ByteBuilder.allocate(20);
        for (ByteBuffer buf : bytes.buffers()) {
            bytes1.put(buf);
        }
        assertArrayEquals(bytes1.array(), bytes.array());
    }
}