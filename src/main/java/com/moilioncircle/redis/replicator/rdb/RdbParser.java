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

import com.moilioncircle.redis.replicator.AbstractReplicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.PostFullSyncEvent;
import com.moilioncircle.redis.replicator.event.PreFullSyncEvent;
import com.moilioncircle.redis.replicator.io.ByteArrayInputStream;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.*;
import com.moilioncircle.redis.replicator.util.ByteArray;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.moilioncircle.redis.replicator.Constants.*;

/**
 * Redis RDB format
 * rdb version 6
 * rdb version 7
 *
 * @author leon.chen
 *         [https://github.com/antirez/redis/blob/3.0/src/rdb.c]
 *         [https://github.com/sripathikrishnan/redis-rdb-tools/wiki/Redis-RDB-Dump-File-Format]
 * @since 2016/8/11
 */
public class RdbParser extends BaseRdbParser implements RawByteListener {

    protected final AbstractReplicator replicator;
    private final List<RawByteListener> listeners = new CopyOnWriteArrayList<>();

    public RdbParser(RedisInputStream in, AbstractReplicator replicator) {
        super(in);
        this.replicator = replicator;
    }

    public void addRawByteListener(RawByteListener listener) {
        this.listeners.add(listener);
    }

    public void removeRawByteListener(RawByteListener listener) {
        this.listeners.remove(listener);
    }

    private void notify(byte... bytes) {
        for (RawByteListener listener : listeners) {
            listener.handle(bytes);
        }
    }

    @Override
    public void handle(byte... rawBytes) {
        notify(rawBytes);
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
     * @throws IOException when read timeout
     */
    public long parse() throws IOException {
        /*
         * ----------------------------
         * 52 45 44 49 53              # Magic String "REDIS"
         * 30 30 30 33                 # RDB Version Number in big endian. In this case, version = 0003 = 3
         * ----------------------------
         */
        in.addRawByteListener(this);
        try {
            String magicString = StringHelper.str(in, 5);//REDIS
            if (!magicString.equals("REDIS")) {
                logger.error("Can't read MAGIC STRING [REDIS] ,value:" + magicString);
                return in.total();
            }
            int version = Integer.parseInt(StringHelper.str(in, 4));//0006 or 0007
            switch (version) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    this.replicator.submitEvent(new PreFullSyncEvent());
                    long checksum = rdbLoad(version);
                    this.replicator.submitEvent(new PostFullSyncEvent(checksum));
                    return in.total();
                default:
                    logger.error("Can't handle RDB format version " + version);
                    return in.total();
            }
        } finally {
            in.removeRawByteListener(this);
        }
    }

