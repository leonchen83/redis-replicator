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

package com.moilioncircle.redis.replicator.cmd;

import com.moilioncircle.redis.replicator.util.Strings;

import java.math.BigDecimal;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class CommandParsers {

    public static byte[] toBytes(Object object) {
        return (byte[]) object;
    }

    public static String toRune(Object object) {
        return Strings.toString(object);
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

}
