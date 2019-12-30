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

package com.moilioncircle.redis.replicator.rdb.module;

import com.moilioncircle.redis.replicator.Constants;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.BaseRdbParser;
import com.moilioncircle.redis.replicator.util.ByteArray;
import com.moilioncircle.redis.replicator.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.moilioncircle.redis.replicator.Constants.RDB_MODULE_OPCODE_DOUBLE;
import static com.moilioncircle.redis.replicator.Constants.RDB_MODULE_OPCODE_FLOAT;
import static com.moilioncircle.redis.replicator.Constants.RDB_MODULE_OPCODE_STRING;
import static com.moilioncircle.redis.replicator.Constants.RDB_MODULE_OPCODE_UINT;

/**
 * @author Leon Chen
 * @version 2.1.2
 * @since 2.1.0
 */
public class DefaultRdbModuleParser {
    protected static final Logger logger = LoggerFactory.getLogger(DefaultRdbModuleParser.class);

    private final RedisInputStream in;
    private final BaseRdbParser parser;

    public DefaultRdbModuleParser(RedisInputStream in) {
        this.in = in;
        this.parser = new BaseRdbParser(in);
    }

    public RedisInputStream inputStream() {
        return this.in;
    }

    /**
     * @param version param version of {@link ModuleParser#parse(RedisInputStream, int)}
     * @return signed long
     * @throws IOException IOException
     * @since 2.3.0
     */
    public long loadSigned(int version) throws IOException {
        if (version == 2) {
            long opcode = parser.rdbLoadLen().len;
            if (opcode != RDB_MODULE_OPCODE_UINT)
                throw new UnsupportedOperationException("Error loading signed or unsigned long from RDB.");
        }
        return parser.rdbLoadLen().len;
    }

    /**
     * @param version param version of {@link ModuleParser#parse(RedisInputStream, int)}
     * @return unsigned long
     * @throws IOException IOException
     * @since 2.3.0
     */
    public BigInteger loadUnsigned(int version) throws IOException {
        byte[] ary = new byte[8];
        long value = loadSigned(version);
        for (int i = 0; i < 8; i++) {
            ary[7 - i] = (byte) ((value >>> (i << 3)) & 0xFF);
        }
        return new BigInteger(1, ary);
    }

    /**
     * @param version param version of {@link ModuleParser#parse(RedisInputStream, int)}
     * @return string
     * @throws IOException IOException
     * @since 2.3.0
     */
    public String loadString(int version) throws IOException {
        if (version == 2) {
            long opcode = parser.rdbLoadLen().len;
            if (opcode != RDB_MODULE_OPCODE_STRING)
                throw new UnsupportedOperationException("Error loading string from RDB.");
        }
        ByteArray bytes = parser.rdbGenericLoadStringObject(Constants.RDB_LOAD_NONE);
        return Strings.toString(bytes.first());
    }

    /**
     * @param version param version of {@link ModuleParser#parse(RedisInputStream, int)}
     * @return string buffer
     * @throws IOException IOException
     * @since 2.3.0
     */
    public byte[] loadStringBuffer(int version) throws IOException {
        if (version == 2) {
            long opcode = parser.rdbLoadLen().len;
            if (opcode != RDB_MODULE_OPCODE_STRING)
                throw new UnsupportedOperationException("Error loading string from RDB.");
        }
        ByteArray bytes = parser.rdbGenericLoadStringObject(Constants.RDB_LOAD_PLAIN);
        return bytes.first();
    }

    /**
     * @param version param version of {@link ModuleParser#parse(RedisInputStream, int)}
     * @return double
     * @throws IOException IOException
     * @since 2.3.0
     */
    public double loadDouble(int version) throws IOException {
        if (version == 2) {
            long opcode = parser.rdbLoadLen().len;
            if (opcode != RDB_MODULE_OPCODE_DOUBLE)
                throw new UnsupportedOperationException("Error loading double from RDB.");
        }
        return parser.rdbLoadBinaryDoubleValue();
    }

    /**
     * @param version param version of {@link ModuleParser#parse(RedisInputStream, int)}
     * @return single precision float
     * @throws IOException io exception
     * @since 2.3.0
     */
    public float loadFloat(int version) throws IOException {
        if (version == 2) {
            long opcode = parser.rdbLoadLen().len;
            if (opcode != RDB_MODULE_OPCODE_FLOAT)
                throw new UnsupportedOperationException("Error loading float from RDB.");
        }
        return parser.rdbLoadBinaryFloatValue();
    }

    /**
     * @param version param version of {@link ModuleParser#parse(RedisInputStream, int)}
     * @return BigDecimal(long double in c)
     * @throws IOException io exception
     * @since 3.4.0
     */
    public BigDecimal loadLongDouble(int version) throws IOException {
        return new BigDecimal(loadString(version));
    }
}
