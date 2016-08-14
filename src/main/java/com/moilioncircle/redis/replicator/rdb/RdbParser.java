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

package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.*;
import com.moilioncircle.redis.replicator.util.Lzf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static com.moilioncircle.redis.replicator.Constants.*;

/**
 * Redis RDB format
 *
 * @author leon.chen
 * @see [https://github.com/antirez/redis/blob/3.0/src/rdb.c]
 * @see [https://github.com/sripathikrishnan/redis-rdb-tools/wiki/Redis-RDB-Dump-File-Format]
 * @since 2016/8/11
 */
public class RdbParser {
    private static final Log logger = LogFactory.getLog(RdbParser.class);

    private final RedisInputStream in;

    private final Replicator replicator;

    public RdbParser(RedisInputStream in, Replicator replicator) {
        this.in = in;
        this.replicator = replicator;
    }

    public long parse() throws IOException {
        return rdbLoad();
    }

    /**
     * ----------------------------# RDB is a binary format. There are no new lines or spaces in the file.
     * 52 45 44 49 53              # Magic String "REDIS"
     * 30 30 30 33                 # RDB Version Number in big endian. In this case, version = 0003 = 3
     * ----------------------------
     * FE 00                       # FE = code that indicates database selector. db number = 00
     * ----------------------------# Key-Value pair starts
     * FD $unsigned int            # FD indicates "expiry time in seconds". After that, expiry time is read as a 4 byte unsigned int
     * $value-type                 # 1 byte flag indicating the type of value - set, map, sorted set etc.
     * $string-encoded-name         # The name, encoded as a redis string
     * $encoded-value              # The value. Encoding depends on $value-type
     * ----------------------------
     * FC $unsigned long           # FC indicates "expiry time in ms". After that, expiry time is read as a 8 byte unsigned long
     * $value-type                 # 1 byte flag indicating the type of value - set, map, sorted set etc.
     * $string-encoded-name         # The name, encoded as a redis string
     * $encoded-value              # The value. Encoding depends on $value-type
     * ----------------------------
     * $value-type                 # This name value pair doesn't have an expiry. $value_type guaranteed != to FD, FC, FE and FF
     * $string-encoded-name
     * $encoded-value
     * ----------------------------
     * FE $length-encoding         # Previous db ends, next db starts. Database number read using length encoding.
     * ----------------------------
     * ...                         # Key value pairs for this database, additonal database
     * FF                          ## End of RDB file indicator
     * 8 byte checksum             ## CRC 64 checksum of the entire file.
     *
     * @return read bytes
     * @throws IOException
     */
    private long rdbLoad() throws IOException {
        /*
         * ----------------------------
         * 52 45 44 49 53              # Magic String "REDIS"
         * 30 30 30 33                 # RDB Version Number in big endian. In this case, version = 0003 = 3
         * ----------------------------
         */
        String magicString = StringHelper.str(in, 5);//REDIS
        if (!magicString.equals("REDIS")) {
            logger.error("Can't read MAGIC STRING [REDIS] ,value:" + magicString);
            return in.total();
        }
        int version = Integer.parseInt(StringHelper.str(in, 4));//0006
        if (version < 1 || version > 6) {
            logger.error("Can't handle RDB format version " + version);
            return in.total();
        }
        /*
         * ----------------------------
         * FE 00                       # FE = code that indicates database selector. db number = 00
         * ----------------------------
         */
        int flag;
        if ((flag = in.read()) != 0xfe) {
            logger.error("Expect byte [0xfe] but :[0x" + Integer.toHexString(flag) + "]");
            return in.total();
        }
        int dbNumber = in.readInt(1);

        /**
         * rdb
         */
        loop:
        while (true) {
            int type = in.read();
            KeyValuePair kv = null;
            switch (type) {
                /*
                 * ----------------------------
                 * FD $unsigned int            # FD indicates "expiry time in seconds". After that, expiry time is read as a 4 byte unsigned int
                 * $value-type                 # 1 byte flag indicating the type of value - set, map, sorted set etc.
                 * $string-encoded-name         # The name, encoded as a redis string
                 * $encoded-value              # The value. Encoding depends on $value-type
                 * ----------------------------
                 */
                case REDIS_RDB_OPCODE_EXPIRETIME:
                    int expiredSec = rdbLoadTime();
                    int valueType = in.read();
                    String key = rdbLoadEncodedStringObject();
                    kv = rdbLoadObject(valueType);
                    kv.setDbNumber(dbNumber);
                    kv.setExpiredSeconds(expiredSec);
                    kv.setKey(key);
                    break;
                /*
                 * ----------------------------
                 * FC $unsigned long           # FC indicates "expiry time in ms". After that, expiry time is read as a 8 byte unsigned long
                 * $value-type                 # 1 byte flag indicating the type of value - set, map, sorted set etc.
                 * $string-encoded-name         # The name, encoded as a redis string
                 * $encoded-value              # The value. Encoding depends on $value-type
                 * ----------------------------
                 */
                case REDIS_RDB_OPCODE_EXPIRETIME_MS:
                    long expiredMs = rdbLoadMillisecondTime();
                    valueType = in.read();
                    key = rdbLoadEncodedStringObject();
                    kv = rdbLoadObject(valueType);
                    kv.setDbNumber(dbNumber);
                    kv.setExpiredMs(expiredMs);
                    kv.setKey(key);
                    break;
                /*
                 * ----------------------------
                 * $value-type                 # This name value pair doesn't have an expiry. $value_type guaranteed != to FD, FC, FE and FF
                 * $string-encoded-name
                 * $encoded-value
                 * ----------------------------
                 */
                case REDIS_RDB_TYPE_STRING:
                case REDIS_RDB_TYPE_LIST:
                case REDIS_RDB_TYPE_SET:
                case REDIS_RDB_TYPE_ZSET:
                case REDIS_RDB_TYPE_HASH:
                case REDIS_RDB_TYPE_HASH_ZIPMAP:
                case REDIS_RDB_TYPE_LIST_ZIPLIST:
                case REDIS_RDB_TYPE_SET_INTSET:
                case REDIS_RDB_TYPE_ZSET_ZIPLIST:
                case REDIS_RDB_TYPE_HASH_ZIPLIST:
                    valueType = type;
                    key = rdbLoadEncodedStringObject();
                    kv = rdbLoadObject(valueType);
                    kv.setDbNumber(dbNumber);
                    kv.setKey(key);
                    break;
                /*
                 * ----------------------------
                 * FE $length-encoding         # Previous db ends, next db starts. Database number read using length encoding.
                 * ----------------------------
                 */
                case REDIS_RDB_OPCODE_SELECTDB:
                    dbNumber = rdbLoadLen().len;
                    break;
                /*
                 * ----------------------------
                 * ...                         # Key value pairs for this database, additonal database
                 * FF                          ## End of RDB file indicator
                 * 8 byte checksum             ## CRC 64 checksum of the entire file.
                 * ----------------------------
                 */
                case REDIS_RDB_OPCODE_EOF:
                    byte[] checksum = in.readBytes(8);
                    break loop;
                default:
                    throw new AssertionError("Un-except value-type:" + type);
            }
            if (kv == null) continue;
            if (logger.isDebugEnabled()) logger.debug(kv);
            if (!replicator.doRdbFilter(kv)) continue;
            replicator.doRdbHandler(kv);
        }
        return in.total();
    }

