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

package com.moilioncircle.redis.replicator.rdb.skip;

import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_FREQ;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_IDLE;

import java.io.IOException;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.DefaultRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.RdbValueVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.ContextKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;
import com.moilioncircle.redis.replicator.rdb.datatype.Function;

/**
 * @author Leon Chen
 * @since 2.4.6
 */
public class SkipRdbVisitor extends DefaultRdbVisitor {

    public SkipRdbVisitor(Replicator replicator) {
        super(replicator);
    }
    
    public SkipRdbVisitor(Replicator replicator, RdbValueVisitor valueVisitor) {
        super(replicator, valueVisitor);
    }
    
    @Override
    public Function applyFunction(RedisInputStream in, int version) throws IOException {
        valueVisitor.applyFunction(in, version);
        return null;
    }
    
    @Override
    public Function applyFunction2(RedisInputStream in, int version) throws IOException {
        valueVisitor.applyFunction2(in, version);
        return null;
    }

    @Override
    public DB applySelectDB(RedisInputStream in, int version) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadLen();
        return null;
    }

    @Override
    public DB applyResizeDB(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadLen();
        parser.rdbLoadLen();
        return null;
    }

    @Override
    public Event applyExpireTime(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadTime();
        int type = applyType(in);
        context.setValueRdbType(type);
        if (type == RDB_OPCODE_FREQ) {
            applyFreq(in, version, context);
        } else if (type == RDB_OPCODE_IDLE) {
            applyIdle(in, version, context);
        } else {
            rdbLoadObject(in, version, context);
        }
        return null;
    }

    @Override
    public Event applyExpireTimeMs(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadMillisecondTime();
        int type = applyType(in);
        context.setValueRdbType(type);
        if (type == RDB_OPCODE_FREQ) {
            applyFreq(in, version, context);
        } else if (type == RDB_OPCODE_IDLE) {
            applyIdle(in, version, context);
        } else {
            rdbLoadObject(in, version, context);
        }
        return null;
    }

    @Override
    public Event applyFreq(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        in.read();
        int valueType = applyType(in);
        context.setValueRdbType(valueType);
        rdbLoadObject(in, version, context);
        return null;
    }

    @Override
    public Event applyIdle(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadLen();
        int valueType = applyType(in);
        context.setValueRdbType(valueType);
        rdbLoadObject(in, version, context);
        return null;
    }

    @Override
    public Event applyAux(RedisInputStream in, int version) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        parser.rdbLoadEncodedStringObject();
        return null;
    }

    @Override
    public Event applyModuleAux(RedisInputStream in, int version) throws IOException {
        return super.applyModuleAux(in, version);
    }

    @Override
    public Event applyString(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyString(in, version);
        return null;
    }

    @Override
    public Event applyList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyList(in, version);
        return null;
    }

    @Override
    public Event applySet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applySet(in, version);
        return null;
    }
    
    @Override
    public Event applySetListPack(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applySetListPack(in, version);
        return null;
    }

    @Override
    public Event applyZSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyZSet(in, version);
        return null;
    }

    @Override
    public Event applyZSet2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyZSet2(in, version);
        return null;
    }

    @Override
    public Event applyHash(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyHash(in, version);
        return null;
    }

    @Override
    public Event applyHashZipMap(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyHashZipMap(in, version);
        return null;
    }

    @Override
    public Event applyListZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyListZipList(in, version);
        return null;
    }

    @Override
    public Event applySetIntSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applySetIntSet(in, version);
        return null;
    }

    @Override
    public Event applyZSetZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyZSetZipList(in, version);
        return null;
    }
    
    @Override
    public Event applyZSetListPack(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyZSetListPack(in, version);
        return null;
    }

    @Override
    public Event applyHashZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyHashZipList(in, version);
        return null;
    }
    
    @Override
    public Event applyHashListPack(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyHashListPack(in, version);
        return null;
    }

    @Override
    public Event applyListQuickList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyListQuickList(in, version);
        return null;
    }

    @Override
    public Event applyListQuickList2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyListQuickList2(in, version);
        return null;
    }
    
    @Override
    public Event applyHashMetadata(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException{
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyHashMetadata(in, version);
        return null;
    }
    
    @Override
    public Event applyHashListPackEx(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyHashListPackEx(in, version);
        return null;
    }
    
    @Override
    public Event applyModule(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyModule(in, version);
        return null;
    }

    @Override
    public Event applyModule2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyModule2(in, version);
        return null;
    }

    @Override
    public Event applyStreamListPacks(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyStreamListPacks(in, version);
        return null;
    }
    
    @Override
    public Event applyStreamListPacks2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyStreamListPacks2(in, version);
        return null;
    }
    
    @Override
    public Event applyStreamListPacks3(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        valueVisitor.applyStreamListPacks3(in, version);
        return null;
    }
}
