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

package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.AuxField;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;
import com.moilioncircle.redis.replicator.rdb.datatype.EvictType;
import com.moilioncircle.redis.replicator.rdb.datatype.ExpiredType;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueHash;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueList;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueModule;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueSet;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueStream;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueZSet;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.datatype.Stream;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;
import com.moilioncircle.redis.replicator.rdb.skip.SkipRdbParser;
import com.moilioncircle.redis.replicator.util.ByteArrayMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import static com.moilioncircle.redis.replicator.Constants.MODULE_SET;
import static com.moilioncircle.redis.replicator.Constants.RDB_LOAD_NONE;
import static com.moilioncircle.redis.replicator.Constants.RDB_MODULE_OPCODE_EOF;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_FREQ;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_IDLE;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_ZIPLIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_ZIPMAP;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST_QUICKLIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST_ZIPLIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_MODULE;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_MODULE_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET_INTSET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_STREAM_LISTPACKS;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_STRING;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_ZIPLIST;
import static com.moilioncircle.redis.replicator.Constants.STREAM_ITEM_FLAG_DELETED;
import static com.moilioncircle.redis.replicator.Constants.STREAM_ITEM_FLAG_SAMEFIELDS;
import static com.moilioncircle.redis.replicator.rdb.BaseRdbParser.StringHelper.listPackEntry;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class DefaultRdbVisitor extends RdbVisitor {
    
    protected static final Logger logger = LoggerFactory.getLogger(DefaultRdbVisitor.class);
    
    protected static final Comparator<Stream.ID> STREAM_COMPARATOR = Stream.ID.comparator();
    
    protected final Replicator replicator;
    
    public DefaultRdbVisitor(final Replicator replicator) {
        this.replicator = replicator;
    }
    
    @Override
    public String applyMagic(RedisInputStream in) throws IOException {
        String magic = BaseRdbParser.StringHelper.str(in, 5);//REDIS
        if (!magic.equals("REDIS")) {
            throw new UnsupportedOperationException("can't read MAGIC STRING [REDIS] ,value:" + magic);
        }
        return magic;
    }
    
    @Override
    public int applyVersion(RedisInputStream in) throws IOException {
        int version = parseInt(BaseRdbParser.StringHelper.str(in, 4));
        if (version < 2 || version > 9) {
            throw new UnsupportedOperationException(String.valueOf("can't handle RDB format version " + version));
        }
        return version;
    }
    
    @Override
    public int applyType(RedisInputStream in) throws IOException {
        return in.read();
    }
    
    @Override
    public DB applySelectDB(RedisInputStream in, int version) throws IOException {
        /*
         * ----------------------------
         * FE $length-encoding         # Previous db ends, next db starts. Database number read using length encoding.
         * ----------------------------
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        long dbNumber = parser.rdbLoadLen().len;
        return new DB(dbNumber);
    }
    
    @Override
    public DB applyResizeDB(RedisInputStream in, DB db, int version) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        long dbsize = parser.rdbLoadLen().len;
        long expiresSize = parser.rdbLoadLen().len;
        if (db != null) db.setDbsize(dbsize);
        if (db != null) db.setExpires(expiresSize);
        return db;
    }
    
    @Override
    public Event applyAux(RedisInputStream in, int version) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        String auxKey = new String(parser.rdbLoadEncodedStringObject().first(), UTF_8);
        String auxValue = new String(parser.rdbLoadEncodedStringObject().first(), UTF_8);
        if (!auxKey.startsWith("%")) {
            if (logger.isInfoEnabled()) {
                logger.info("RDB {}: {}", auxKey, auxValue);
            }
            if (auxKey.equals("repl-id")) replicator.getConfiguration().setReplId(auxValue);
            if (auxKey.equals("repl-offset")) replicator.getConfiguration().setReplOffset(parseLong(auxValue));
            if (auxKey.equals("repl-stream-db")) replicator.getConfiguration().setReplStreamDB(parseInt(auxValue));
            return new AuxField(auxKey, auxValue);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("unrecognized RDB AUX field: {}, value: {}", auxKey, auxValue);
            }
            return null;
        }
    }
    
    @Override
    public Event applyModuleAux(RedisInputStream in, int version) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadLen();
        parser.rdbLoadCheckModuleValue();
        return null;
    }
    
    @Override
    public long applyEof(RedisInputStream in, int version) throws IOException {
        /*
         * ----------------------------
         * ...                         # Key value pairs for this database, additonal database
         * FF                          ## End of RDB file indicator
         * 8 byte checksum             ## CRC 64 checksum of the entire file.
         * ----------------------------
         */
        if (version >= 5) return in.readLong(8);
        return 0L;
    }
    
    @Override
    public Event applyExpireTime(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * ----------------------------
         * FD $unsigned int            # FD indicates "expiry time in seconds". After that, expiry time is read as a 4 byte unsigned int
         * $value-type                 # 1 byte flag indicating the type of value - set, map, sorted set etc.
         * $string-encoded-name         # The name, encoded as a redis string
         * $encoded-value              # The value. Encoding depends on $value-type
         * ----------------------------
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        int expiredSec = parser.rdbLoadTime();
        int type = applyType(in);
        KeyValuePair<?> kv;
        if (type == RDB_OPCODE_FREQ) {
            kv = (KeyValuePair<?>) applyFreq(in, db, version);
        } else if (type == RDB_OPCODE_IDLE) {
            kv = (KeyValuePair<?>) applyIdle(in, db, version);
        } else {
            kv = rdbLoadObject(in, db, type, version);
        }
        kv.setExpiredType(ExpiredType.SECOND);
        kv.setExpiredValue((long) expiredSec);
        return kv;
    }
    
    @Override
    public Event applyExpireTimeMs(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * ----------------------------
         * FC $unsigned long           # FC indicates "expiry time in ms". After that, expiry time is read as a 8 byte unsigned long
         * $value-type                 # 1 byte flag indicating the type of value - set, map, sorted set etc.
         * $string-encoded-name         # The name, encoded as a redis string
         * $encoded-value              # The value. Encoding depends on $value-type
         * ----------------------------
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        long expiredMs = parser.rdbLoadMillisecondTime();
        int type = applyType(in);
        KeyValuePair<?> kv;
        if (type == RDB_OPCODE_FREQ) {
            kv = (KeyValuePair<?>) applyFreq(in, db, version);
        } else if (type == RDB_OPCODE_IDLE) {
            kv = (KeyValuePair<?>) applyIdle(in, db, version);
        } else {
            kv = rdbLoadObject(in, db, type, version);
        }
        kv.setExpiredType(ExpiredType.MS);
        kv.setExpiredValue(expiredMs);
        return kv;
    }
    
    @Override
    public Event applyFreq(RedisInputStream in, DB db, int version) throws IOException {
        long lfuFreq = in.read();
        int valueType = applyType(in);
        KeyValuePair<?> kv = rdbLoadObject(in, db, valueType, version);
        kv.setEvictType(EvictType.LFU);
        kv.setEvictValue(lfuFreq);
        return kv;
    }
    
    @Override
    public Event applyIdle(RedisInputStream in, DB db, int version) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        long lruIdle = parser.rdbLoadLen().len;
        int valueType = applyType(in);
        KeyValuePair<?> kv = rdbLoadObject(in, db, valueType, version);
        kv.setEvictType(EvictType.LRU);
        kv.setEvictValue(lruIdle);
        return kv;
    }
    
    @Override
    public Event applyString(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |       <content>       |
         * |    string contents    |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueString o0 = new KeyStringValueString();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        byte[] val = parser.rdbLoadEncodedStringObject().first();
        o0.setValueRdbType(RDB_TYPE_STRING);
        o0.setValue(new String(val, UTF_8));
        o0.setRawValue(val);
        o0.setDb(db);
        o0.setKey(new String(key, UTF_8));
        o0.setRawKey(key);
        return o0;
    }
    
    @Override
    public Event applyList(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |    <len>     |       <content>       |
         * | 1 or 5 bytes |    string contents    |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueList o1 = new KeyStringValueList();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        long len = parser.rdbLoadLen().len;
        List<String> list = new ArrayList<>();
        List<byte[]> rawList = new ArrayList<>();
        while (len > 0) {
            byte[] element = parser.rdbLoadEncodedStringObject().first();
            list.add(new String(element, UTF_8));
            rawList.add(element);
            len--;
        }
        o1.setValueRdbType(RDB_TYPE_LIST);
        o1.setValue(list);
        o1.setRawValue(rawList);
        o1.setDb(db);
        o1.setKey(new String(key, UTF_8));
        o1.setRawKey(key);
        return o1;
    }
    
    @Override
    public Event applySet(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |    <len>     |       <content>       |
         * | 1 or 5 bytes |    string contents    |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueSet o2 = new KeyStringValueSet();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        long len = parser.rdbLoadLen().len;
        Set<String> set = new LinkedHashSet<>();
        Set<byte[]> rawSet = new LinkedHashSet<>();
        while (len > 0) {
            byte[] element = parser.rdbLoadEncodedStringObject().first();
            set.add(new String(element, UTF_8));
            rawSet.add(element);
            len--;
        }
        o2.setValueRdbType(RDB_TYPE_SET);
        o2.setValue(set);
        o2.setRawValue(rawSet);
        o2.setDb(db);
        o2.setKey(new String(key, UTF_8));
        o2.setRawKey(key);
        return o2;
    }
    
    @Override
    public Event applyZSet(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |    <len>     |       <content>       |        <score>       |
         * | 1 or 5 bytes |    string contents    |    double content    |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueZSet o3 = new KeyStringValueZSet();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        long len = parser.rdbLoadLen().len;
        Set<ZSetEntry> zset = new LinkedHashSet<>();
        while (len > 0) {
            byte[] element = parser.rdbLoadEncodedStringObject().first();
            double score = parser.rdbLoadDoubleValue();
            zset.add(new ZSetEntry(new String(element, UTF_8), score, element));
            len--;
        }
        o3.setValueRdbType(RDB_TYPE_ZSET);
        o3.setValue(zset);
        o3.setDb(db);
        o3.setKey(new String(key, UTF_8));
        o3.setRawKey(key);
        return o3;
    }
    
    @Override
    public Event applyZSet2(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |    <len>     |       <content>       |        <score>       |
         * | 1 or 5 bytes |    string contents    |    binary double     |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueZSet o5 = new KeyStringValueZSet();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        /* rdb version 8*/
        long len = parser.rdbLoadLen().len;
        Set<ZSetEntry> zset = new LinkedHashSet<>();
        while (len > 0) {
            byte[] element = parser.rdbLoadEncodedStringObject().first();
            double score = parser.rdbLoadBinaryDoubleValue();
            zset.add(new ZSetEntry(new String(element, UTF_8), score, element));
            len--;
        }
        o5.setValueRdbType(RDB_TYPE_ZSET_2);
        o5.setValue(zset);
        o5.setDb(db);
        o5.setKey(new String(key, UTF_8));
        o5.setRawKey(key);
        return o5;
    }
    
    @Override
    public Event applyHash(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |    <len>     |       <content>       |
         * | 1 or 5 bytes |    string contents    |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueHash o4 = new KeyStringValueHash();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        long len = parser.rdbLoadLen().len;
        Map<String, String> map = new LinkedHashMap<>();
        ByteArrayMap<byte[]> rawMap = new ByteArrayMap<>();
        while (len > 0) {
            byte[] field = parser.rdbLoadEncodedStringObject().first();
            byte[] value = parser.rdbLoadEncodedStringObject().first();
            map.put(new String(field, UTF_8), new String(value, UTF_8));
            rawMap.put(field, value);
            len--;
        }
        o4.setValueRdbType(RDB_TYPE_HASH);
        o4.setValue(map);
        o4.setRawValue(rawMap);
        o4.setDb(db);
        o4.setKey(new String(key, UTF_8));
        o4.setRawKey(key);
        return o4;
    }
    
    @Override
    public Event applyHashZipMap(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |<zmlen> |   <len>     |"foo"    |    <len>   | <free> |   "bar" |<zmend> |
         * | 1 byte | 1 or 5 byte | content |1 or 5 byte | 1 byte | content | 1 byte |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueHash o9 = new KeyStringValueHash();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        Map<String, String> map = new LinkedHashMap<>();
        ByteArrayMap<byte[]> rawMap = new ByteArrayMap<>();
        BaseRdbParser.LenHelper.zmlen(stream); // zmlen
        while (true) {
            int zmEleLen = BaseRdbParser.LenHelper.zmElementLen(stream);
            if (zmEleLen == 255) {
                o9.setValueRdbType(RDB_TYPE_HASH_ZIPMAP);
                o9.setValue(map);
                o9.setRawValue(rawMap);
                o9.setDb(db);
                o9.setKey(new String(key, UTF_8));
                o9.setRawKey(key);
                return o9;
            }
            byte[] field = BaseRdbParser.StringHelper.bytes(stream, zmEleLen);
            zmEleLen = BaseRdbParser.LenHelper.zmElementLen(stream);
            if (zmEleLen == 255) {
                //value is null
                map.put(new String(field, UTF_8), null);
                rawMap.put(field, null);
                o9.setValueRdbType(RDB_TYPE_HASH_ZIPMAP);
                o9.setValue(map);
                o9.setRawValue(rawMap);
                o9.setDb(db);
                o9.setKey(new String(key, UTF_8));
                o9.setRawKey(key);
                return o9;
            }
            int free = BaseRdbParser.LenHelper.free(stream);
            byte[] value = BaseRdbParser.StringHelper.bytes(stream, zmEleLen);
            BaseRdbParser.StringHelper.skip(stream, free);
            map.put(new String(field, UTF_8), new String(value, UTF_8));
            rawMap.put(field, value);
        }
    }
    
    @Override
    public Event applyListZipList(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |<zlbytes>| <zltail>| <zllen>| <entry> ...<entry> | <zlend>|
         * | 4 bytes | 4 bytes | 2bytes | zipListEntry ...   | 1byte  |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueList o10 = new KeyStringValueList();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        
        List<String> list = new ArrayList<>();
        List<byte[]> rawList = new ArrayList<>();
        BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
        BaseRdbParser.LenHelper.zltail(stream); // zltail
        int zllen = BaseRdbParser.LenHelper.zllen(stream);
        for (int i = 0; i < zllen; i++) {
            byte[] e = BaseRdbParser.StringHelper.zipListEntry(stream);
            list.add(new String(e, UTF_8));
            rawList.add(e);
        }
        int zlend = BaseRdbParser.LenHelper.zlend(stream);
        if (zlend != 255) {
            throw new AssertionError("zlend expect 255 but " + zlend);
        }
        o10.setValueRdbType(RDB_TYPE_LIST_ZIPLIST);
        o10.setValue(list);
        o10.setRawValue(rawList);
        o10.setDb(db);
        o10.setKey(new String(key, UTF_8));
        o10.setRawKey(key);
        return o10;
    }
    
    @Override
    public Event applySetIntSet(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |<encoding>| <length-of-contents>|              <contents>                            |
         * | 4 bytes  |            4 bytes  | 2 bytes element| 4 bytes element | 8 bytes element |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueSet o11 = new KeyStringValueSet();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        
        Set<String> set = new LinkedHashSet<>();
        Set<byte[]> rawSet = new LinkedHashSet<>();
        int encoding = BaseRdbParser.LenHelper.encoding(stream);
        long lenOfContent = BaseRdbParser.LenHelper.lenOfContent(stream);
        for (long i = 0; i < lenOfContent; i++) {
            switch (encoding) {
                case 2:
                    String element = String.valueOf(stream.readInt(2));
                    set.add(element);
                    rawSet.add(element.getBytes());
                    break;
                case 4:
                    element = String.valueOf(stream.readInt(4));
                    set.add(element);
                    rawSet.add(element.getBytes());
                    break;
                case 8:
                    element = String.valueOf(stream.readLong(8));
                    set.add(element);
                    rawSet.add(element.getBytes());
                    break;
                default:
                    throw new AssertionError("expect encoding [2,4,8] but:" + encoding);
            }
        }
        o11.setValueRdbType(RDB_TYPE_SET_INTSET);
        o11.setValue(set);
        o11.setRawValue(rawSet);
        o11.setDb(db);
        o11.setKey(new String(key, UTF_8));
        o11.setRawKey(key);
        return o11;
    }
    
    @Override
    public Event applyZSetZipList(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |<zlbytes>| <zltail>| <zllen>| <entry> ...<entry> | <zlend>|
         * | 4 bytes | 4 bytes | 2bytes | zipListEntry ...   | 1byte  |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueZSet o12 = new KeyStringValueZSet();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        
        Set<ZSetEntry> zset = new LinkedHashSet<>();
        BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
        BaseRdbParser.LenHelper.zltail(stream); // zltail
        int zllen = BaseRdbParser.LenHelper.zllen(stream);
        while (zllen > 0) {
            byte[] element = BaseRdbParser.StringHelper.zipListEntry(stream);
            zllen--;
            double score = Double.valueOf(new String(BaseRdbParser.StringHelper.zipListEntry(stream), UTF_8));
            zllen--;
            zset.add(new ZSetEntry(new String(element, UTF_8), score, element));
        }
        int zlend = BaseRdbParser.LenHelper.zlend(stream);
        if (zlend != 255) {
            throw new AssertionError("zlend expect 255 but " + zlend);
        }
        o12.setValueRdbType(RDB_TYPE_ZSET_ZIPLIST);
        o12.setValue(zset);
        o12.setDb(db);
        o12.setKey(new String(key, UTF_8));
        o12.setRawKey(key);
        return o12;
    }
    
    @Override
    public Event applyHashZipList(RedisInputStream in, DB db, int version) throws IOException {
        /*
         * |<zlbytes>| <zltail>| <zllen>| <entry> ...<entry> | <zlend>|
         * | 4 bytes | 4 bytes | 2bytes | zipListEntry ...   | 1byte  |
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueHash o13 = new KeyStringValueHash();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        
        Map<String, String> map = new LinkedHashMap<>();
        ByteArrayMap<byte[]> rawMap = new ByteArrayMap<>();
        BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
        BaseRdbParser.LenHelper.zltail(stream); // zltail
        int zllen = BaseRdbParser.LenHelper.zllen(stream);
        while (zllen > 0) {
            byte[] field = BaseRdbParser.StringHelper.zipListEntry(stream);
            zllen--;
            byte[] value = BaseRdbParser.StringHelper.zipListEntry(stream);
            zllen--;
            map.put(new String(field, UTF_8), new String(value, UTF_8));
            rawMap.put(field, value);
        }
        int zlend = BaseRdbParser.LenHelper.zlend(stream);
        if (zlend != 255) {
            throw new AssertionError("zlend expect 255 but " + zlend);
        }
        o13.setValueRdbType(RDB_TYPE_HASH_ZIPLIST);
        o13.setValue(map);
        o13.setRawValue(rawMap);
        o13.setDb(db);
        o13.setKey(new String(key, UTF_8));
        o13.setRawKey(key);
        return o13;
    }
    
    @Override
    public Event applyListQuickList(RedisInputStream in, DB db, int version) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueList o14 = new KeyStringValueList();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        long len = parser.rdbLoadLen().len;
        List<String> list = new ArrayList<>();
        List<byte[]> rawList = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            RedisInputStream stream = new RedisInputStream(parser.rdbGenericLoadStringObject(RDB_LOAD_NONE));
    
            BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
            BaseRdbParser.LenHelper.zltail(stream); // zltail
            int zllen = BaseRdbParser.LenHelper.zllen(stream);
            for (int j = 0; j < zllen; j++) {
                byte[] e = BaseRdbParser.StringHelper.zipListEntry(stream);
                list.add(new String(e, UTF_8));
                rawList.add(e);
            }
            int zlend = BaseRdbParser.LenHelper.zlend(stream);
            if (zlend != 255) {
                throw new AssertionError("zlend expect 255 but " + zlend);
            }
        }
        o14.setValueRdbType(RDB_TYPE_LIST_QUICKLIST);
        o14.setValue(list);
        o14.setRawValue(rawList);
        o14.setDb(db);
        o14.setKey(new String(key, UTF_8));
        o14.setRawKey(key);
        return o14;
    }
    
    @Override
    public Event applyModule(RedisInputStream in, DB db, int version) throws IOException {
        //|6|6|6|6|6|6|6|6|6|10|
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueModule o6 = new KeyStringValueModule();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        char[] c = new char[9];
        long moduleid = parser.rdbLoadLen().len;
        for (int i = 0; i < c.length; i++) {
            c[i] = MODULE_SET[(int) (moduleid >>> (10 + (c.length - 1 - i) * 6) & 63)];
        }
        String moduleName = new String(c);
        int moduleVersion = (int) (moduleid & 1023);
        ModuleParser<? extends Module> moduleParser = lookupModuleParser(moduleName, moduleVersion);
        if (moduleParser == null) {
            throw new NoSuchElementException("module parser[" + moduleName + ", " + moduleVersion + "] not register. rdb type: [RDB_TYPE_MODULE]");
        }
        o6.setValueRdbType(RDB_TYPE_MODULE);
        o6.setValue(moduleParser.parse(in, 1));
        o6.setDb(db);
        o6.setKey(new String(key, UTF_8));
        o6.setRawKey(key);
        return o6;
    }
    
    @Override
    public Event applyModule2(RedisInputStream in, DB db, int version) throws IOException {
        //|6|6|6|6|6|6|6|6|6|10|
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueModule o7 = new KeyStringValueModule();
        byte[] rawKey = parser.rdbLoadEncodedStringObject().first();
        String key = new String(rawKey, UTF_8);
        char[] c = new char[9];
        long moduleid = parser.rdbLoadLen().len;
        for (int i = 0; i < c.length; i++) {
            c[i] = MODULE_SET[(int) (moduleid >>> (10 + (c.length - 1 - i) * 6) & 63)];
        }
        String moduleName = new String(c);
        int moduleVersion = (int) (moduleid & 1023);
        ModuleParser<? extends Module> moduleParser = lookupModuleParser(moduleName, moduleVersion);
        Module module = null;
        if (moduleParser == null) {
            logger.warn("module parser[{}, {}] not register. rdb type: [RDB_TYPE_MODULE_2]. key: [{}]. module parse skipped.", moduleName, moduleVersion, key);
            SkipRdbParser skipRdbParser = new SkipRdbParser(in);
            skipRdbParser.rdbLoadCheckModuleValue();
        } else {
            module = moduleParser.parse(in, 2);
            long eof = parser.rdbLoadLen().len;
            if (eof != RDB_MODULE_OPCODE_EOF) {
                throw new UnsupportedOperationException("The RDB file contains module data for the module '" + moduleName + "' that is not terminated by the proper module value EOF marker");
            }
        }
        o7.setValueRdbType(RDB_TYPE_MODULE_2);
        o7.setValue(module);
        o7.setDb(db);
        o7.setKey(new String(rawKey, UTF_8));
        o7.setRawKey(rawKey);
        return o7;
    }
    
    protected ModuleParser<? extends Module> lookupModuleParser(String moduleName, int moduleVersion) {
        return replicator.getModuleParser(moduleName, moduleVersion);
    }
    
    @Override
    @SuppressWarnings("resource")
    public Event applyStreamListPacks(RedisInputStream in, DB db, int version) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyStringValueStream o15 = new KeyStringValueStream();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        
        Stream stream = new Stream();
        
        // Entries
        NavigableMap<Stream.ID, Stream.Entry> entries = new TreeMap<>(STREAM_COMPARATOR);
        long listPacks = parser.rdbLoadLen().len;
        while (listPacks-- > 0) {
            RedisInputStream rawId = new RedisInputStream(parser.rdbLoadPlainStringObject());
            Stream.ID baseId = new Stream.ID(rawId.readLong(8, false), rawId.readLong(8, false));
            RedisInputStream listPack = new RedisInputStream(parser.rdbLoadPlainStringObject());
            listPack.skip(4); // total-bytes
            listPack.skip(2); // num-elements
            /*
             * Master entry
             * +-------+---------+------------+---------+--/--+---------+---------+-+
             * | count | deleted | num-fields | field_1 | field_2 | ... | field_N |0|
             * +-------+---------+------------+---------+--/--+---------+---------+-+
             */
            long count = Long.parseLong(new String(listPackEntry(listPack), UTF_8)); // count
            long deleted = Long.parseLong(new String(listPackEntry(listPack), UTF_8)); // deleted
            int numFields = Integer.parseInt(new String(listPackEntry(listPack), UTF_8)); // num-fields
            byte[][] tempFields = new byte[numFields][];
            for (int i = 0; i < numFields; i++) {
                tempFields[i] = listPackEntry(listPack);
            }
            listPackEntry(listPack); // 0
    
            long total = count + deleted;
            while (total-- > 0) {
                Map<String, String> fields = new LinkedHashMap<>();
                Map<byte[], byte[]> rawFields = new ByteArrayMap<>();
                /*
                 * FLAG
                 * +-----+--------+
                 * |flags|entry-id|
                 * +-----+--------+
                 */
                int flag = Integer.parseInt(new String(listPackEntry(listPack), UTF_8));
                long ms = Long.parseLong(new String(listPackEntry(listPack), UTF_8));
                long seq = Long.parseLong(new String(listPackEntry(listPack), UTF_8));
                Stream.ID id = baseId.delta(ms, seq);
                boolean delete = (flag & STREAM_ITEM_FLAG_DELETED) != 0;
                if ((flag & STREAM_ITEM_FLAG_SAMEFIELDS) != 0) {
                    /*
                     * SAMEFIELD
                     * +-------+-/-+-------+--------+
                     * |value-1|...|value-N|lp-count|
                     * +-------+-/-+-------+--------+
                     */
                    for (int i = 0; i < numFields; i++) {
                        byte[] rawValue = listPackEntry(listPack);
                        String value = new String(rawValue, UTF_8);
                        byte[] rawField = tempFields[i];
                        String field = new String(rawField, UTF_8);
                        fields.put(field, value);
                        rawFields.put(rawField, rawValue);
                    }
                    entries.put(id, new Stream.Entry(id, delete, fields, rawFields));
                } else {
                    /*
                     * NONEFIELD
                     * +----------+-------+-------+-/-+-------+-------+--------+
                     * |num-fields|field-1|value-1|...|field-N|value-N|lp-count|
                     * +----------+-------+-------+-/-+-------+-------+--------+
                     */
                    numFields = Integer.parseInt(new String(listPackEntry(listPack), UTF_8));
                    for (int i = 0; i < numFields; i++) {
                        byte[] rawField = listPackEntry(listPack);
                        String field = new String(rawField, UTF_8);
                        byte[] rawValue = listPackEntry(listPack);
                        String value = new String(rawValue, UTF_8);
                        fields.put(field, value);
                        rawFields.put(rawField, rawValue);
                    }
                    entries.put(id, new Stream.Entry(id, delete, fields, rawFields));
                }
                listPackEntry(listPack); // lp-count
            }
            int lpend = listPack.read(); // lp-end
            if (lpend != 255) {
                throw new AssertionError("listpack expect 255 but " + lpend);
            }
        }
    
        long length = parser.rdbLoadLen().len;
        Stream.ID lastId = new Stream.ID(parser.rdbLoadLen().len, parser.rdbLoadLen().len);
        
        // Group
        List<Stream.Group> groups = new ArrayList<>();
        long groupCount = parser.rdbLoadLen().len;
        while (groupCount-- > 0) {
            Stream.Group group = new Stream.Group();
            byte[] groupName = parser.rdbLoadPlainStringObject().first();
            Stream.ID groupLastId = new Stream.ID(parser.rdbLoadLen().len, parser.rdbLoadLen().len);
    
            // Group PEL
            NavigableMap<Stream.ID, Stream.Nack> groupPendingEntries = new TreeMap<>(STREAM_COMPARATOR);
            long globalPel = parser.rdbLoadLen().len;
            while (globalPel-- > 0) {
                Stream.ID rawId = new Stream.ID(in.readLong(8, false), in.readLong(8, false));
                long deliveryTime = parser.rdbLoadMillisecondTime();
                long deliveryCount = parser.rdbLoadLen().len;
                groupPendingEntries.put(rawId, new Stream.Nack(rawId, null, deliveryTime, deliveryCount));
            }
    
            // Consumer
            List<Stream.Consumer> consumers = new ArrayList<>();
            long consumerCount = parser.rdbLoadLen().len;
            while (consumerCount-- > 0) {
                Stream.Consumer consumer = new Stream.Consumer();
                byte[] consumerName = parser.rdbLoadPlainStringObject().first();
                long seenTime = parser.rdbLoadMillisecondTime();
    
                // Consumer PEL
                NavigableMap<Stream.ID, Stream.Nack> consumerPendingEntries = new TreeMap<>(STREAM_COMPARATOR);
                long pel = parser.rdbLoadLen().len;
                while (pel-- > 0) {
                    Stream.ID rawId = new Stream.ID(in.readLong(8, false), in.readLong(8, false));
                    Stream.Nack nack = groupPendingEntries.get(rawId);
                    nack.setConsumer(consumer);
                    consumerPendingEntries.put(rawId, nack);
                }
    
                consumer.setName(new String(consumerName, UTF_8));
                consumer.setSeenTime(seenTime);
                consumer.setPendingEntries(consumerPendingEntries);
                consumer.setRawName(consumerName);
                consumers.add(consumer);
            }

            group.setName(new String(groupName, UTF_8));
            group.setLastId(groupLastId);
            group.setPendingEntries(groupPendingEntries);
            group.setConsumers(consumers);
            group.setRawName(groupName);
            groups.add(group);
        }
        
        stream.setLastId(lastId);
        stream.setEntries(entries);
        stream.setLength(length);
        stream.setGroups(groups);
        
        o15.setValueRdbType(RDB_TYPE_STREAM_LISTPACKS);
        o15.setDb(db);
        o15.setValue(stream);
        o15.setKey(new String(key, UTF_8));
        o15.setRawKey(key);
        return o15;
    }
    
    protected KeyValuePair<?> rdbLoadObject(RedisInputStream in, DB db, int valueType, int version) throws IOException {
        /*
         * ----------------------------
         * $value-type                 # This name value pair doesn't have an expiry. $value_type guaranteed != to FD, FC, FE and FF
         * $string-encoded-name
         * $encoded-value
         * ----------------------------
         */
        switch (valueType) {
            case RDB_TYPE_STRING:
                return (KeyValuePair<?>) applyString(in, db, version);
            case RDB_TYPE_LIST:
                return (KeyValuePair<?>) applyList(in, db, version);
            case RDB_TYPE_SET:
                return (KeyValuePair<?>) applySet(in, db, version);
            case RDB_TYPE_ZSET:
                return (KeyValuePair<?>) applyZSet(in, db, version);
            case RDB_TYPE_ZSET_2:
                return (KeyValuePair<?>) applyZSet2(in, db, version);
            case RDB_TYPE_HASH:
                return (KeyValuePair<?>) applyHash(in, db, version);
            case RDB_TYPE_HASH_ZIPMAP:
                return (KeyValuePair<?>) applyHashZipMap(in, db, version);
            case RDB_TYPE_LIST_ZIPLIST:
                return (KeyValuePair<?>) applyListZipList(in, db, version);
            case RDB_TYPE_SET_INTSET:
                return (KeyValuePair<?>) applySetIntSet(in, db, version);
            case RDB_TYPE_ZSET_ZIPLIST:
                return (KeyValuePair<?>) applyZSetZipList(in, db, version);
            case RDB_TYPE_HASH_ZIPLIST:
                return (KeyValuePair<?>) applyHashZipList(in, db, version);
            case RDB_TYPE_LIST_QUICKLIST:
                return (KeyValuePair<?>) applyListQuickList(in, db, version);
            case RDB_TYPE_MODULE:
                return (KeyValuePair<?>) applyModule(in, db, version);
            case RDB_TYPE_MODULE_2:
                return (KeyValuePair<?>) applyModule2(in, db, version);
            case RDB_TYPE_STREAM_LISTPACKS:
                return (KeyValuePair<?>) applyStreamListPacks(in, db, version);
            default:
                throw new AssertionError("unexpected value type:" + valueType);
        }
    }
}
