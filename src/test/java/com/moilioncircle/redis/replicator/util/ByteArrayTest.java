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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ByteArrayTest {
    @Test
    public void test() {

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
        assertEquals(str.substring(10), getString(bytes1));

        str = "sdajk";
        ByteArray bytes2 = new ByteArray(str.getBytes().length, 10);
        b1 = str.getBytes();
        i = 0;
        for (byte b : b1) {
            bytes2.set(i, b);
            assertEquals(b, bytes2.get(i));
            i++;
        }
        assertEquals(getString(bytes2), "sdajk");

        ByteArray bytes3 = new ByteArray(bytes2.length() - 1, 10);
        ByteArray.arraycopy(bytes2, 1, bytes3, 0, bytes2.length() - 1);
        assertEquals(str.substring(1), getString(bytes3));
    }
    
    @Test
    public void testToString() {
        String str = "this is a test";
        ByteArray array = new ByteArray(str.getBytes());
        assertEquals(array.toString(), str);
    }
    
    @Test 
    public void testWriteTo() throws IOException {
        String str = "this is a long string to test byte array";
        byte[] bytes = str.getBytes();
        {
            ByteArray ary = new ByteArray(bytes);
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ary.writeTo(out, 1, str.length() - 11);
                String actual = new String(out.toByteArray());
                assertEquals("his is a long string to test ", actual);
            }
        }
    
        {
            ByteArray ary = new ByteArray(bytes.length, 10);
            for (int i = 0; i < bytes.length; i++) {
                ary.set(i, bytes[i]);
            }
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ary.writeTo(out, 1, str.length() - 11);
                String actual = new String(out.toByteArray());
                assertEquals("his is a long string to test ", actual);
            }
        }
    }

    private String getString(ByteArray ary) {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        for (byte[] b : ary) {
            try {
                o.write(b);
            } catch (IOException e) {
            }
        }
        return new String(o.toByteArray());
    }

}