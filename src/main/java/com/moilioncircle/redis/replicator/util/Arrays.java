/*
 * Copyright 2016 leon chen
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

import com.moilioncircle.redis.replicator.Constants;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Leon Chen
 * @since 2.2.0
 */
public class Arrays {

    public static String deepToString(Object[] a) {
        if (a == null) return "null";
        int bufLen = 20 * a.length;
        if (a.length != 0 && bufLen <= 0)
            bufLen = Integer.MAX_VALUE;
        StringBuilder buf = new StringBuilder(bufLen);
        deepToString(a, buf, new HashSet<Object[]>());
        return buf.toString();
    }

    private static void deepToString(Object[] a, StringBuilder buf, Set<Object[]> dejaVu) {
        if (a == null) {
            buf.append("null");
            return;
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            buf.append("[]");
            return;
        }

        dejaVu.add(a);
        buf.append('[');
        for (int i = 0; ; i++) {
            Object element = a[i];
            if (element == null) {
                buf.append("null");
            } else {
                Class<?> eClass = element.getClass();

                if (eClass.isArray()) {
                    if (eClass == byte[].class)
                        buf.append(toString((byte[]) element));
                    else if (eClass == short[].class)
                        buf.append(toString((short[]) element));
                    else if (eClass == int[].class)
                        buf.append(toString((int[]) element));
                    else if (eClass == long[].class)
                        buf.append(toString((long[]) element));
                    else if (eClass == char[].class)
                        buf.append(toString((char[]) element));
                    else if (eClass == float[].class)
                        buf.append(toString((float[]) element));
                    else if (eClass == double[].class)
                        buf.append(toString((double[]) element));
                    else if (eClass == boolean[].class)
                        buf.append(toString((boolean[]) element));
                    else { // element is an array of object references
                        if (dejaVu.contains(element))
                            buf.append("[...]");
                        else
                            deepToString((Object[]) element, buf, dejaVu);
                    }
                } else {  // element is non-null and not an array
                    buf.append(element.toString());
                }
            }
            if (i == iMax) break;
            buf.append(", ");
        }
        buf.append(']');
        dejaVu.remove(a);
    }

    public static String toString(long[] a) {
        if (a == null) return "null";
        int iMax = a.length - 1;
        if (iMax == -1) return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(int[] a) {
        if (a == null) return "null";
        int iMax = a.length - 1;
        if (iMax == -1) return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(short[] a) {
        if (a == null) return "null";
        int iMax = a.length - 1;
        if (iMax == -1) return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(char[] a) {
        if (a == null) return "null";
        int iMax = a.length - 1;
        if (iMax == -1) return "";
        return new String(a);
    }

    private static String toString(byte[] a) {
        if (a == null) return "null";
        int iMax = a.length - 1;
        if (iMax == -1) return "";
        return new String(a, Constants.CHARSET);
    }

    private static String toString(boolean[] a) {
        if (a == null) return "null";
        int iMax = a.length - 1;
        if (iMax == -1) return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) return b.append(']').toString();
            b.append(", ");
        }
    }

    private static String toString(float[] a) {
        if (a == null) return "null";
        int iMax = a.length - 1;
        if (iMax == -1) return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) return b.append(']').toString();
            b.append(", ");
        }
    }

    private static String toString(double[] a) {
        if (a == null) return "null";
        int iMax = a.length - 1;
        if (iMax == -1) return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) return b.append(']').toString();
            b.append(", ");
        }
    }

}
