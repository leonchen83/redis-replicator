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

package com.moilioncircle.redis.replicator.rdb.iterable;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.BaseRdbParser;
import com.moilioncircle.redis.replicator.rdb.DefaultRdbValueVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;
import com.moilioncircle.redis.replicator.util.Strings;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;

import static com.moilioncircle.redis.replicator.Constants.RDB_LOAD_NONE;

/**
 * @author Leon Chen
 * @since 3.1.0
 */
@SuppressWarnings("unchecked")
public class ValueIterableRdbValueVisitor extends DefaultRdbValueVisitor {

    public ValueIterableRdbValueVisitor(Replicator replicator) {
        super(replicator);
    }

    @Override
    public <T> T applyList(RedisInputStream in, int version) throws IOException {
        /*
         * |    <len>     |       <content>       |
         * | 1 or 5 bytes |    string contents    |
         */
        BaseRdbParser parser = new BaseRdbParser(in);

        long len = parser.rdbLoadLen().len;
        Iterator<byte[]> val = new Iter<byte[]>(len, parser) {
            @Override
            public boolean hasNext() {
                return condition > 0;
            }

            @Override
            public byte[] next() {
                try {
                    byte[] element = parser.rdbLoadEncodedStringObject().first();
                    condition--;
                    return element;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        return (T) val;
    }

    @Override
    public <T> T applySet(RedisInputStream in, int version) throws IOException {
        /*
         * |    <len>     |       <content>       |
         * | 1 or 5 bytes |    string contents    |
         */
        BaseRdbParser parser = new BaseRdbParser(in);

        long len = parser.rdbLoadLen().len;
        Iterator<byte[]> val = new Iter<byte[]>(len, parser) {
            @Override
            public boolean hasNext() {
                return condition > 0;
            }

            @Override
            public byte[] next() {
                try {
                    byte[] element = parser.rdbLoadEncodedStringObject().first();
                    condition--;
                    return element;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        return (T) val;
    }

    @Override
    public <T> T applyZSet(RedisInputStream in, int version) throws IOException {
        /*
         * |    <len>     |       <content>       |        <score>       |
         * | 1 or 5 bytes |    string contents    |    double content    |
         */
        BaseRdbParser parser = new BaseRdbParser(in);

        long len = parser.rdbLoadLen().len;
        Iterator<ZSetEntry> val = new Iter<ZSetEntry>(len, parser) {
            @Override
            public boolean hasNext() {
                return condition > 0;
            }

            @Override
            public ZSetEntry next() {
                try {
                    byte[] element = parser.rdbLoadEncodedStringObject().first();
                    double score = parser.rdbLoadDoubleValue();
                    condition--;
                    return new ZSetEntry(element, score);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        return (T) val;
    }

    @Override
    public <T> T applyZSet2(RedisInputStream in, int version) throws IOException {
        /*
         * |    <len>     |       <content>       |        <score>       |
         * | 1 or 5 bytes |    string contents    |    binary double     |
         */
        BaseRdbParser parser = new BaseRdbParser(in);

        /* rdb version 8*/
        long len = parser.rdbLoadLen().len;
        Iterator<ZSetEntry> val = new Iter<ZSetEntry>(len, parser) {
            @Override
            public boolean hasNext() {
                return condition > 0;
            }

            @Override
            public ZSetEntry next() {
                try {
                    byte[] element = parser.rdbLoadEncodedStringObject().first();
                    double score = parser.rdbLoadBinaryDoubleValue();
                    condition--;
                    return new ZSetEntry(element, score);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        return (T) val;
    }

    @Override
    public <T> T applyHash(RedisInputStream in, int version) throws IOException {
        /*
         * |    <len>     |       <content>       |
         * | 1 or 5 bytes |    string contents    |
         */
        BaseRdbParser parser = new BaseRdbParser(in);

        long len = parser.rdbLoadLen().len;
        Iterator<Map.Entry<byte[], byte[]>> val = new Iter<Map.Entry<byte[], byte[]>>(len, parser) {
            @Override
            public boolean hasNext() {
                return condition > 0;
            }

            @Override
            public Map.Entry<byte[], byte[]> next() {
                try {
                    byte[] field = parser.rdbLoadEncodedStringObject().first();
                    byte[] value = parser.rdbLoadEncodedStringObject().first();
                    condition--;
                    return new AbstractMap.SimpleEntry<>(field, value);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        return (T) val;
    }

    @Override
    public <T> T applyHashZipMap(RedisInputStream in, int version) throws IOException {
        /*
         * |<zmlen> |   <len>     |"foo"    |    <len>   | <free> |   "bar" |<zmend> |
         * | 1 byte | 1 or 5 byte | content |1 or 5 byte | 1 byte | content | 1 byte |
         */
        BaseRdbParser parser = new BaseRdbParser(in);

        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        BaseRdbParser.LenHelper.zmlen(stream); // zmlen
        Iterator<Map.Entry<byte[], byte[]>> val = new HashZipMapIter(stream);
        return (T) val;
    }

    @Override
    public <T> T applyListZipList(RedisInputStream in, int version) throws IOException {
        /*
         * |<zlbytes>| <zltail>| <zllen>| <entry> ...<entry> | <zlend>|
         * | 4 bytes | 4 bytes | 2bytes | zipListEntry ...   | 1byte  |
         */
        BaseRdbParser parser = new BaseRdbParser(in);

        final RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
        BaseRdbParser.LenHelper.zltail(stream); // zltail
        int zllen = BaseRdbParser.LenHelper.zllen(stream);
        Iterator<byte[]> val = new Iter<byte[]>(zllen, null) {
            @Override
            public boolean hasNext() {
                if (condition > 0) return true;
                try {
                    int zlend = BaseRdbParser.LenHelper.zlend(stream);
                    if (zlend != 255) {
                        throw new AssertionError("zlend expect 255 but " + zlend);
                    }
                    return false;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public byte[] next() {
                try {
                    byte[] e = BaseRdbParser.StringHelper.zipListEntry(stream);
                    condition--;
                    return e;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        return (T) val;
    }

    @Override
    public <T> T applySetIntSet(RedisInputStream in, int version) throws IOException {
        /*
         * |<encoding>| <length-of-contents>|              <contents>                            |
         * | 4 bytes  |            4 bytes  | 2 bytes element| 4 bytes element | 8 bytes element |
         */
        BaseRdbParser parser = new BaseRdbParser(in);

        final RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        final int encoding = BaseRdbParser.LenHelper.encoding(stream);
        long lenOfContent = BaseRdbParser.LenHelper.lenOfContent(stream);
        Iterator<byte[]> val = new Iter<byte[]>(lenOfContent, null) {
            @Override
            public boolean hasNext() {
                return condition > 0;
            }

            @Override
            public byte[] next() {
                try {
                    switch (encoding) {
                        case 2:
                            String element = String.valueOf(stream.readInt(2));
                            condition--;
                            return element.getBytes();
                        case 4:
                            element = String.valueOf(stream.readInt(4));
                            condition--;
                            return element.getBytes();
                        case 8:
                            element = String.valueOf(stream.readLong(8));
                            condition--;
                            return element.getBytes();
                        default:
                            throw new AssertionError("expect encoding [2,4,8] but:" + encoding);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        return (T) val;
    }

    @Override
    public <T> T applyZSetZipList(RedisInputStream in, int version) throws IOException {
        /*
         * |<zlbytes>| <zltail>| <zllen>| <entry> ...<entry> | <zlend>|
         * | 4 bytes | 4 bytes | 2bytes | zipListEntry ...   | 1byte  |
         */
        BaseRdbParser parser = new BaseRdbParser(in);

        final RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
        BaseRdbParser.LenHelper.zltail(stream); // zltail
        int zllen = BaseRdbParser.LenHelper.zllen(stream);
        Iterator<ZSetEntry> val = new Iter<ZSetEntry>(zllen, null) {
            @Override
            public boolean hasNext() {
                if (condition > 0) return true;
                try {
                    int zlend = BaseRdbParser.LenHelper.zlend(stream);
                    if (zlend != 255) {
                        throw new AssertionError("zlend expect 255 but " + zlend);
                    }
                    return false;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public ZSetEntry next() {
                try {
                    byte[] element = BaseRdbParser.StringHelper.zipListEntry(stream);
                    condition--;
                    double score = Double.valueOf(Strings.toString(BaseRdbParser.StringHelper.zipListEntry(stream)));
                    condition--;
                    return new ZSetEntry(element, score);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        return (T) val;
    }

    @Override
    public <T> T applyHashZipList(RedisInputStream in, int version) throws IOException {
        /*
         * |<zlbytes>| <zltail>| <zllen>| <entry> ...<entry> | <zlend>|
         * | 4 bytes | 4 bytes | 2bytes | zipListEntry ...   | 1byte  |
         */
        BaseRdbParser parser = new BaseRdbParser(in);

        final RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
        BaseRdbParser.LenHelper.zltail(stream); // zltail
        int zllen = BaseRdbParser.LenHelper.zllen(stream);
        Iterator<Map.Entry<byte[], byte[]>> val = new Iter<Map.Entry<byte[], byte[]>>(zllen, null) {
            @Override
            public boolean hasNext() {
                if (condition > 0) return true;
                try {
                    int zlend = BaseRdbParser.LenHelper.zlend(stream);
                    if (zlend != 255) {
                        throw new AssertionError("zlend expect 255 but " + zlend);
                    }
                    return false;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public Map.Entry<byte[], byte[]> next() {
                try {
                    byte[] field = BaseRdbParser.StringHelper.zipListEntry(stream);
                    condition--;
                    byte[] value = BaseRdbParser.StringHelper.zipListEntry(stream);
                    condition--;
                    return new AbstractMap.SimpleEntry<>(field, value);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        return (T) val;
    }

    @Override
    public <T> T applyListQuickList(RedisInputStream in, int version) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);

        long len = parser.rdbLoadLen().len;
        Iterator<byte[]> val = new QuickListIter(len, parser);
        return (T) val;
    }

    private static abstract class Iter<T> implements Iterator<T> {

        protected long condition;
        protected final BaseRdbParser parser;

        private Iter(long condition, BaseRdbParser parser) {
            this.condition = condition;
            this.parser = parser;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class HashZipMapIter extends Iter<Map.Entry<byte[], byte[]>> {

        protected int zmEleLen;
        protected final RedisInputStream stream;

        private HashZipMapIter(RedisInputStream stream) {
            super(0, null);
            this.stream = stream;
        }

        @Override
        public boolean hasNext() {
            try {
                return (this.zmEleLen = BaseRdbParser.LenHelper.zmElementLen(stream)) != 255;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public Map.Entry<byte[], byte[]> next() {
            try {
                byte[] field = BaseRdbParser.StringHelper.bytes(stream, zmEleLen);
                this.zmEleLen = BaseRdbParser.LenHelper.zmElementLen(stream);
                if (this.zmEleLen == 255) {
                    return new AbstractMap.SimpleEntry<>(field, null);
                }
                int free = BaseRdbParser.LenHelper.free(stream);
                byte[] value = BaseRdbParser.StringHelper.bytes(stream, zmEleLen);
                BaseRdbParser.StringHelper.skip(stream, free);
                return new AbstractMap.SimpleEntry<>(field, value);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static class QuickListIter extends Iter<byte[]> {

        protected int zllen = -1;
        protected RedisInputStream stream;

        private QuickListIter(long condition, BaseRdbParser parser) {
            super(condition, parser);
        }

        @Override
        public boolean hasNext() {
            return zllen > 0 || condition > 0;
        }

        @Override
        public byte[] next() {
            try {
                if (zllen == -1 && condition > 0) {
                    this.stream = new RedisInputStream(parser.rdbGenericLoadStringObject(RDB_LOAD_NONE));
                    BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
                    BaseRdbParser.LenHelper.zltail(stream); // zltail
                    this.zllen = BaseRdbParser.LenHelper.zllen(stream);
                    if (zllen == 0) {
                        int zlend = BaseRdbParser.LenHelper.zlend(stream);
                        if (zlend != 255) {
                            throw new AssertionError("zlend expect 255 but " + zlend);
                        }
                        zllen = -1;
                        condition--;
                    }
                    if (hasNext()) return next();
                    throw new IllegalStateException("end of iterator");
                } else {
                    byte[] e = BaseRdbParser.StringHelper.zipListEntry(stream);
                    zllen--;
                    if (zllen == 0) {
                        int zlend = BaseRdbParser.LenHelper.zlend(stream);
                        if (zlend != 255) {
                            throw new AssertionError("zlend expect 255 but " + zlend);
                        }
                        zllen = -1;
                        condition--;
                    }
                    return e;
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
