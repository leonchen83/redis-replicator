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

import com.moilioncircle.redis.replicator.util.Strings;
import junit.framework.TestCase;

/**
 * @author Leon Chen
 * @since 2.2.0
 */
public class AbstractParserTest {

    protected Object[] toObjectArray(Object[] raw) {
        Object[] r = new Object[raw.length];
        for (int i = 0; i < r.length; i++) {
            if (raw[i] instanceof String) r[i] = ((String) raw[i]).getBytes();
            else r[i] = raw[i];
        }
        return r;
    }

    protected void assertEquals(String str, byte[] bytes) {
        TestCase.assertEquals(str, Strings.toString(bytes));
    }

    protected void assertEquals(boolean a, boolean b) {
        TestCase.assertEquals(a, b);
    }

    protected void assertEquals(long a, long b) {
        TestCase.assertEquals(a, b);
    }

    protected void assertEquals(Boolean a, Boolean b) {
        TestCase.assertEquals(a, b);
    }
}
