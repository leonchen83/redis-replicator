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

package com.moilioncircle.redis.replicator.rdb.dump;

import static com.moilioncircle.redis.replicator.Constants.MODULE_SET;
import static com.moilioncircle.redis.replicator.Constants.QUICKLIST_NODE_CONTAINER_PACKED;
import static com.moilioncircle.redis.replicator.Constants.QUICKLIST_NODE_CONTAINER_PLAIN;
import static com.moilioncircle.redis.replicator.Constants.RDB_LOAD_NONE;
import static com.moilioncircle.redis.replicator.Constants.RDB_MODULE_OPCODE_EOF;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_FUNCTION;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_FUNCTION2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_HASH_LISTPACK;
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
import static com.moilioncircle.redis.replicator.rdb.BaseRdbParser.StringHelper.listPackEntry;
import static com.moilioncircle.redis.replicator.util.CRC64.crc64;
import static com.moilioncircle.redis.replicator.util.CRC64.longToByteArray;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.NoSuchElementException;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.io.ByteBufferOutputStream;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.BaseRdbEncoder;
import com.moilioncircle.redis.replicator.rdb.BaseRdbParser;
import com.moilioncircle.redis.replicator.rdb.DefaultRdbValueVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.dump.datatype.DumpFunction;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;
import com.moilioncircle.redis.replicator.rdb.skip.SkipRdbParser;
import com.moilioncircle.redis.replicator.util.ByteArray;
import com.moilioncircle.redis.replicator.util.ByteBuilder;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * @author Leon Chen
 * @since 3.1.0
 */
@SuppressWarnings("unchecked")
public class DumpRdbValueVisitor extends DefaultRdbValueVisitor {

    private class DefaultRawByteListener implements RawByteListener {
        private final int version;
        private final ByteBuilder builder;

        private DefaultRawByteListener(byte type, int version) {
            this.builder = ByteBuilder.allocate(DumpRdbValueVisitor.this.size);
            this.builder.put(type);
            int ver = DumpRdbValueVisitor.this.version;
            this.version = ver == -1 ? version : ver;
        }

        @Override
        public void handle(byte... rawBytes) {
            this.builder.put(rawBytes);
        }
    
        public void handle(ByteBuffer buffer) {
            this.builder.put(buffer);
        }

        public byte[] getBytes() {
            this.builder.put((byte) version);
            this.builder.put((byte) 0x00);
            List<ByteBuffer> buffers = this.builder.buffers();
            byte[] crc = longToByteArray(crc64(buffers));
            for (byte b : crc) {
                this.builder.put(b);
            }
            return this.builder.array();
        }
    }

    private final int size;
    private final int version;

    public DumpRdbValueVisitor(Replicator replicator) {
        this(replicator, -1);
    }

    public DumpRdbValueVisitor(Replicator replicator, int version) {
        this(replicator, version, 8192);
    }

    public DumpRdbValueVisitor(Replicator replicator, int version, int size) {
        super(replicator);
        this.version = version;
        this.size = size;
    }
    
