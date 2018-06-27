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

package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.util.Arrays;

import java.math.BigDecimal;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class CommandParsers {
    
    public static byte[] toBytes(Object object) {
        return (byte[]) object;
    }
    
    public static String toRune(Object object) {
        if (object == null) return null;
        return new String(toBytes(object), UTF_8);
    }
    
    public static double toDouble(Object object) {
        return Double.parseDouble(toRune(object));
    }
    
    public static int toInt(Object object) {
        return new BigDecimal(toRune(object)).intValueExact();
    }
    
    public static long toLong(Object object) {
        return new BigDecimal(toRune(object)).longValueExact();
    }
    
    public static boolean eq(String o1, String o2) {
        Objects.requireNonNull(o1);
        Objects.requireNonNull(o2);
        return o1.equalsIgnoreCase(o2);
    }
    
    public static String format(Object[] command) {
        return format(command, "", "", " ");
    }
    
    public static String format(Object[] command, String st, String ed, String sep) {
        return Arrays.deepToString(command, st, ed, sep);
    }
    
}
