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
import com.moilioncircle.redis.replicator.rdb.datatype.ContextKeyValuePair;
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
import com.moilioncircle.redis.replicator.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class DefaultRdbVisitor extends RdbVisitor {

    protected static final Logger logger = LoggerFactory.getLogger(DefaultRdbVisitor.class);

    protected final Replicator replicator;
    protected final RdbValueVisitor valueVisitor;

    public DefaultRdbVisitor(Replicator replicator) {
        this(replicator, new DefaultRdbValueVisitor(replicator));
    }

    public DefaultRdbVisitor(Replicator replicator, RdbValueVisitor valueVisitor) {
        this.replicator = replicator;
        this.valueVisitor = valueVisitor;
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
    public DB applyResizeDB(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        long dbsize = parser.rdbLoadLen().len;
        long expiresSize = parser.rdbLoadLen().len;
        DB db = context.getDb();
        if (db != null) db.setDbsize(dbsize);
        if (db != null) db.setExpires(expiresSize);
        return db;
    }

    @Override
    public Event applyAux(RedisInputStream in, int version) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        String auxKey = Strings.toString(parser.rdbLoadEncodedStringObject().first());
        String auxValue = Strings.toString(parser.rdbLoadEncodedStringObject().first());
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
    public Event applyExpireTime(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        /*
         * ----------------------------
         * FD $unsigned int            # FD indicates "expiry time in seconds". After that, expiry time is read as a 4 byte unsigned int
         * $value-type                 # 1 byte flag indicating the type of value - set, map, sorted set etc.
         * $string-encoded-name         # The name, encoded as a redis string
         * $encoded-value              # The value. Encoding depends on $value-type
         * ----------------------------
         */
        BaseRdbParser parser = new BaseRdbParser(in);
        long expiredSec = parser.rdbLoadTime();
        int type = applyType(in);
        context.setExpiredType(ExpiredType.SECOND);
        context.setExpiredValue(expiredSec);
        context.setValueRdbType(type);
        KeyValuePair<?, ?> kv;
        if (type == RDB_OPCODE_FREQ) {
            kv = (KeyValuePair<?, ?>) applyFreq(in, version, context);
        } else if (type == RDB_OPCODE_IDLE) {
            kv = (KeyValuePair<?, ?>) applyIdle(in, version, context);
        } else {
            kv = rdbLoadObject(in, version, context);
        }
        return kv;
    }

    @Override
    public Event applyExpireTimeMs(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
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
        context.setExpiredType(ExpiredType.MS);
        context.setExpiredValue(expiredMs);
        context.setValueRdbType(type);
        KeyValuePair<?, ?> kv;
        if (type == RDB_OPCODE_FREQ) {
            kv = (KeyValuePair<?, ?>) applyFreq(in, version, context);
        } else if (type == RDB_OPCODE_IDLE) {
            kv = (KeyValuePair<?, ?>) applyIdle(in, version, context);
        } else {
            kv = rdbLoadObject(in, version, context);
        }
        return kv;
    }

    @Override
    public Event applyFreq(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        long lfuFreq = in.read();
        int valueType = applyType(in);
        context.setValueRdbType(valueType);
        context.setEvictType(EvictType.LFU);
        context.setEvictValue(lfuFreq);
        KeyValuePair<?, ?> kv = rdbLoadObject(in, version, context);
        return kv;
    }

    @Override
    public Event applyIdle(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        long lruIdle = parser.rdbLoadLen().len;
        int valueType = applyType(in);
        context.setValueRdbType(valueType);
        context.setEvictType(EvictType.LRU);
        context.setEvictValue(lruIdle);
        KeyValuePair<?, ?> kv = rdbLoadObject(in, version, context);
        return kv;
    }

    @Override
    public Event applyString(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o0 = new KeyStringValueString();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        byte[] val = valueVisitor.applyString(in, version);
        o0.setValueRdbType(RDB_TYPE_STRING);
        o0.setValue(val);
        o0.setKey(key);
        return context.valueOf(o0);
    }

    @Override
    public Event applyList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], List<byte[]>> o1 = new KeyStringValueList();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        List<byte[]> list = valueVisitor.applyList(in, version);
        o1.setValueRdbType(RDB_TYPE_LIST);
        o1.setValue(list);
        o1.setKey(key);
        return context.valueOf(o1);
    }

    @Override
    public Event applySet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Set<byte[]>> o2 = new KeyStringValueSet();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        Set<byte[]> set = valueVisitor.applySet(in, version);
        o2.setValueRdbType(RDB_TYPE_SET);
        o2.setValue(set);
        o2.setKey(key);
        return context.valueOf(o2);
    }

    @Override
    public Event applyZSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Set<ZSetEntry>> o3 = new KeyStringValueZSet();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        Set<ZSetEntry> zset = valueVisitor.applyZSet(in, version);
        o3.setValueRdbType(RDB_TYPE_ZSET);
        o3.setValue(zset);
        o3.setKey(key);
        return context.valueOf(o3);
    }

    @Override
    public Event applyZSet2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Set<ZSetEntry>> o5 = new KeyStringValueZSet();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        Set<ZSetEntry> zset = valueVisitor.applyZSet2(in, version);
        o5.setValueRdbType(RDB_TYPE_ZSET_2);
        o5.setValue(zset);
        o5.setKey(key);
        return context.valueOf(o5);
    }

    @Override
    public Event applyHash(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Map<byte[], byte[]>> o4 = new KeyStringValueHash();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        ByteArrayMap map = valueVisitor.applyHash(in, version);
        o4.setValueRdbType(RDB_TYPE_HASH);
        o4.setValue(map);
        o4.setKey(key);
        return context.valueOf(o4);
    }

    @Override
    public Event applyHashZipMap(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Map<byte[], byte[]>> o9 = new KeyStringValueHash();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        ByteArrayMap map = valueVisitor.applyHashZipMap(in, version);
        o9.setValueRdbType(RDB_TYPE_HASH_ZIPMAP);
        o9.setValue(map);
        o9.setKey(key);
        return context.valueOf(o9);
    }

    @Override
    public Event applyListZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], List<byte[]>> o10 = new KeyStringValueList();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        List<byte[]> list = valueVisitor.applyListZipList(in, version);
        o10.setValueRdbType(RDB_TYPE_LIST_ZIPLIST);
        o10.setValue(list);
        o10.setKey(key);
        return context.valueOf(o10);
    }

    @Override
    public Event applySetIntSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Set<byte[]>> o11 = new KeyStringValueSet();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        Set<byte[]> set = valueVisitor.applySetIntSet(in, version);
        o11.setValueRdbType(RDB_TYPE_SET_INTSET);
        o11.setValue(set);
        o11.setKey(key);
        return context.valueOf(o11);
    }

    @Override
    public Event applyZSetZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Set<ZSetEntry>> o12 = new KeyStringValueZSet();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        Set<ZSetEntry> zset = valueVisitor.applyZSetZipList(in, version);
        o12.setValueRdbType(RDB_TYPE_ZSET_ZIPLIST);
        o12.setValue(zset);
        o12.setKey(key);
        return context.valueOf(o12);
    }

    @Override
    public Event applyHashZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Map<byte[], byte[]>> o13 = new KeyStringValueHash();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        ByteArrayMap map = valueVisitor.applyHashZipList(in, version);
        o13.setValueRdbType(RDB_TYPE_HASH_ZIPLIST);
        o13.setValue(map);
        o13.setKey(key);
        return context.valueOf(o13);
    }

    @Override
    public Event applyListQuickList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], List<byte[]>> o14 = new KeyStringValueList();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        List<byte[]> list = valueVisitor.applyListQuickList(in, version);
        o14.setValueRdbType(RDB_TYPE_LIST_QUICKLIST);
        o14.setValue(list);
        o14.setKey(key);
        return context.valueOf(o14);
    }

    @Override
    public Event applyModule(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Module> o6 = new KeyStringValueModule();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        Module module = valueVisitor.applyModule(in, version);
        o6.setValueRdbType(RDB_TYPE_MODULE);
        o6.setValue(module);
        o6.setKey(key);
        return context.valueOf(o6);
    }

    @Override
    public Event applyModule2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Module> o7 = new KeyStringValueModule();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        Module module = valueVisitor.applyModule2(in, version);
        o7.setValueRdbType(RDB_TYPE_MODULE_2);
        o7.setValue(module);
        o7.setKey(key);
        return context.valueOf(o7);
    }

    @Override
    @SuppressWarnings("resource")
    public Event applyStreamListPacks(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], Stream> o15 = new KeyStringValueStream();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        Stream stream = valueVisitor.applyStreamListPacks(in, version);
        o15.setValueRdbType(RDB_TYPE_STREAM_LISTPACKS);
        o15.setValue(stream);
        o15.setKey(key);
        return context.valueOf(o15);
    }

    protected ModuleParser<? extends Module> lookupModuleParser(String moduleName, int moduleVersion) {
        return replicator.getModuleParser(moduleName, moduleVersion);
    }

    protected KeyValuePair<?, ?> rdbLoadObject(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        /*
         * ----------------------------
         * $value-type                 # This name value pair doesn't have an expiry. $value_type guaranteed != to FD, FC, FE and FF
         * $string-encoded-name
         * $encoded-value
         * ----------------------------
         */
        int valueType = context.getValueRdbType();
        switch (valueType) {
            case RDB_TYPE_STRING:
                return (KeyValuePair<?, ?>) applyString(in, version, context);
            case RDB_TYPE_LIST:
                return (KeyValuePair<?, ?>) applyList(in, version, context);
            case RDB_TYPE_SET:
                return (KeyValuePair<?, ?>) applySet(in, version, context);
            case RDB_TYPE_ZSET:
                return (KeyValuePair<?, ?>) applyZSet(in, version, context);
            case RDB_TYPE_ZSET_2:
                return (KeyValuePair<?, ?>) applyZSet2(in, version, context);
            case RDB_TYPE_HASH:
                return (KeyValuePair<?, ?>) applyHash(in, version, context);
            case RDB_TYPE_HASH_ZIPMAP:
                return (KeyValuePair<?, ?>) applyHashZipMap(in, version, context);
            case RDB_TYPE_LIST_ZIPLIST:
                return (KeyValuePair<?, ?>) applyListZipList(in, version, context);
            case RDB_TYPE_SET_INTSET:
                return (KeyValuePair<?, ?>) applySetIntSet(in, version, context);
            case RDB_TYPE_ZSET_ZIPLIST:
                return (KeyValuePair<?, ?>) applyZSetZipList(in, version, context);
            case RDB_TYPE_HASH_ZIPLIST:
                return (KeyValuePair<?, ?>) applyHashZipList(in, version, context);
            case RDB_TYPE_LIST_QUICKLIST:
                return (KeyValuePair<?, ?>) applyListQuickList(in, version, context);
            case RDB_TYPE_MODULE:
                return (KeyValuePair<?, ?>) applyModule(in, version, context);
            case RDB_TYPE_MODULE_2:
                return (KeyValuePair<?, ?>) applyModule2(in, version, context);
            case RDB_TYPE_STREAM_LISTPACKS:
                return (KeyValuePair<?, ?>) applyStreamListPacks(in, version, context);
            default:
                throw new AssertionError("unexpected value type:" + valueType);
        }
    }
}
