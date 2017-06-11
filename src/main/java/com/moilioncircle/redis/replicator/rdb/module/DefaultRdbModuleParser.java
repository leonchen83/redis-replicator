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

package com.moilioncircle.redis.replicator.rdb.module;

import com.moilioncircle.redis.replicator.Constants;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.BaseRdbParser;
import com.moilioncircle.redis.replicator.util.ByteArray;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author Leon Chen
 * @version 2.1.2
 * @since 2.1.0
 */
public class DefaultRdbModuleParser {
    protected static final Log logger = LogFactory.getLog(DefaultRdbModuleParser.class);

    private final RedisInputStream in;
    private final BaseRdbParser parser;

    public DefaultRdbModuleParser(RedisInputStream in) {
        this.in = in;
        this.parser = new BaseRdbParser(in);
    }

    public RedisInputStream inputStream() {
        return this.in;
    }

    public long loadSigned() throws IOException {
        return parser.rdbLoadLen().len;
    }

    /**
     * @return signed long
     * @throws IOException IOException
     * @since 2.1.2
     * @deprecated cause typo and return signed long. Use {@link #loadUnsigned()} instead. will remove this method in 3.0.0
     */
    @Deprecated
    public long loadUnSigned() throws IOException {
        return loadSigned();
    }

    /**
     * @return unsigned long
     * @throws IOException IOException
     * @since 2.1.2
     */
    public BigInteger loadUnsigned() throws IOException {
        byte[] ary = new byte[8];
        long value = loadSigned();
        for (int i = 0; i < 8; i++) {
            ary[7 - i] = (byte) ((value >>> (i << 3)) & 0xFF);
        }
        return new BigInteger(1, ary);
    }

    public String loadString() throws IOException {
        ByteArray bytes = parser.rdbGenericLoadStringObject(Constants.RDB_LOAD_NONE);
        return new String(bytes.first(), Constants.CHARSET);
    }

    public String loadStringBuffer() throws IOException {
        ByteArray bytes = parser.rdbGenericLoadStringObject(Constants.RDB_LOAD_PLAIN);
        return new String(bytes.first(), Constants.CHARSET);
    }

    public double loadDouble() throws IOException {
        return parser.rdbLoadBinaryDoubleValue();
    }
}