    protected long rdbLoad(int version) throws IOException {
        DB db = null;
        long checksum = 0;
        /**
         * rdb
         */
        loop:
        while (true) {
            int type = in.read();
            Event event = null;
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
                    String key = rdbLoadEncodedStringObject().string;
                    KeyValuePair kv = rdbLoadObject(valueType);
                    kv.setDb(db);
                    kv.setExpiredType(ExpiredType.SECOND);
                    kv.setExpiredValue((long) expiredSec);
                    kv.setKey(key);
                    event = kv;
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
                    key = rdbLoadEncodedStringObject().string;
                    kv = rdbLoadObject(valueType);
                    kv.setDb(db);
                    kv.setExpiredType(ExpiredType.MS);
                    kv.setExpiredValue(expiredMs);
                    kv.setKey(key);
                    event = kv;
                    break;
                case REDIS_RDB_OPCODE_AUX:
                    String auxKey = rdbLoadEncodedStringObject().string;
                    String auxValue = rdbLoadEncodedStringObject().string;
                    if (!auxKey.startsWith("%")) {
                        logger.info("RDB " + auxKey + ": " + auxValue);
                        event = new AuxField(auxKey, auxValue);
                    } else {
                        logger.warn("Unrecognized RDB AUX field: " + auxKey + ",value: " + auxValue);
                    }
                    break;
                case REDIS_RDB_OPCODE_RESIZEDB:
                    long dbsize = rdbLoadLen().len;
                    long expiresSize = rdbLoadLen().len;
                    if (db != null) db.setDbsize(dbsize);
                    if (db != null) db.setExpires(expiresSize);
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
                case REDIS_RDB_TYPE_ZSET_2:
                case REDIS_RDB_TYPE_HASH:
                case REDIS_RDB_TYPE_HASH_ZIPMAP:
                case REDIS_RDB_TYPE_LIST_ZIPLIST:
                case REDIS_RDB_TYPE_SET_INTSET:
                case REDIS_RDB_TYPE_ZSET_ZIPLIST:
                case REDIS_RDB_TYPE_HASH_ZIPLIST:
                case REDIS_RDB_TYPE_LIST_QUICKLIST:
                case REDIS_RDB_TYPE_MODULE:
                    valueType = type;
                    key = rdbLoadEncodedStringObject().string;
                    kv = rdbLoadObject(valueType);
                    kv.setDb(db);
                    kv.setKey(key);
                    event = kv;
                    break;
                /*
                 * ----------------------------
                 * FE $length-encoding         # Previous db ends, next db starts. Database number read using length encoding.
                 * ----------------------------
                 */
                case REDIS_RDB_OPCODE_SELECTDB:
                    long dbNumber = rdbLoadLen().len;
                    db = new DB(dbNumber);
                    break;
                /*
                 * ----------------------------
                 * ...                         # Key value pairs for this database, additonal database
                 * FF                          ## End of RDB file indicator
                 * 8 byte checksum             ## CRC 64 checksum of the entire file.
                 * ----------------------------
                 */
                case REDIS_RDB_OPCODE_EOF:
                    if (version >= 5) checksum = in.readLong(8);
                    break loop;
                default:
                    throw new AssertionError("Un-except value-type:" + type);
            }
            if (event == null) continue;
            if (replicator.verbose() && logger.isDebugEnabled()) logger.debug(event);
            //submit event
            this.replicator.submitEvent(event);
        }
        return checksum;
    }

    private KeyValuePair rdbLoadObject(int rdbtype) throws IOException {
        switch (rdbtype) {
            /*
             * |       <content>       |
             * |    string contents    |
             */
            case REDIS_RDB_TYPE_STRING:
                KeyStringValueString o0 = new KeyStringValueString();
                EncodedString val = rdbLoadEncodedStringObject();
                o0.setValueRdbType(rdbtype);
                o0.setValue(val.string);
                o0.setRawBytes(val.rawBytes);
                return o0;
            /*
             * |    <len>     |       <content>       |
             * | 1 or 5 bytes |    string contents    |
             */
            case REDIS_RDB_TYPE_LIST:
                long len = rdbLoadLen().len;
                KeyStringValueList<String> o1 = new KeyStringValueList<>();
                List<String> list = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    String element = rdbLoadEncodedStringObject().string;
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
                    String element = rdbLoadEncodedStringObject().string;
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
                    String element = rdbLoadEncodedStringObject().string;
                    double score = rdbLoadDoubleValue();
                    zset.add(new ZSetEntry(element, score));
                    len--;
                }
                o3.setValueRdbType(rdbtype);
                o3.setValue(zset);
                return o3;
            /*
             * |    <len>     |       <content>       |        <score>       |
             * | 1 or 5 bytes |    string contents    |    binary double     |
             */
            case REDIS_RDB_TYPE_ZSET_2:
                /* rdb version 8*/
                len = rdbLoadLen().len;
                KeyStringValueZSet o5 = new KeyStringValueZSet();
                zset = new LinkedHashSet<>();
                while (len > 0) {
                    String element = rdbLoadEncodedStringObject().string;
                    double score = rdbLoadBinaryDoubleValue();
                    zset.add(new ZSetEntry(element, score));
                    len--;
                }
                o5.setValueRdbType(rdbtype);
                o5.setValue(zset);
                return o5;
            /*
             * |    <len>     |       <content>       |
             * | 1 or 5 bytes |    string contents    |
             */
            case REDIS_RDB_TYPE_HASH:
                len = rdbLoadLen().len;
                KeyStringValueHash o4 = new KeyStringValueHash();
                Map<String, String> map = new LinkedHashMap<>();
                while (len > 0) {
                    String field = rdbLoadEncodedStringObject().string;
                    String value = rdbLoadEncodedStringObject().string;
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
                ByteArray aux = rdbLoadRawStringObject();
                RedisInputStream stream = new RedisInputStream(new ByteArrayInputStream(aux));
                KeyStringValueHash o9 = new KeyStringValueHash();
                map = new LinkedHashMap<>();
                int zmlen = BaseRdbParser.LenHelper.zmlen(stream);
                while (true) {
                    int zmEleLen = BaseRdbParser.LenHelper.zmElementLen(stream);
                    if (zmEleLen == 255) {
                        o9.setValueRdbType(rdbtype);
                        o9.setValue(map);
                        return o9;
                    }
                    String field = BaseRdbParser.StringHelper.str(stream, zmEleLen);
                    zmEleLen = BaseRdbParser.LenHelper.zmElementLen(stream);
                    if (zmEleLen == 255) {
                        o9.setValueRdbType(rdbtype);
                        o9.setValue(map);
                        return o9;
                    }
                    int free = BaseRdbParser.LenHelper.free(stream);
                    String value = BaseRdbParser.StringHelper.str(stream, zmEleLen);
                    BaseRdbParser.StringHelper.skip(stream, free);
                    map.put(field, value);
                }
            /*
             * |<encoding>| <length-of-contents>|              <contents>                           |
             * | 4 bytes  |            4 bytes  | 2 bytes lement| 4 bytes element | 8 bytes element |
             */
            case REDIS_RDB_TYPE_SET_INTSET:
                aux = rdbLoadRawStringObject();
                stream = new RedisInputStream(new ByteArrayInputStream(aux));
                KeyStringValueSet o11 = new KeyStringValueSet();
                set = new LinkedHashSet<>();
                int encoding = BaseRdbParser.LenHelper.encoding(stream);
                int lenOfContent = BaseRdbParser.LenHelper.lenOfContent(stream);
                for (int i = 0; i < lenOfContent; i++) {
                    switch (encoding) {
                        case 2:
                            set.add(String.valueOf(stream.readInt(2)));
                            break;
                        case 4:
                            set.add(String.valueOf(stream.readInt(4)));
                            break;
                        case 8:
                            set.add(String.valueOf(stream.readLong(8)));
                            break;
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
                KeyStringValueList<String> o10 = new KeyStringValueList<>();
                list = new ArrayList<>();
                int zlbytes = BaseRdbParser.LenHelper.zlbytes(stream);
                int zltail = BaseRdbParser.LenHelper.zltail(stream);
                int zllen = BaseRdbParser.LenHelper.zllen(stream);
                for (int i = 0; i < zllen; i++) {
                    list.add(BaseRdbParser.StringHelper.zipListEntry(stream));
                }
                int zlend = BaseRdbParser.LenHelper.zlend(stream);
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
                zlbytes = BaseRdbParser.LenHelper.zlbytes(stream);
                zltail = BaseRdbParser.LenHelper.zltail(stream);
                zllen = BaseRdbParser.LenHelper.zllen(stream);
                while (zllen > 0) {
                    String element = BaseRdbParser.StringHelper.zipListEntry(stream);
                    zllen--;
                    double score = Double.valueOf(BaseRdbParser.StringHelper.zipListEntry(stream));
                    zllen--;
                    zset.add(new ZSetEntry(element, score));
                }
                zlend = BaseRdbParser.LenHelper.zlend(stream);
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
                zlbytes = BaseRdbParser.LenHelper.zlbytes(stream);
                zltail = BaseRdbParser.LenHelper.zltail(stream);
                zllen = BaseRdbParser.LenHelper.zllen(stream);
                while (zllen > 0) {
                    String field = BaseRdbParser.StringHelper.zipListEntry(stream);
                    zllen--;
                    String value = BaseRdbParser.StringHelper.zipListEntry(stream);
                    zllen--;
                    map.put(field, value);
                }
                zlend = BaseRdbParser.LenHelper.zlend(stream);
                if (zlend != 255) {
                    throw new AssertionError("zlend expected 255 but " + zlend);
                }
                o13.setValueRdbType(rdbtype);
                o13.setValue(map);
                return o13;
            /* rdb version 7*/
            case REDIS_RDB_TYPE_LIST_QUICKLIST:
                len = rdbLoadLen().len;
                KeyStringValueList<ByteArray> o14 = new KeyStringValueList<>();
                List<ByteArray> byteList = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    ByteArray element = rdbLoadRawStringObject();
                    byteList.add(element);
                }
                o14.setValueRdbType(rdbtype);
                o14.setValue(byteList);
                return o14;
            case REDIS_RDB_TYPE_MODULE:
                /* rdb version 8*/
                //|6|6|6|6|6|6|6|6|6|10|
                char[] c = new char[9];
                long moduleid = rdbLoadLen().len;
                keyStringValueModule o6 = new keyStringValueModule();
                for (int i = 0; i < c.length; i++) {
                    c[i] = MODULE_SET[(int) (moduleid & 63)];
                    moduleid >>>= 6;
                }
                String moduleName = new String(c);
                int moduleVersion = (int) (moduleid & 1023);
                ModuleHandler handler = lookupModuleHandler(moduleName,moduleVersion);
                o6.setValueRdbType(rdbtype);
                o6.setValue(handler.rdbLoad(in));
                return o6;
            default:
                throw new AssertionError("Un-except value-type:" + rdbtype);

        }
    }

    private ModuleHandler lookupModuleHandler(String moduleName, int moduleVersion) {
        //TODO
        return null;
    }
}

