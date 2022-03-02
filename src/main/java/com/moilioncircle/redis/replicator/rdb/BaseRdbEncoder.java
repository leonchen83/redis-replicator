/*
 * Copyright 2016-2017 Leon Chen
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

package com.moilioncircle.redis.replicator.rdb;

import static com.moilioncircle.redis.replicator.Constants.RDB_14BITLEN;
import static com.moilioncircle.redis.replicator.Constants.RDB_32BITLEN;
import static com.moilioncircle.redis.replicator.Constants.RDB_64BITLEN;
import static com.moilioncircle.redis.replicator.Constants.RDB_6BITLEN;
import static com.moilioncircle.redis.replicator.Constants.RDB_ENCVAL;
import static com.moilioncircle.redis.replicator.Constants.RDB_ENC_LZF;
import static java.lang.Double.doubleToLongBits;
import static java.lang.Float.floatToIntBits;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import com.moilioncircle.redis.replicator.util.ByteArray;
import com.moilioncircle.redis.replicator.util.Lzf;

/**
 * @author Leon Chen
 * @since 3.5.3
 */
public class BaseRdbEncoder {
    
    /**
     * @param time time
     * @param out out
     * @throws IOException IOException
     * @see BaseRdbParser#rdbLoadTime()
     * @since 3.5.3
     */
    public void rdbSaveTime(int time, OutputStream out) throws IOException {
        out.write(ByteBuffer.allocate(Integer.BYTES).order(LITTLE_ENDIAN).putInt(time).array());
    }
    
    /**
     * @since 3.5.3
     * @param timestamp timestamp
     * @param out out
     * @throws IOException IOException
     * @see BaseRdbParser#rdbLoadMillisecondTime()
     */
    public void rdbSaveMillisecondTime(long timestamp, OutputStream out) throws IOException {
        out.write(ByteBuffer.allocate(Long.BYTES).order(LITTLE_ENDIAN).putLong(timestamp).array());
    }
    
    /**
     * @since 3.5.3
     * @param len len
     * @param out out
     * @return length
     * @throws IOException IOException
     * @see BaseRdbParser#rdbLoadLen()
     */
    public int rdbSaveLen(long len, OutputStream out) throws IOException {
        byte[] ary = toUnsigned(len);
        BigInteger value = new BigInteger(1, ary);
        if (value.compareTo(BigInteger.valueOf(0XFFFFFFFFL)) > 0) {
            /* Save a 64 bit len */
            out.write(RDB_64BITLEN);
            out.write(ByteBuffer.allocate(Long.BYTES).order(BIG_ENDIAN).put(ary).array());
            return 9;
        } else if (len < (1 << 6)) {
            out.write((byte) ((len & 0xFF) | (RDB_6BITLEN << 6)));
            return 1;
        } else if (len < (1 << 14)) {
            /* Save a 14 bit len */
            out.write((byte) (((len >> 8) & 0xFF) | (RDB_14BITLEN << 6)));
            out.write((byte) (len & 0xFF));
            return 2;
        } else if (len <= 0XFFFFFFFFL) {
            /* Save a 32 bit len */
            out.write(RDB_32BITLEN);
            out.write(ByteBuffer.allocate(Integer.BYTES).order(BIG_ENDIAN).putInt((int) len).array());
            return 5;
        } else {
            /* Save a 64 bit len */
            out.write(RDB_64BITLEN);
            out.write(ByteBuffer.allocate(Long.BYTES).order(BIG_ENDIAN).putLong(len).array());
            return 9;
        }
    }
    
    /**
     * @since 3.5.3
     * @param len len
     * @return byte array
     * @throws IOException IOException
     * @see BaseRdbParser#rdbLoadLen()
     */
    public byte[] rdbSaveLen(long len) throws IOException {
        byte[] ary = toUnsigned(len);
        BigInteger value = new BigInteger(1, ary);
        if (value.compareTo(BigInteger.valueOf(0XFFFFFFFFL)) > 0) {
            /* Save a 64 bit len */
            return ByteBuffer.allocate(9).order(BIG_ENDIAN).put((byte) RDB_64BITLEN).put(ary).array();
        } else if (len < (1 << 6)) {
            return new byte[]{(byte) ((len & 0xFF) | (RDB_6BITLEN << 6))};
        } else if (len < (1 << 14)) {
            /* Save a 14 bit len */
            return new byte[]{(byte) (((len >> 8) & 0xFF) | (RDB_14BITLEN << 6)), (byte) (len & 0xFF)};
        } else if (len <= 0XFFFFFFFFL) {
            /* Save a 32 bit len */
            return ByteBuffer.allocate(5).order(BIG_ENDIAN).put((byte) RDB_32BITLEN).putInt((int) len).array();
        } else {
            /* Save a 64 bit len */
            return ByteBuffer.allocate(9).order(BIG_ENDIAN).put((byte) RDB_64BITLEN).putLong(len).array();
        }
    }
    
