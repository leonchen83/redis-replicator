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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class Strings {

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
}