    /**
     * "expiry time in seconds". After that, expiry time is read as a 4 byte unsigned int
     *
     * @return seconds
     * @throws IOException
     */
    private int rdbLoadTime() throws IOException {
        return in.readInt(4);
    }

    /**
     * "expiry time in ms". After that, expiry time is read as a 8 byte unsigned long
     *
     * @return millisecond
     * @throws IOException
     */
    private long rdbLoadMillisecondTime() throws IOException {
        return in.readLong(8);
    }

    /**
     * read bytes 1 or 2 or 5
     * 1. |00xxxxxx| remaining 6 bits represent the length
     * 2. |01xxxxxx|xxxxxxxx| the combined 14 bits represent the length
     * 3. |10xxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx| the remaining 6 bits are discarded.Additional 4 bytes represent the length(big endian in version6)
     * 4. |11xxxxxx| the remaining 6 bits are read.and then the next object is encoded in a special format.so we set isencoded = true
     *
     * @return tuple(len, isencoded)
     * @throws IOException
     * @see #rdbLoadIntegerObject
     * @see #rdbLoadLzfStringObject
     */
    private Len rdbLoadLen() throws IOException {
        boolean isencoded = false;
        int rawByte = in.read();
        int type = (rawByte & 0xc0) >> 6;
        int value;
        switch (type) {
            case REDIS_RDB_ENCVAL:
                isencoded = true;
                value = rawByte & 0x3f;
                break;
            case REDIS_RDB_6BITLEN:
                value = rawByte & 0x3f;
                break;
            case REDIS_RDB_14BITLEN:
                value = ((rawByte & 0x3F) << 8) | in.read();
                break;
            case REDIS_RDB_32BITLEN:
                value = in.readInt(4, false);
                break;
            default:
                throw new AssertionError("Un-except len-type:" + type);

        }
        return new Len(value, isencoded);
    }

