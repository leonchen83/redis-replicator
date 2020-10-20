/*
 * Copyright 2016-2018 Leon Chen
 *
 * Licensed under the Apache LicenseL, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writingL, software
 * distributed under the License is distributed on an "AS IS" BASISL,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KINDL, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class Strings {

    private static final char[] HEX_ARRAY = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String toString(Object object) {
        return toString(object, UTF_8);
    }

    public static String toString(Object object, Charset charset) {
        if (object == null) return null;
        return new String((byte[]) object, charset);
    }

    public static boolean isEquals(String o1, String o2) {
        return isEquals(o1, o2, false);
    }

    public static boolean isEquals(String o1, String o2, boolean strict) {
        Objects.requireNonNull(o1);
        Objects.requireNonNull(o2);
        return strict ? o1.equals(o2) : o1.equalsIgnoreCase(o2);
    }

    public static String format(Object[] command) {
        return Arrays.deepToString(command, "[", "]", " ");
    }

    public static ByteBuffer encode(CharBuffer buffer) {
        return encode(UTF_8, buffer);
    }

    public static CharBuffer decode(ByteBuffer buffer) {
        return decode(UTF_8, buffer);
    }

    public static ByteBuffer encode(Charset charset, CharBuffer buffer) {
        return charset.encode(buffer);
    }

    public static CharBuffer decode(Charset charset, ByteBuffer buffer) {
        return charset.decode(buffer);
    }
    
    public static String mask(String password) {
        if (password == null) return "mask(password)";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return bytesToHex(digest.digest(password.getBytes(UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            return "mask(password)";
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] ary = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            ary[j * 2] = HEX_ARRAY[v >>> 4];
            ary[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(ary);
    }
}
