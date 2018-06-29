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

package com.moilioncircle.redis.replicator.rdb.datatype;

import com.moilioncircle.redis.replicator.util.Strings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class ZSetEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] element;
    private double score;

    public ZSetEntry() {
    }

    public ZSetEntry(byte[] element, double score) {
        this.element = element;
        this.score = score;
    }

    public byte[] getElement() {
        return element;
    }

    public double getScore() {
        return score;
    }

    public void setElement(byte[] element) {
        this.element = element;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "[" + Strings.toString(element) + ", " + score + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZSetEntry zSetEntry = (ZSetEntry) o;
        return Double.compare(zSetEntry.score, score) == 0 &&
                Arrays.equals(element, zSetEntry.element);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(score);
        result = 31 * result + Arrays.hashCode(element);
        return result;
    }
}