    /**
     * @param enctype 0,1,2
     * @param encode  true: encoded string.false:raw bytes
     * @return String rdb object
     * @throws IOException
     */
    private Object rdbLoadIntegerObject(int enctype, boolean encode) throws IOException {
        byte[] value;
        switch (enctype) {
            case REDIS_RDB_ENC_INT8:
                value = in.readBytes(1);
                break;
            case REDIS_RDB_ENC_INT16:
                value = in.readBytes(2);
                break;
            case REDIS_RDB_ENC_INT32:
                value = in.readBytes(4);
                break;
            default:
                value = new byte[]{0x00};
                break;
        }
        return encode ? String.valueOf(in.readInt(value)) : value;
    }

    /**
     * |11xxxxxx| remaining 6bit is 3,then lzf compressed string follows
     * lzf format
     * |lzf flag|clen:1 or 2 or 5 bytes|len:1 or 2 or 5 bytes |       lzf compressed bytes           |
     * |11xxxxxx|xxxxxxxx|....|xxxxxxxx|xxxxxxxx|....|xxxxxxxx|xxxxxxxx|xxxxxxxx|............xxxxxxxx|
     *
     * @param encode true: encoded string.false:raw bytes
     * @return String rdb object
     * @throws IOException
     * @see #rdbLoadLen
     */
    private Object rdbLoadLzfStringObject(boolean encode) throws IOException {
        int clen = rdbLoadLen().len;
        int len = rdbLoadLen().len;
        byte[] inBytes = in.readBytes(clen);
        byte[] outBytes = Lzf.decode(inBytes, len);
        return encode ? new String(outBytes, "UTF-8") : outBytes;
    }

    /**
     * 1.|11xxxxxx|xxxxxxxx| remaining 6bit is 0, then an 8 bit integer follows
     * 2.|11xxxxxx|xxxxxxxx|xxxxxxxx| remaining 6bit is 1, then an 16 bit integer follows
     * 3.|11xxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx| remaining 6bit is 2, then an 32 bit integer follows
     * 4.|11xxxxxx| remaining 6bit is 3,then lzf compressed string follows
     *
     * @param encode true: encoded string.false:raw bytes
     * @return String rdb object
     * @throws IOException
     * @see #rdbLoadIntegerObject
     * @see #rdbLoadLzfStringObject
     */
    private Object rdbGenericLoadStringObject(boolean encode) throws IOException {
        Len lenObj = rdbLoadLen();
        int len = lenObj.len;
        boolean isencoded = lenObj.isencoded;
        if (isencoded) {
            switch (len) {
                case REDIS_RDB_ENC_INT8:
                case REDIS_RDB_ENC_INT16:
                case REDIS_RDB_ENC_INT32:
                    return rdbLoadIntegerObject(len, encode);
                case REDIS_RDB_ENC_LZF:
                    return rdbLoadLzfStringObject(encode);
                default:
                    throw new AssertionError("Unknown RdbParser encoding type:" + len);
            }
        }
        return encode ? StringHelper.str(in, len) : in.readBytes(len);
    }

    /**
     * @return String rdb object with raw bytes
     * @throws IOException
     */
    private byte[] rdbLoadRawStringObject() throws IOException {
        return (byte[]) rdbGenericLoadStringObject(false);
    }

    /**
     * @return String rdb object with UTF-8 string
     * @throws IOException
     */
    private String rdbLoadEncodedStringObject() throws IOException {
        return (String) rdbGenericLoadStringObject(true);
    }

