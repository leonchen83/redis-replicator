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

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.DefaultRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.ContextKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;

import java.io.IOException;
import java.util.NoSuchElementException;

import static com.moilioncircle.redis.replicator.Constants.MODULE_SET;
import static com.moilioncircle.redis.replicator.Constants.RDB_MODULE_OPCODE_EOF;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_FREQ;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_IDLE;

/**
 * @author Leon Chen
 * @since 2.4.6
 */
public class SkipRdbVisitor extends DefaultRdbVisitor {

    public SkipRdbVisitor(Replicator replicator) {
        super(replicator);
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
        parser.rdbLoadEncodedStringObject();
        return null;
    }

    @Override
    public Event applyList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        long len = parser.rdbLoadLen().len;
        while (len > 0) {
            parser.rdbLoadEncodedStringObject();
            len--;
        }
        return null;
    }

    @Override
    public Event applySet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        long len = parser.rdbLoadLen().len;
        while (len > 0) {
            parser.rdbLoadEncodedStringObject();
            len--;
        }
        return null;
    }

    @Override
    public Event applyZSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        long len = parser.rdbLoadLen().len;
        while (len > 0) {
            parser.rdbLoadEncodedStringObject();
            parser.rdbLoadDoubleValue();
            len--;
        }
        return null;
    }

    @Override
    public Event applyZSet2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        long len = parser.rdbLoadLen().len;
        while (len > 0) {
            parser.rdbLoadEncodedStringObject();
            parser.rdbLoadBinaryDoubleValue();
            len--;
        }
        return null;
    }

    @Override
    public Event applyHash(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        long len = parser.rdbLoadLen().len;
        while (len > 0) {
            parser.rdbLoadEncodedStringObject();
            parser.rdbLoadEncodedStringObject();
            len--;
        }
        return null;
    }

    @Override
    public Event applyHashZipMap(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        parser.rdbLoadPlainStringObject();
        return null;
    }

    @Override
    public Event applyListZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        parser.rdbLoadPlainStringObject();
        return null;
    }

    @Override
    public Event applySetIntSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        parser.rdbLoadPlainStringObject();
        return null;
    }

    @Override
    public Event applyZSetZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        parser.rdbLoadPlainStringObject();
        return null;
    }

    @Override
    public Event applyHashZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        parser.rdbLoadPlainStringObject();
        return null;
    }

    @Override
    public Event applyListQuickList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        long len = parser.rdbLoadLen().len;
        for (long i = 0; i < len; i++) {
            parser.rdbGenericLoadStringObject();
        }
        return null;
    }

    @Override
    public Event applyModule(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
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
        moduleParser.parse(in, 1);
        return null;
    }

    @Override
    public Event applyModule2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        char[] c = new char[9];
        long moduleid = parser.rdbLoadLen().len;
        for (int i = 0; i < c.length; i++) {
            c[i] = MODULE_SET[(int) (moduleid >>> (10 + (c.length - 1 - i) * 6) & 63)];
        }
        String moduleName = new String(c);
        int moduleVersion = (int) (moduleid & 1023);
        ModuleParser<? extends Module> moduleParser = lookupModuleParser(moduleName, moduleVersion);
        if (moduleParser == null) {
            SkipRdbParser skipRdbParser = new SkipRdbParser(in);
            skipRdbParser.rdbLoadCheckModuleValue();
        } else {
            moduleParser.parse(in, 2);
            long eof = parser.rdbLoadLen().len;
            if (eof != RDB_MODULE_OPCODE_EOF) {
                throw new UnsupportedOperationException("The RDB file contains module data for the module '" + moduleName + "' that is not terminated by the proper module value EOF marker");
            }
        }
        return null;
    }

    @Override
    public Event applyStreamListPacks(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        SkipRdbParser parser = new SkipRdbParser(in);
        parser.rdbLoadEncodedStringObject();
        long listPacks = parser.rdbLoadLen().len;
        while (listPacks-- > 0) {
            parser.rdbLoadPlainStringObject();
            parser.rdbLoadPlainStringObject();
        }
        parser.rdbLoadLen();
        parser.rdbLoadLen();
        parser.rdbLoadLen();
        long groupCount = parser.rdbLoadLen().len;
        while (groupCount-- > 0) {
            parser.rdbLoadPlainStringObject();
            parser.rdbLoadLen();
            parser.rdbLoadLen();
            long groupPel = parser.rdbLoadLen().len;
            while (groupPel-- > 0) {
                in.skip(16);
                parser.rdbLoadMillisecondTime();
                parser.rdbLoadLen();
            }
            long consumerCount = parser.rdbLoadLen().len;
            while (consumerCount-- > 0) {
                parser.rdbLoadPlainStringObject();
                parser.rdbLoadMillisecondTime();
                long consumerPel = parser.rdbLoadLen().len;
                while (consumerPel-- > 0) {
                    in.skip(16);
                }
            }
        }
        return null;
    }
}
