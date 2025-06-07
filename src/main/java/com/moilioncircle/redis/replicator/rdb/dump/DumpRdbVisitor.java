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

package com.moilioncircle.redis.replicator.rdb.dump;

import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_LISTPACK;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_LISTPACK_EX;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_METADATA;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_ZIPLIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_ZIPMAP;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST_QUICKLIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST_QUICKLIST_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_LIST_ZIPLIST;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_MODULE;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_MODULE_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET_INTSET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_SET_LISTPACK;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_STREAM_LISTPACKS;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_STREAM_LISTPACKS_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_STREAM_LISTPACKS_3;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_STRING;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_LISTPACK;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_ZIPLIST;

import java.io.IOException;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.BaseRdbParser;
import com.moilioncircle.redis.replicator.rdb.DefaultRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.RdbValueVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.ContextKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.dump.datatype.DumpKeyValuePair;

/**
 * @author Leon Chen
 * @since 2.5.0
 */
public class DumpRdbVisitor extends DefaultRdbVisitor {
    
    protected int version = -1;

    public DumpRdbVisitor(Replicator replicator) {
        this(replicator, -1);
    }

    /**
     * @param replicator the replicator
     * @param version    dumped version : redis 2.8.x = 6, redis 3.x = 7, redis 4.0.x = 8, redis 5.0+ = 9. 
     *                   -1 means dumped version = rdb version
     * @since 2.6.0
     */
    public DumpRdbVisitor(Replicator replicator, int version) {
        this(replicator, version, 8192);
    }

    public DumpRdbVisitor(Replicator replicator, int version, int size) {
        super(replicator, new DumpRdbValueVisitor(replicator, version, size));
        this.version = version;
    }
    
    /**
     * @param replicator the replicator
     * @param valueVisitor rdb value visitor
     * @since 3.5.1
     */
    public DumpRdbVisitor(Replicator replicator, RdbValueVisitor valueVisitor) {
        super(replicator, valueVisitor);
    }
    
    @Override
    public Event applyString(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o0 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o0.setValueRdbType(RDB_TYPE_STRING);
        o0.setKey(key);
        o0.setValue(valueVisitor.applyString(in, version));
        return context.valueOf(o0);
    }