    @Override
    public <T> T applyFunction(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_OPCODE_FUNCTION, version);
        replicator.addRawByteListener(listener);
        try {
            SkipRdbParser parser = new SkipRdbParser(in);
            parser.rdbGenericLoadStringObject(); // name
            parser.rdbGenericLoadStringObject(); // engine name
            long hasDesc = parser.rdbLoadLen().len;
            if (hasDesc == 1) {
                parser.rdbGenericLoadStringObject(); // description
            }
            parser.rdbGenericLoadStringObject(); // code
        } finally {
            replicator.removeRawByteListener(listener);
        }
        DumpFunction function = new DumpFunction();
        function.setSerialized(listener.getBytes());
        return (T) function;
    }
    
    @Override
    public <T> T applyFunction2(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_OPCODE_FUNCTION2, version);
        replicator.addRawByteListener(listener);
        try {
            SkipRdbParser parser = new SkipRdbParser(in);
            parser.rdbGenericLoadStringObject(); // code
        } finally {
            replicator.removeRawByteListener(listener);
        }
        DumpFunction function = new DumpFunction();
        function.setSerialized(listener.getBytes());
        return (T) function;
    }

    @Override
    public <T> T applyString(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_STRING, version);
        replicator.addRawByteListener(listener);
        try {
            new SkipRdbParser(in).rdbLoadEncodedStringObject();
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }

    @Override
    public <T> T applyList(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_LIST, version);
        replicator.addRawByteListener(listener);
        try {
            SkipRdbParser skipParser = new SkipRdbParser(in);
            long len = skipParser.rdbLoadLen().len;
            while (len > 0) {
                skipParser.rdbLoadEncodedStringObject();
                len--;
            }
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }

    @Override
    public <T> T applySet(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_SET, version);
        replicator.addRawByteListener(listener);
        try {
            SkipRdbParser skipParser = new SkipRdbParser(in);
            long len = skipParser.rdbLoadLen().len;
            while (len > 0) {
                skipParser.rdbLoadEncodedStringObject();
                len--;
            }
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }
    
    @Override
    public <T> T applySetListPack(RedisInputStream in, int version) throws IOException {
        if (this.version != -1 && this.version < 11 /* since redis rdb version 11 */) {
            // downgrade to RDB_TYPE_SET
            BaseRdbParser parser = new BaseRdbParser(in);
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_SET, version);
            try (ByteBufferOutputStream out = new ByteBufferOutputStream(size)) {
                RedisInputStream listPack = new RedisInputStream(parser.rdbLoadPlainStringObject());
                listPack.skip(4); // total-bytes
                int len = listPack.readInt(2);
                listener.handle(encoder.rdbSaveLen(len));
                while (len > 0) {
                    byte[] element = listPackEntry(listPack);
                    encoder.rdbGenericSaveStringObject(new ByteArray(element), out);
                    len--;
                }
                int lpend = listPack.read(); // lp-end
                if (lpend != 255) {
                    throw new AssertionError("listpack expect 255 but " + lpend);
                }
                listener.handle(out.toByteBuffer());
                return (T) listener.getBytes();
            }
        } else {
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_SET_LISTPACK, version);
            replicator.addRawByteListener(listener);
            try {
                SkipRdbParser skipParser = new SkipRdbParser(in);
                skipParser.rdbLoadPlainStringObject();
            } finally {
                replicator.removeRawByteListener(listener);
            }
            return (T) listener.getBytes();
        }
    }

    @Override
    public <T> T applyZSet(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_ZSET, version);
        replicator.addRawByteListener(listener);
        try {
            SkipRdbParser skipParser = new SkipRdbParser(in);
            long len = skipParser.rdbLoadLen().len;
            while (len > 0) {
                skipParser.rdbLoadEncodedStringObject();
                skipParser.rdbLoadDoubleValue();
                len--;
            }
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }

    @Override
    public <T> T applyZSet2(RedisInputStream in, int version) throws IOException {
        if (this.version != -1 && this.version < 8 /* since redis rdb version 8 */) {
            // downgrade to RDB_TYPE_ZSET
            BaseRdbParser parser = new BaseRdbParser(in);
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_ZSET, version);
            try (ByteBufferOutputStream out = new ByteBufferOutputStream(size)) {
                long len = parser.rdbLoadLen().len;
                listener.handle(encoder.rdbSaveLen(len));
                while (len > 0) {
                    ByteArray element = parser.rdbLoadEncodedStringObject();
                    encoder.rdbGenericSaveStringObject(element, out);
                    double score = parser.rdbLoadBinaryDoubleValue();
                    encoder.rdbSaveDoubleValue(score, out);
                    len--;
                }
                listener.handle(out.toByteBuffer());
                return (T) listener.getBytes();
            }
        } else {
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_ZSET_2, version);
            replicator.addRawByteListener(listener);
            try {
                SkipRdbParser skipParser = new SkipRdbParser(in);
                long len = skipParser.rdbLoadLen().len;
                while (len > 0) {
                    skipParser.rdbLoadEncodedStringObject();
                    skipParser.rdbLoadBinaryDoubleValue();
                    len--;
                }
            } finally {
                replicator.removeRawByteListener(listener);
            }
            return (T) listener.getBytes();
        }
    }

    @Override
    public <T> T applyHash(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_HASH, version);
        replicator.addRawByteListener(listener);
        try {
            SkipRdbParser skipParser = new SkipRdbParser(in);
            long len = skipParser.rdbLoadLen().len;
            while (len > 0) {
                skipParser.rdbLoadEncodedStringObject();
                skipParser.rdbLoadEncodedStringObject();
                len--;
            }
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }

    @Override
    public <T> T applyHashZipMap(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_HASH_ZIPMAP, version);
        replicator.addRawByteListener(listener);
        try {
            new SkipRdbParser(in).rdbLoadPlainStringObject();
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }

    @Override
    public <T> T applyListZipList(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_LIST_ZIPLIST, version);
        replicator.addRawByteListener(listener);
        try {
            new SkipRdbParser(in).rdbLoadPlainStringObject();
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }

    @Override
    public <T> T applySetIntSet(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_SET_INTSET, version);
        replicator.addRawByteListener(listener);
        try {
            new SkipRdbParser(in).rdbLoadPlainStringObject();
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }

    @Override
    public <T> T applyZSetZipList(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_ZSET_ZIPLIST, version);
        replicator.addRawByteListener(listener);
        try {
            new SkipRdbParser(in).rdbLoadPlainStringObject();
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }
    
    @Override
    public <T> T applyZSetListPack(RedisInputStream in, int version) throws IOException {
        if (this.version != -1 && this.version < 10 /* since redis rdb version 10 */) {
            // downgrade to RDB_TYPE_ZSET
            BaseRdbParser parser = new BaseRdbParser(in);
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_ZSET, version);
            try (ByteBufferOutputStream out = new ByteBufferOutputStream(size)) {
                RedisInputStream listPack = new RedisInputStream(parser.rdbLoadPlainStringObject());
                listPack.skip(4); // total-bytes
                int len = listPack.readInt(2);
                listener.handle(encoder.rdbSaveLen(len / 2));
                while (len > 0) {
                    byte[] element = listPackEntry(listPack);
                    encoder.rdbGenericSaveStringObject(new ByteArray(element), out);
                    len--;
                    double score = Double.valueOf(Strings.toString(listPackEntry(listPack)));
                    encoder.rdbSaveDoubleValue(score, out);
                    len--;
                }
                int lpend = listPack.read(); // lp-end
                if (lpend != 255) {
                    throw new AssertionError("listpack expect 255 but " + lpend);
                }
                listener.handle(out.toByteBuffer());
                return (T) listener.getBytes();
            }
        } else {
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_ZSET_LISTPACK, version);
            replicator.addRawByteListener(listener);
            try {
                SkipRdbParser skipParser = new SkipRdbParser(in);
                skipParser.rdbLoadPlainStringObject();
            } finally {
                replicator.removeRawByteListener(listener);
            }
            return (T) listener.getBytes();
        }
    }

    @Override
    public <T> T applyHashZipList(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_HASH_ZIPLIST, version);
        replicator.addRawByteListener(listener);
        try {
            new SkipRdbParser(in).rdbLoadPlainStringObject();
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }
    
    @Override
    public <T> T applyHashListPack(RedisInputStream in, int version) throws IOException {
        if (this.version != -1 && this.version < 10 /* since redis rdb version 10 */) {
            // downgrade to RDB_TYPE_HASH
            BaseRdbParser parser = new BaseRdbParser(in);
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_HASH, version);
            try (ByteBufferOutputStream out = new ByteBufferOutputStream(size)) {
                RedisInputStream listPack = new RedisInputStream(parser.rdbLoadPlainStringObject());
                listPack.skip(4); // total-bytes
                int len = listPack.readInt(2);
                listener.handle(encoder.rdbSaveLen(len / 2));
                while (len > 0) {
                    byte[] field = listPackEntry(listPack);
                    encoder.rdbGenericSaveStringObject(new ByteArray(field), out);
                    len--;
                    byte[] value = listPackEntry(listPack);
                    encoder.rdbGenericSaveStringObject(new ByteArray(value), out);
                    len--;
                }
                int lpend = listPack.read(); // lp-end
                if (lpend != 255) {
                    throw new AssertionError("listpack expect 255 but " + lpend);
                }
                listener.handle(out.toByteBuffer());
                return (T) listener.getBytes();
            }
        } else {
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_HASH_LISTPACK, version);
            replicator.addRawByteListener(listener);
            try {
                SkipRdbParser skipParser = new SkipRdbParser(in);
                skipParser.rdbLoadPlainStringObject();
            } finally {
                replicator.removeRawByteListener(listener);
            }
            return (T) listener.getBytes();
        }
    }

    @Override
    public <T> T applyListQuickList(RedisInputStream in, int version) throws IOException {
        if (this.version != -1 && this.version < 7 /* since redis rdb version 7 */) {
            // downgrade to RDB_TYPE_LIST
            BaseRdbParser parser = new BaseRdbParser(in);
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            try (ByteBufferOutputStream out = new ByteBufferOutputStream(size)) {
                int total = 0;
                long len = parser.rdbLoadLen().len;
                for (long i = 0; i < len; i++) {
                    RedisInputStream stream = new RedisInputStream(parser.rdbGenericLoadStringObject(RDB_LOAD_NONE));
            
                    BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
                    BaseRdbParser.LenHelper.zltail(stream); // zltail
                    int zllen = BaseRdbParser.LenHelper.zllen(stream);
                    for (int j = 0; j < zllen; j++) {
                        byte[] e = BaseRdbParser.StringHelper.zipListEntry(stream);
                        encoder.rdbGenericSaveStringObject(new ByteArray(e), out);
                        total++;
                    }
                    int zlend = BaseRdbParser.LenHelper.zlend(stream);
                    if (zlend != 255) {
                        throw new AssertionError("zlend expect 255 but " + zlend);
                    }
                }
        
                DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_LIST, version);
                listener.handle(encoder.rdbSaveLen(total));
                listener.handle(out.toByteBuffer());
                return (T) listener.getBytes();
            }
        } else {
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_LIST_QUICKLIST, version);
            replicator.addRawByteListener(listener);
            try {
                SkipRdbParser skipParser = new SkipRdbParser(in);
                long len = skipParser.rdbLoadLen().len;
                for (long i = 0; i < len; i++) {
                    skipParser.rdbGenericLoadStringObject();
                }
            } finally {
                replicator.removeRawByteListener(listener);
            }
            return (T) listener.getBytes();
        }
    }
    
    @Override
    public <T> T applyListQuickList2(RedisInputStream in, int version) throws IOException {
        if (this.version != -1 && this.version < 10 /* since redis rdb version 10 */) {
            // downgrade to RDB_TYPE_LIST
            BaseRdbParser parser = new BaseRdbParser(in);
            BaseRdbEncoder encoder = new BaseRdbEncoder();
            try (ByteBufferOutputStream out = new ByteBufferOutputStream(size)) {
                int total = 0;
                long len = parser.rdbLoadLen().len;
                for (long i = 0; i < len; i++) {
                    long container = parser.rdbLoadLen().len;
                    ByteArray bytes = parser.rdbLoadPlainStringObject();
                    if (container == QUICKLIST_NODE_CONTAINER_PLAIN) {
                        encoder.rdbGenericSaveStringObject(new ByteArray(bytes.first()), out);
                        total++;
                    } else if (container == QUICKLIST_NODE_CONTAINER_PACKED) {
                        RedisInputStream listPack = new RedisInputStream(bytes);
                        listPack.skip(4); // total-bytes
                        int innerLen = listPack.readInt(2);
                        for (int j = 0; j < innerLen; j++) {
                            byte[] e = listPackEntry(listPack);
                            encoder.rdbGenericSaveStringObject(new ByteArray(e), out);
                            total++;
                        }
                        int lpend = listPack.read(); // lp-end
                        if (lpend != 255) {
                            throw new AssertionError("listpack expect 255 but " + lpend);
                        }
                    } else {
                        throw new UnsupportedOperationException(String.valueOf(container));
                    }
                }
            
                DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_LIST, version);
                listener.handle(encoder.rdbSaveLen(total));
                listener.handle(out.toByteBuffer());
                return (T) listener.getBytes();
            }
        } else {
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_LIST_QUICKLIST_2, version);
            replicator.addRawByteListener(listener);
            try {
                SkipRdbParser skipParser = new SkipRdbParser(in);
                long len = skipParser.rdbLoadLen().len;
                for (long i = 0; i < len; i++) {
                    skipParser.rdbLoadLen();
                    skipParser.rdbLoadPlainStringObject();
                }
            } finally {
                replicator.removeRawByteListener(listener);
            }
            return (T) listener.getBytes();
        }
    }

    @Override
    public <T> T applyModule(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_MODULE, version);
        replicator.addRawByteListener(listener);
        try {
            SkipRdbParser skipParser = new SkipRdbParser(in);
            char[] c = new char[9];
            long moduleid = skipParser.rdbLoadLen().len;
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
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }

    @Override
    public <T> T applyModule2(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_MODULE_2, version);
        replicator.addRawByteListener(listener);
        try {
            BaseRdbParser parser = new BaseRdbParser(in);
            SkipRdbParser skipParser = new SkipRdbParser(in);
            char[] c = new char[9];
            long moduleid = skipParser.rdbLoadLen().len;
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
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }

    @Override
    @SuppressWarnings("resource")
    public <T> T applyStreamListPacks(RedisInputStream in, int version) throws IOException {
        DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_STREAM_LISTPACKS, version);
        replicator.addRawByteListener(listener);
        try {
            SkipRdbParser skipParser = new SkipRdbParser(in);
            long listPacks = skipParser.rdbLoadLen().len;
            while (listPacks-- > 0) {
                skipParser.rdbLoadPlainStringObject();
                skipParser.rdbLoadPlainStringObject();
            }
            skipParser.rdbLoadLen();
            skipParser.rdbLoadLen();
            skipParser.rdbLoadLen();
            long groupCount = skipParser.rdbLoadLen().len;
            while (groupCount-- > 0) {
                skipParser.rdbLoadPlainStringObject();
                skipParser.rdbLoadLen();
                skipParser.rdbLoadLen();
                long groupPel = skipParser.rdbLoadLen().len;
                while (groupPel-- > 0) {
                    in.skip(16);
                    skipParser.rdbLoadMillisecondTime();
                    skipParser.rdbLoadLen();
                }
                long consumerCount = skipParser.rdbLoadLen().len;
                while (consumerCount-- > 0) {
                    skipParser.rdbLoadPlainStringObject();
                    skipParser.rdbLoadMillisecondTime();
                    long consumerPel = skipParser.rdbLoadLen().len;
                    while (consumerPel-- > 0) {
                        in.skip(16);
                    }
                }
            }
        } finally {
            replicator.removeRawByteListener(listener);
        }
        return (T) listener.getBytes();
    }
    
    @Override
    @SuppressWarnings("resource")
    public <T> T applyStreamListPacks2(RedisInputStream in, int version) throws IOException {
        if (this.version != -1 && this.version < 10 /* since redis rdb version 10 */) {
            // downgrade to RDB_TYPE_STREAM_LISTPACKS
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_STREAM_LISTPACKS, version);
            replicator.addRawByteListener(listener);
            try {
                SkipRdbParser skipParser = new SkipRdbParser(in);
                long listPacks = skipParser.rdbLoadLen().len;
                while (listPacks-- > 0) {
                    skipParser.rdbLoadPlainStringObject();
                    skipParser.rdbLoadPlainStringObject();
                }
                skipParser.rdbLoadLen(); // length
                skipParser.rdbLoadLen(); // lastId
                skipParser.rdbLoadLen(); // lastId
                replicator.removeRawByteListener(listener);
                skipParser.rdbLoadLen(); // firstId
                skipParser.rdbLoadLen(); // firstId
                skipParser.rdbLoadLen(); // maxDeletedEntryId
                skipParser.rdbLoadLen(); // maxDeletedEntryId
                skipParser.rdbLoadLen(); // entriesAdded
                replicator.addRawByteListener(listener);
                long groupCount = skipParser.rdbLoadLen().len;
                while (groupCount-- > 0) {
                    skipParser.rdbLoadPlainStringObject();
                    skipParser.rdbLoadLen();
                    skipParser.rdbLoadLen();
                    replicator.removeRawByteListener(listener);
                    skipParser.rdbLoadLen(); // entriesRead
                    replicator.addRawByteListener(listener);
                    long groupPel = skipParser.rdbLoadLen().len;
                    while (groupPel-- > 0) {
                        in.skip(16);
                        skipParser.rdbLoadMillisecondTime();
                        skipParser.rdbLoadLen();
                    }
                    long consumerCount = skipParser.rdbLoadLen().len;
                    while (consumerCount-- > 0) {
                        skipParser.rdbLoadPlainStringObject();
                        skipParser.rdbLoadMillisecondTime();
                        long consumerPel = skipParser.rdbLoadLen().len;
                        while (consumerPel-- > 0) {
                            in.skip(16);
                        }
                    }
                }
            } finally {
                replicator.removeRawByteListener(listener);
            }
            return (T) listener.getBytes();
        } else {
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_STREAM_LISTPACKS_2, version);
            replicator.addRawByteListener(listener);
            try {
                SkipRdbParser skipParser = new SkipRdbParser(in);
                long listPacks = skipParser.rdbLoadLen().len;
                while (listPacks-- > 0) {
                    skipParser.rdbLoadPlainStringObject();
                    skipParser.rdbLoadPlainStringObject();
                }
                skipParser.rdbLoadLen(); // length
                skipParser.rdbLoadLen(); // lastId
                skipParser.rdbLoadLen(); // lastId
                skipParser.rdbLoadLen(); // firstId
                skipParser.rdbLoadLen(); // firstId
                skipParser.rdbLoadLen(); // maxDeletedEntryId
                skipParser.rdbLoadLen(); // maxDeletedEntryId
                skipParser.rdbLoadLen(); // entriesAdded
                long groupCount = skipParser.rdbLoadLen().len;
                while (groupCount-- > 0) {
                    skipParser.rdbLoadPlainStringObject();
                    skipParser.rdbLoadLen();
                    skipParser.rdbLoadLen();
                    skipParser.rdbLoadLen(); // entriesRead
                    long groupPel = skipParser.rdbLoadLen().len;
                    while (groupPel-- > 0) {
                        in.skip(16);
                        skipParser.rdbLoadMillisecondTime();
                        skipParser.rdbLoadLen();
                    }
                    long consumerCount = skipParser.rdbLoadLen().len;
                    while (consumerCount-- > 0) {
                        skipParser.rdbLoadPlainStringObject();
                        skipParser.rdbLoadMillisecondTime();
                        long consumerPel = skipParser.rdbLoadLen().len;
                        while (consumerPel-- > 0) {
                            in.skip(16);
                        }
                    }
                }
            } finally {
                replicator.removeRawByteListener(listener);
            }
            return (T) listener.getBytes();
        }
    }
    
    @Override
    @SuppressWarnings("resource")
    public <T> T applyStreamListPacks3(RedisInputStream in, int version) throws IOException {
        if (this.version != -1 && this.version < 11 /* since redis rdb version 11 */) {
            // downgrade to RDB_TYPE_STREAM_LISTPACKS
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_STREAM_LISTPACKS, version);
            replicator.addRawByteListener(listener);
            try {
                SkipRdbParser skipParser = new SkipRdbParser(in);
                long listPacks = skipParser.rdbLoadLen().len;
                while (listPacks-- > 0) {
                    skipParser.rdbLoadPlainStringObject();
                    skipParser.rdbLoadPlainStringObject();
                }
                skipParser.rdbLoadLen(); // length
                skipParser.rdbLoadLen(); // lastId
                skipParser.rdbLoadLen(); // lastId
                replicator.removeRawByteListener(listener);
                skipParser.rdbLoadLen(); // firstId
                skipParser.rdbLoadLen(); // firstId
                skipParser.rdbLoadLen(); // maxDeletedEntryId
                skipParser.rdbLoadLen(); // maxDeletedEntryId
                skipParser.rdbLoadLen(); // entriesAdded
                replicator.addRawByteListener(listener);
                long groupCount = skipParser.rdbLoadLen().len;
                while (groupCount-- > 0) {
                    skipParser.rdbLoadPlainStringObject();
                    skipParser.rdbLoadLen();
                    skipParser.rdbLoadLen();
                    replicator.removeRawByteListener(listener);
                    skipParser.rdbLoadLen(); // entriesRead
                    replicator.addRawByteListener(listener);
                    long groupPel = skipParser.rdbLoadLen().len;
                    while (groupPel-- > 0) {
                        in.skip(16);
                        skipParser.rdbLoadMillisecondTime();
                        skipParser.rdbLoadLen();
                    }
                    long consumerCount = skipParser.rdbLoadLen().len;
                    while (consumerCount-- > 0) {
                        skipParser.rdbLoadPlainStringObject();
                        skipParser.rdbLoadMillisecondTime(); // seenTime
                        replicator.removeRawByteListener(listener);
                        skipParser.rdbLoadMillisecondTime(); // activeTime
                        replicator.addRawByteListener(listener);
                        long consumerPel = skipParser.rdbLoadLen().len;
                        while (consumerPel-- > 0) {
                            in.skip(16);
                        }
                    }
                }
            } finally {
                replicator.removeRawByteListener(listener);
            }
            return (T) listener.getBytes();
        } else {
            DefaultRawByteListener listener = new DefaultRawByteListener((byte) RDB_TYPE_STREAM_LISTPACKS_3, version);
            replicator.addRawByteListener(listener);
            try {
                SkipRdbParser skipParser = new SkipRdbParser(in);
                long listPacks = skipParser.rdbLoadLen().len;
                while (listPacks-- > 0) {
                    skipParser.rdbLoadPlainStringObject();
                    skipParser.rdbLoadPlainStringObject();
                }
                skipParser.rdbLoadLen(); // length
                skipParser.rdbLoadLen(); // lastId
                skipParser.rdbLoadLen(); // lastId
                skipParser.rdbLoadLen(); // firstId
                skipParser.rdbLoadLen(); // firstId
                skipParser.rdbLoadLen(); // maxDeletedEntryId
                skipParser.rdbLoadLen(); // maxDeletedEntryId
                skipParser.rdbLoadLen(); // entriesAdded
                long groupCount = skipParser.rdbLoadLen().len;
                while (groupCount-- > 0) {
                    skipParser.rdbLoadPlainStringObject();
                    skipParser.rdbLoadLen();
                    skipParser.rdbLoadLen();
                    skipParser.rdbLoadLen(); // entriesRead
                    long groupPel = skipParser.rdbLoadLen().len;
                    while (groupPel-- > 0) {
                        in.skip(16);
                        skipParser.rdbLoadMillisecondTime();
                        skipParser.rdbLoadLen();
                    }
                    long consumerCount = skipParser.rdbLoadLen().len;
                    while (consumerCount-- > 0) {
                        skipParser.rdbLoadPlainStringObject();
                        skipParser.rdbLoadMillisecondTime(); // seenTime
                        skipParser.rdbLoadMillisecondTime(); // activeTime
                        long consumerPel = skipParser.rdbLoadLen().len;
                        while (consumerPel-- > 0) {
                            in.skip(16);
                        }
                    }
                }
            } finally {
                replicator.removeRawByteListener(listener);
            }
            return (T) listener.getBytes();
        }
    }
}
