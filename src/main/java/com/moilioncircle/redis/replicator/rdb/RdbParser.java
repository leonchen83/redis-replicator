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
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
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
public class RdbParser implements RawByteListener {

    protected final RedisInputStream in;
    protected RdbEntityVisitor rdbEntityVisitor;
    protected final AbstractReplicator replicator;
    protected static final Log logger = LogFactory.getLog(RdbParser.class);
    protected final List<RawByteListener> listeners = new CopyOnWriteArrayList<>();

    public RdbParser(RedisInputStream in, AbstractReplicator replicator) {
        this.in = in;
        this.replicator = replicator;
        this.rdbEntityVisitor = this.replicator.getRdbEntityVisitor();
    }

    public void addRawByteListener(RawByteListener listener) {
        this.listeners.add(listener);
    }

    public void removeRawByteListener(RawByteListener listener) {
        this.listeners.remove(listener);
    }

    protected void notify(byte... bytes) {
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
            rdbEntityVisitor.applyMagic(in);
            int version = rdbEntityVisitor.applyVersion(in);
            this.replicator.submitEvent(new PreFullSyncEvent());
            DB db = null;
            long checksum;
            /**
             * rdb
             */
            loop:
            while (true) {
                int type = rdbEntityVisitor.applyType(in);
                Event event = null;
                switch (type) {
                    case RDB_OPCODE_EXPIRETIME:
                        event = rdbEntityVisitor.applyExpireTime(in, db, version);
                        break;
                    case RDB_OPCODE_EXPIRETIME_MS:
                        event = rdbEntityVisitor.applyExpireTimeMs(in, db, version);
                        break;
                    case RDB_OPCODE_AUX:
                        event = rdbEntityVisitor.applyAux(in, version);
                        break;
                    case RDB_OPCODE_RESIZEDB:
                        rdbEntityVisitor.applyResizeDB(in, db, version);
                        break;
                    case RDB_OPCODE_SELECTDB:
                        db = rdbEntityVisitor.applySelectDB(in, version);
                        break;
                    case RDB_OPCODE_EOF:
                        checksum = rdbEntityVisitor.applyEof(in, version);
                        break loop;
                    case RDB_TYPE_STRING:
                        event = rdbEntityVisitor.applyString(in, db, version);
                        break;
                    case RDB_TYPE_LIST:
                        event = rdbEntityVisitor.applyList(in, db, version);
                        break;
                    case RDB_TYPE_SET:
                        event = rdbEntityVisitor.applySet(in, db, version);
                        break;
                    case RDB_TYPE_ZSET:
                        event = rdbEntityVisitor.applyZSet(in, db, version);
                        break;
                    case RDB_TYPE_ZSET_2:
                        event = rdbEntityVisitor.applyZSet2(in, db, version);
                        break;
                    case RDB_TYPE_HASH:
                        event = rdbEntityVisitor.applyHash(in, db, version);
                        break;
                    case RDB_TYPE_HASH_ZIPMAP:
                        event = rdbEntityVisitor.applyHashZipMap(in, db, version);
                        break;
                    case RDB_TYPE_LIST_ZIPLIST:
                        event = rdbEntityVisitor.applyListZipList(in, db, version);
                        break;
                    case RDB_TYPE_SET_INTSET:
                        event = rdbEntityVisitor.applySetIntSet(in, db, version);
                        break;
                    case RDB_TYPE_ZSET_ZIPLIST:
                        event = rdbEntityVisitor.applyZSetZipList(in, db, version);
                        break;
                    case RDB_TYPE_HASH_ZIPLIST:
                        event = rdbEntityVisitor.applyHashZipList(in, db, version);
                        break;
                    case RDB_TYPE_LIST_QUICKLIST:
                        event = rdbEntityVisitor.applyListQuickList(in, db, version);
                        break;
                    case RDB_TYPE_MODULE:
                        event = rdbEntityVisitor.applyModule(in, db, version);
                        break;
                    default:
                        throw new AssertionError("Un-except value-type:" + type);
                }
                if (event == null) continue;
                if (replicator.verbose() && logger.isDebugEnabled()) logger.debug(event);
                this.replicator.submitEvent(event);
            }
            this.replicator.submitEvent(new PostFullSyncEvent(checksum));
            return in.total();
        } finally {
            in.removeRawByteListener(this);
        }
    }
}