    /**
     * @since 3.5.3
     * @param value value
     * @param out out
     * @throws IOException IOException
     * @see BaseRdbParser#rdbLoadDoubleValue()
     */
    public void rdbSaveDoubleValue(double value, OutputStream out) throws IOException {
        if (value == Double.NEGATIVE_INFINITY) {
            out.write(255);
        } else if (value == Double.POSITIVE_INFINITY) {
            out.write(254);
        } else if (Double.isNaN(value)) {
            out.write(253);
        } else {
            String str = null;
            if (value == (double) (long) value) {
                str = Long.toString((long) value, 10);
            } else {
                str = String.format("%.17f", value);
            }
            out.write(str.length());
            out.write(str.getBytes());
        }
    }
    
    /**
     * @since 3.5.3
     * @param value value
     * @param out out
     * @throws IOException IOException
     * @see BaseRdbParser#rdbLoadBinaryFloatValue()
     */
    public void rdbSaveBinaryFloatValue(float value, OutputStream out) throws IOException {
        out.write(ByteBuffer.allocate(Long.BYTES).order(LITTLE_ENDIAN).putLong(floatToIntBits(value)).array());
    }
    
    /**
     * @since 3.5.3
     * @param value value
     * @param out out
     * @throws IOException IOException
     * @see BaseRdbParser#rdbLoadBinaryDoubleValue()
     */
    public void rdbSaveBinaryDoubleValue(double value, OutputStream out) throws IOException {
        out.write(ByteBuffer.allocate(Long.BYTES).order(LITTLE_ENDIAN).putLong(doubleToLongBits(value)).array());
    }
    
    /**
     * @since 3.5.3
     * @param bytes input
     * @param out out
     * @throws IOException IOException
     * @see BaseRdbParser#rdbLoadEncodedStringObject()
     */
    public void rdbSaveEncodedStringObject(ByteArray bytes, OutputStream out) throws IOException {
        // at least compress 4 bytes
        ByteArray compressed = new ByteArray(bytes.length() - 4);
        long length = Lzf.encode(bytes, bytes.length(), compressed, 0);
        if (length <= 0) {
            rdbSavePlainStringObject(bytes, out);
        } else {
            int type = (RDB_ENCVAL << 6) | RDB_ENC_LZF;
            out.write(type);
            rdbSaveLen(length, out);
            rdbSaveLen(bytes.length(), out);
            out.write(compressed.first(), 0, (int) length);
        }
    }
    
    /**
     * @since 3.5.3
     * @param bytes bytes
     * @param out out
     * @throws IOException IOException
     * @see BaseRdbParser#rdbGenericLoadStringObject(int)
     */
    public void rdbGenericSaveStringObject(ByteArray bytes, OutputStream out) throws IOException {
        if (bytes.length() > 20) {
            rdbSaveEncodedStringObject(bytes, out);
        } else {
            rdbSavePlainStringObject(bytes, out);
        }
    }
    
    /**
     * @since 3.5.3
     * @param bytes bytes
     * @param out out
     * @throws IOException IOException
     * @see BaseRdbParser#rdbLoadPlainStringObject()
     */
    public void rdbSavePlainStringObject(ByteArray bytes, OutputStream out) throws IOException {
        rdbSaveLen(bytes.length(), out);
        out.write(bytes.first());
    }
    
    private byte[] toUnsigned(long value) {
        byte[] ary = new byte[8];
        for (int i = 0; i < 8; i++) {
            ary[7 - i] = (byte) ((value >>> (i << 3)) & 0xFF);
        }
        return ary;
    }
}