    private KeyValuePair rdbLoadObject(int rdbtype) throws IOException {
        switch (rdbtype) {
            /*
             * |       <content>       |
             * |    string contents    |
             */
            case REDIS_RDB_TYPE_STRING:
                KeyStringValueString o0 = new KeyStringValueString();
                String val = rdbLoadEncodedStringObject();
                o0.setValueRdbType(rdbtype);
                o0.setValue(val);
                return o0;
            /*
             * |    <len>     |       <content>       |
             * | 1 or 5 bytes |    string contents    |
             */
            case REDIS_RDB_TYPE_LIST:
                int len = rdbLoadLen().len;
                KeyStringValueList o1 = new KeyStringValueList();
                List<String> list = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    String element = rdbLoadEncodedStringObject();
                    list.add(element);
                }
                o1.setValueRdbType(rdbtype);
                o1.setValue(list);
                return o1;
            /*
             * |    <len>     |       <content>       |
             * | 1 or 5 bytes |    string contents    |
             */
            case REDIS_RDB_TYPE_SET:
                len = rdbLoadLen().len;
                KeyStringValueSet o2 = new KeyStringValueSet();
                Set<String> set = new LinkedHashSet<>();
                for (int i = 0; i < len; i++) {
                    String element = rdbLoadEncodedStringObject();
                    set.add(element);
                }
                o2.setValueRdbType(rdbtype);
                o2.setValue(set);
                return o2;
            /*
             * |    <len>     |       <content>       |        <score>       |
             * | 1 or 5 bytes |    string contents    |    double content    |
             */
            case REDIS_RDB_TYPE_ZSET:
                len = rdbLoadLen().len;
                KeyStringValueZSet o3 = new KeyStringValueZSet();
                Set<ZSetEntry> zset = new LinkedHashSet<>();
                while (len > 0) {
                    String element = rdbLoadEncodedStringObject();
                    double score = rdbLoadDoubleValue();
                    zset.add(new ZSetEntry(element, score));
                    len--;
                }
                o3.setValueRdbType(rdbtype);
                o3.setValue(zset);
                return o3;
            /*
             * |    <len>     |       <content>       |
             * | 1 or 5 bytes |    string contents    |
             */
            case REDIS_RDB_TYPE_HASH:
                len = rdbLoadLen().len;
                KeyStringValueHash o4 = new KeyStringValueHash();
                Map<String, String> map = new LinkedHashMap<>();
                while (len > 0) {
                    String field = rdbLoadEncodedStringObject();
                    String value = rdbLoadEncodedStringObject();
                    map.put(field, value);
                    len--;
                }
                o4.setValueRdbType(rdbtype);
                o4.setValue(map);
                return o4;
            /*
             * |<zmlen> |   <len>     |"foo"    |    <len>   | <free> |   "bar" |<zmend> |
             * | 1 byte | 1 or 5 byte | content |1 or 5 byte | 1 byte | content | 1 byte |
             */
            case REDIS_RDB_TYPE_HASH_ZIPMAP:
                byte[] aux = rdbLoadRawStringObject();
                RedisInputStream stream = new RedisInputStream(new ByteArrayInputStream(aux));
                KeyStringValueHash o9 = new KeyStringValueHash();
                map = new LinkedHashMap<>();
                int zmlen = LenHelper.zmlen(stream);
                while (true) {
                    int zmEleLen = LenHelper.zmElementLen(stream);
                    if (zmEleLen == -1) {
                        break;
                    }
                    String field = StringHelper.str(stream, zmEleLen);
                    zmEleLen = LenHelper.zmElementLen(stream);
                    int free = LenHelper.free(stream);
                    String value = StringHelper.str(stream, zmEleLen);
                    StringHelper.skip(stream, free);
                    map.put(field, value);
                }
                int zmend = LenHelper.zmend(stream);
                if (zmend != 255) {
                    throw new AssertionError("zmend expected 255 but " + zmend);
                }
                o9.setValueRdbType(rdbtype);
                o9.setValue(map);
                return o9;
            /*
             * |<encoding>| <length-of-contents>|              <contents>                           |
             * | 4 bytes  |            4 bytes  | 2 bytes lement| 4 bytes element | 8 bytes element |
             */
            case REDIS_RDB_TYPE_SET_INTSET:
                aux = rdbLoadRawStringObject();
                stream = new RedisInputStream(new ByteArrayInputStream(aux));
                KeyStringValueSet o11 = new KeyStringValueSet();
                set = new LinkedHashSet<>();
                int encoding = LenHelper.encoding(stream);
                int lenOfContent = LenHelper.lenOfContent(stream);
                for (int i = 0; i < lenOfContent; i++) {
                    switch (encoding) {
                        case 2:
                            set.add(String.valueOf(stream.readInt(2)));
                        case 4:
                            set.add(String.valueOf(stream.readInt(4)));
                        case 8:
                            set.add(String.valueOf(stream.readLong(8)));
                        default:
                            throw new AssertionError("Expect encoding [2,4,8] but:" + encoding);
                    }
                }
                o11.setValueRdbType(rdbtype);
                o11.setValue(set);
                return o11;
            /*
             * |<zlbytes>| <zltail>| <zllen>| <entry> ...<entry> | <zlend>|
             * | 4 bytes | 4 bytes | 2bytes | zipListEntry ...   | 1byte  |
             */
            case REDIS_RDB_TYPE_LIST_ZIPLIST:
                aux = rdbLoadRawStringObject();
                stream = new RedisInputStream(new ByteArrayInputStream(aux));
                KeyStringValueList o10 = new KeyStringValueList();
                list = new ArrayList<>();
                int zlbytes = LenHelper.zlbytes(stream);
                int zltail = LenHelper.zltail(stream);
                int zllen = LenHelper.zllen(stream);
                for (int i = 0; i < zllen; i++) {
                    list.add(StringHelper.zipListEntry(stream));
                }
                int zlend = LenHelper.zlend(stream);
                if (zlend != 255) {
                    throw new AssertionError("zlend expected 255 but " + zlend);
                }
                o10.setValueRdbType(rdbtype);
                o10.setValue(list);
                return o10;
            /*
             * |<zlbytes>| <zltail>| <zllen>| <entry> ...<entry> | <zlend>|
             * | 4 bytes | 4 bytes | 2bytes | zipListEntry ...   | 1byte  |
             */
            case REDIS_RDB_TYPE_ZSET_ZIPLIST:
                aux = rdbLoadRawStringObject();
                stream = new RedisInputStream(new ByteArrayInputStream(aux));
                KeyStringValueZSet o12 = new KeyStringValueZSet();
                zset = new LinkedHashSet<>();
                zlbytes = LenHelper.zlbytes(stream);
                zltail = LenHelper.zltail(stream);
                zllen = LenHelper.zllen(stream);
                while (zllen > 0) {
                    String element = StringHelper.zipListEntry(stream);
                    zllen--;
                    double score = Double.valueOf(StringHelper.zipListEntry(stream));
                    zllen--;
                    zset.add(new ZSetEntry(element, score));
                }
                zlend = LenHelper.zlend(stream);
                if (zlend != 255) {
                    throw new AssertionError("zlend expected 255 but " + zlend);
                }
                o12.setValueRdbType(rdbtype);
                o12.setValue(zset);
                return o12;
            /*
             * |<zlbytes>| <zltail>| <zllen>| <entry> ...<entry> | <zlend>|
             * | 4 bytes | 4 bytes | 2bytes | zipListEntry ...   | 1byte  |
             */
            case REDIS_RDB_TYPE_HASH_ZIPLIST:
                aux = rdbLoadRawStringObject();
                stream = new RedisInputStream(new ByteArrayInputStream(aux));
                KeyStringValueHash o13 = new KeyStringValueHash();
                map = new LinkedHashMap<>();
                zlbytes = LenHelper.zlbytes(stream);
                zltail = LenHelper.zltail(stream);
                zllen = LenHelper.zllen(stream);
                while (zllen > 0) {
                    String field = StringHelper.zipListEntry(stream);
                    zllen--;
                    String value = StringHelper.zipListEntry(stream);
                    zllen--;
                    map.put(field, value);
                }
                zlend = LenHelper.zlend(stream);
                if (zlend != 255) {
                    throw new AssertionError("zlend expected 255 but " + zlend);
                }
                o13.setValueRdbType(rdbtype);
                o13.setValue(map);
                return o13;
            default:
                throw new AssertionError("Un-except value-type:" + rdbtype);

        }
    }

    private double rdbLoadDoubleValue() throws IOException {
        int len = in.read();
        switch (len) {
            case 255:
                return Double.NEGATIVE_INFINITY;
            case 254:
                return Double.POSITIVE_INFINITY;
            case 253:
                return Double.NaN;
            default:
                byte[] bytes = in.readBytes(len);
                return Double.valueOf(new String(bytes));
        }
    }

    /**
     * @see #rdbLoadLen
     */
    private static class Len {
        public final int len;
        public final boolean isencoded;

        private Len(int len, boolean isencoded) {
            this.len = len;
            this.isencoded = isencoded;
        }
    }

    private static class StringHelper {
        private StringHelper() {
        }

        public static String str(RedisInputStream in, int len) throws IOException {
            return in.readString(len);
        }

        public static long skip(RedisInputStream in, long len) throws IOException {
            return in.skip(len);
        }

        /**
         * <length-prev-entry><special-flag><raw-bytes-of-entry>
         * <length-prev-entry> format:
         * |xxxxxxxx| if first byte value < 254. then 1 byte as prev len.
         * |xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx| if first byte >=254 then next 4 byte as prev len.
         * <p>
         * <special-flag>:
         * |00xxxxxx| remaining 6 bit as string len.
         * |01xxxxxx|xxxxxxxx| combined 14 bit as string len.
         * |10xxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx| next 4 byte as string len.
         * <p>
         * |11111110|xxxxxxxx| next 1 byte as 8bit int
         * |11000000|xxxxxxxx|xxxxxxxx| next 2 bytes as 16bit int
         * |11110000|xxxxxxxx|xxxxxxxx|xxxxxxxx| next 3 bytes as 24bit int
         * |11010000|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx| next 4 bytes as 32bit int
         * |11100000|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx|xxxxxxxx| next 8 bytes as 64bit long
         * |11xxxxxx| next 6 bit value as int value
         *
         * @param in ziplist raw bytes stream
         * @return encoded string
         * @throws IOException
         */
        public static String zipListEntry(RedisInputStream in) throws IOException {
            int prevlen = in.read();
            if (prevlen >= 254) {
                prevlen = in.readInt(4);
            }
            int special = in.read();
            switch (special >> 6) {
                case 0:
                    int len = special & 0x3f;
                    return StringHelper.str(in, len);
                case 1:
                    len = ((special & 0x3f) << 8) | in.read();
                    return StringHelper.str(in, len);
                case 2:
                    //bigEndian
                    len = in.readInt(4, false);
                    return StringHelper.str(in, len);
                default:
                    break;
            }
            switch (special) {
                case ZIP_INT_8B:
                    return String.valueOf(in.readInt(1));
                case ZIP_INT_16B:
                    return String.valueOf(in.readInt(2));
                case ZIP_INT_24B:
                    return String.valueOf(in.readInt(3));
                case ZIP_INT_32B:
                    return String.valueOf(in.readInt(4));
                case ZIP_INT_64B:
                    return String.valueOf(in.readLong(8));
                default:
                    //6BIT
                    return String.valueOf(special - 0xf1);
            }
        }
    }

    private static class LenHelper {
        private LenHelper() {
        }

        //zip hash
        public static int zmlen(RedisInputStream in) throws IOException {
            return in.read();
        }

        public static int zmend(RedisInputStream in) throws IOException {
            return in.read();
        }

        public static int free(RedisInputStream in) throws IOException {
            return in.read();
        }

        public static int zmElementLen(RedisInputStream in) throws IOException {
            int len = in.read();
            if (len >= 0 && len <= 253) {
                return len;
            } else if (len == 254) {
                return in.readInt(4, false);
            } else {
                return -1;
            }
        }

        //zip list
        public static int zlbytes(RedisInputStream in) throws IOException {
            return in.readInt(4);
        }

        public static int zlend(RedisInputStream in) throws IOException {
            return in.read();
        }

        public static int zltail(RedisInputStream in) throws IOException {
            return in.readInt(4);
        }

        public static int zllen(RedisInputStream in) throws IOException {
            return in.readInt(2);
        }

        //int set
        public static int encoding(RedisInputStream in) throws IOException {
            return in.readInt(4);
        }

        public static int lenOfContent(RedisInputStream in) throws IOException {
            return in.readInt(4);
        }
    }
}