    @Override
    public Event applyList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o1 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o1.setValueRdbType(RDB_TYPE_LIST);
        o1.setKey(key);
        o1.setValue(valueVisitor.applyList(in, version));
        return context.valueOf(o1);
    }

    @Override
    public Event applySet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o2 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o2.setValueRdbType(RDB_TYPE_SET);
        o2.setKey(key);
        o2.setValue(valueVisitor.applySet(in, version));
        return context.valueOf(o2);
    }
    
    @Override
    public Event applySetListPack(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o20 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        if (this.version != -1 && this.version < 11 /* since redis rdb version 11 */) {
            o20.setValueRdbType(RDB_TYPE_SET);
        } else {
            o20.setValueRdbType(RDB_TYPE_SET_LISTPACK);
        }
        o20.setKey(key);
        o20.setValue(valueVisitor.applySetListPack(in, version));
        return context.valueOf(o20);
    }

    @Override
    public Event applyZSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o3 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o3.setValueRdbType(RDB_TYPE_ZSET);
        o3.setKey(key);
        o3.setValue(valueVisitor.applyZSet(in, version));
        return context.valueOf(o3);
    }

    @Override
    public Event applyZSet2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o5 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        if (this.version != -1 && this.version < 8 /* since redis rdb version 8 */) {
            o5.setValueRdbType(RDB_TYPE_ZSET);
        } else {
            o5.setValueRdbType(RDB_TYPE_ZSET_2);
        }
        o5.setKey(key);
        o5.setValue(valueVisitor.applyZSet2(in, version));
        return context.valueOf(o5);
    }

    @Override
    public Event applyHash(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o4 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o4.setValueRdbType(RDB_TYPE_HASH);
        o4.setKey(key);
        o4.setValue(valueVisitor.applyHash(in, version));
        return context.valueOf(o4);
    }

    @Override
    public Event applyHashZipMap(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o9 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o9.setValueRdbType(RDB_TYPE_HASH_ZIPMAP);
        o9.setKey(key);
        o9.setValue(valueVisitor.applyHashZipMap(in, version));
        return context.valueOf(o9);
    }

    @Override
    public Event applyListZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o10 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o10.setValueRdbType(RDB_TYPE_LIST_ZIPLIST);
        o10.setKey(key);
        o10.setValue(valueVisitor.applyListZipList(in, version));
        return context.valueOf(o10);
    }

    @Override
    public Event applySetIntSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o11 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o11.setValueRdbType(RDB_TYPE_SET_INTSET);
        o11.setKey(key);
        o11.setValue(valueVisitor.applySetIntSet(in, version));
        return context.valueOf(o11);
    }

    @Override
    public Event applyZSetZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o12 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o12.setValueRdbType(RDB_TYPE_ZSET_ZIPLIST);
        o12.setKey(key);
        o12.setValue(valueVisitor.applyZSetZipList(in, version));
        return context.valueOf(o12);
    }
    
    @Override
    public Event applyZSetListPack(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o17 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        if (this.version != -1 && this.version < 10 /* since redis rdb version 10 */) {
            o17.setValueRdbType(RDB_TYPE_ZSET);
        } else {
            o17.setValueRdbType(RDB_TYPE_ZSET_LISTPACK);
        }
        o17.setKey(key);
        o17.setValue(valueVisitor.applyZSetListPack(in, version));
        return context.valueOf(o17);
    }

    @Override
    public Event applyHashZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o13 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o13.setValueRdbType(RDB_TYPE_HASH_ZIPLIST);
        o13.setKey(key);
        o13.setValue(valueVisitor.applyHashZipList(in, version));
        return context.valueOf(o13);
    }
    
    @Override
    public Event applyHashListPack(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o16 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        if (this.version != -1 && this.version < 10 /* since redis rdb version 10 */) {
            o16.setValueRdbType(RDB_TYPE_HASH);
        } else {
            o16.setValueRdbType(RDB_TYPE_HASH_LISTPACK);
        }
        o16.setKey(key);
        o16.setValue(valueVisitor.applyHashListPack(in, version));
        return context.valueOf(o16);
    }

    @Override
    public Event applyListQuickList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o14 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        if (this.version != -1 && this.version < 7 /* since redis rdb version 7 */) {
            o14.setValueRdbType(RDB_TYPE_LIST);
        } else {
            o14.setValueRdbType(RDB_TYPE_LIST_QUICKLIST);
        }
        o14.setKey(key);
        o14.setValue(valueVisitor.applyListQuickList(in, version));
        return context.valueOf(o14);
    }
    
    @Override
    public Event applyListQuickList2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o18 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        if (this.version != -1 && this.version < 10 /* since redis rdb version 10 */) {
            o18.setValueRdbType(RDB_TYPE_LIST);
        } else {
            o18.setValueRdbType(RDB_TYPE_LIST_QUICKLIST_2);
        }
        o18.setKey(key);
        o18.setValue(valueVisitor.applyListQuickList2(in, version));
        return context.valueOf(o18);
    }
    
    @Override
    public Event applyHashMetadata(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException{
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o24 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        if (this.version != -1 && this.version < 12 /* since redis rdb version 12 */) {
            o24.setValueRdbType(RDB_TYPE_HASH);
        } else {
            o24.setValueRdbType(RDB_TYPE_HASH_METADATA);
        }
        o24.setKey(key);
        o24.setValue(valueVisitor.applyHashMetadata(in, version));
        return context.valueOf(o24);
    }
    
    @Override
    public Event applyHashListPackEx(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o25 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        if (this.version != -1 && this.version < 12 /* since redis rdb version 12 */) {
            o25.setValueRdbType(RDB_TYPE_HASH);
        } else {
            o25.setValueRdbType(RDB_TYPE_HASH_LISTPACK_EX);
        }
        o25.setKey(key);
        o25.setValue(valueVisitor.applyHashListPackEx(in, version));
        return context.valueOf(o25);
    }

    @Override
    public Event applyModule(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o6 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o6.setValueRdbType(RDB_TYPE_MODULE);
        o6.setKey(key);
        o6.setValue(valueVisitor.applyModule(in, version));
        return context.valueOf(o6);
    }

    @Override
    public Event applyModule2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o7 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o7.setValueRdbType(RDB_TYPE_MODULE_2);
        o7.setKey(key);
        o7.setValue(valueVisitor.applyModule2(in, version));
        return context.valueOf(o7);
    }

    @Override
    public Event applyStreamListPacks(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o15 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();

        o15.setValueRdbType(RDB_TYPE_STREAM_LISTPACKS);
        o15.setKey(key);
        o15.setValue(valueVisitor.applyStreamListPacks(in, version));
        return context.valueOf(o15);
    }
    
    @Override
    public Event applyStreamListPacks2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o19 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        if (this.version != -1 && this.version < 10 /* since redis rdb version 10 */) {
            o19.setValueRdbType(RDB_TYPE_STREAM_LISTPACKS);
        } else {
            o19.setValueRdbType(RDB_TYPE_STREAM_LISTPACKS_2);
        }
        o19.setKey(key);
        o19.setValue(valueVisitor.applyStreamListPacks2(in, version));
        return context.valueOf(o19);
    }
    
    @Override
    public Event applyStreamListPacks3(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        KeyValuePair<byte[], byte[]> o21 = new DumpKeyValuePair();
        byte[] key = parser.rdbLoadEncodedStringObject().first();
        if (this.version != -1 && this.version < 11 /* since redis rdb version 11 */) {
            o21.setValueRdbType(RDB_TYPE_STREAM_LISTPACKS);
        } else {
            o21.setValueRdbType(RDB_TYPE_STREAM_LISTPACKS_3);
        }
        o21.setKey(key);
        o21.setValue(valueVisitor.applyStreamListPacks3(in, version));
        return context.valueOf(o21);
    }
}
