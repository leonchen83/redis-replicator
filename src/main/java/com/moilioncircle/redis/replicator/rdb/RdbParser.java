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

import com.moilioncircle.redis.replicator.AbstractReplicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.PostFullSyncEvent;
import com.moilioncircle.redis.replicator.event.PreFullSyncEvent;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_AUX;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_EOF;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_EXPIRETIME;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_EXPIRETIME_MS;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_RESIZEDB;
import static com.moilioncircle.redis.replicator.Constants.RDB_OPCODE_SELECTDB;
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
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_STRING;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_2;
import static com.moilioncircle.redis.replicator.Constants.RDB_TYPE_ZSET_ZIPLIST;
import static com.moilioncircle.redis.replicator.Status.CONNECTED;

/**
 * Redis RDB format
 * <p>
 *
 * @author Leon Chen
 * @see <a href="https://github.com/antirez/redis/blob/3.0/src/rdb.c">rdb.c</a>
 * @see <a href="https://github.com/leonchen83/redis-replicator/wiki/RDB-dump-data-format">Redis rdb dump data format</a>
 * @since 2.1.0
 */
public class RdbParser {

    protected final RedisInputStream in;
    protected final RdbVisitor rdbVisitor;
    protected final AbstractReplicator replicator;
    protected static final Logger logger = LoggerFactory.getLogger(RdbParser.class);

    public RdbParser(RedisInputStream in, AbstractReplicator replicator) {
        this.in = in;
        this.replicator = replicator;
        this.rdbVisitor = this.replicator.getRdbVisitor();
    }

    /**
     * The RDB E-BNF
     * <p>
     * RDB        =    'REDIS', $version, [AUX], {SELECTDB, [RESIZEDB], {RECORD}}, '0xFF', [$checksum];
     * <p>
     * RECORD     =    [EXPIRED], KEY, VALUE;
     * <p>
     * SELECTDB   =    '0xFE', $length;
     * <p>
     * AUX        =    {'0xFA', $string, $string};            (*Introduced in rdb version 7*)
     * <p>
     * RESIZEDB   =    '0xFB', $length, $length;              (*Introduced in rdb version 7*)
     * <p>
     * EXPIRED    =    ('0xFD', $second) | ('0xFC', $millisecond);
     * <p>
     * KEY        =    $string;
     * <p>
     * VALUE      =    $value-type, ( $string
     * <p>
     * | $list
     * <p>
     * | $set
     * <p>
     * | $zset
     * <p>
     * | $hash
     * <p>
     * | $zset2                  (*Introduced in rdb version 8*)
     * <p>
     * | $module                 (*Introduced in rdb version 8*)
     * <p>
     * | $module2                (*Introduced in rdb version 8*)
     * <p>
     * | $hashzipmap
     * <p>
     * | $listziplist
     * <p>
     * | $setintset
     * <p>
     * | $zsetziplist
     * <p>
     * | $hashziplist
     * <p>
     * | $listquicklist);        (*Introduced in rdb version 7*)
     * <p>
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
        this.replicator.submitEvent(new PreFullSyncEvent());
        rdbVisitor.applyMagic(in);
        int version = rdbVisitor.applyVersion(in);
        DB db = null;
        /*
         * rdb
         */
        loop:
        while (this.replicator.getStatus() == CONNECTED) {
            int type = rdbVisitor.applyType(in);
            Event event = null;
            switch (type) {
                case RDB_OPCODE_EXPIRETIME:
                    event = rdbVisitor.applyExpireTime(in, db, version);
                    break;
                case RDB_OPCODE_EXPIRETIME_MS:
                    event = rdbVisitor.applyExpireTimeMs(in, db, version);
                    break;
                case RDB_OPCODE_AUX:
                    event = rdbVisitor.applyAux(in, version);
                    break;
                case RDB_OPCODE_RESIZEDB:
                    rdbVisitor.applyResizeDB(in, db, version);
                    break;
                case RDB_OPCODE_SELECTDB:
                    db = rdbVisitor.applySelectDB(in, version);
                    break;
                case RDB_OPCODE_EOF:
                    long checksum = rdbVisitor.applyEof(in, version);
                    this.replicator.submitEvent(new PostFullSyncEvent(checksum));
                    break loop;
                case RDB_TYPE_STRING:
                    event = rdbVisitor.applyString(in, db, version);
                    break;
                case RDB_TYPE_LIST:
                    event = rdbVisitor.applyList(in, db, version);
                    break;
                case RDB_TYPE_SET:
                    event = rdbVisitor.applySet(in, db, version);
                    break;
                case RDB_TYPE_ZSET:
                    event = rdbVisitor.applyZSet(in, db, version);
                    break;
                case RDB_TYPE_ZSET_2:
                    event = rdbVisitor.applyZSet2(in, db, version);
                    break;
                case RDB_TYPE_HASH:
                    event = rdbVisitor.applyHash(in, db, version);
                    break;
                case RDB_TYPE_HASH_ZIPMAP:
                    event = rdbVisitor.applyHashZipMap(in, db, version);
                    break;
                case RDB_TYPE_LIST_ZIPLIST:
                    event = rdbVisitor.applyListZipList(in, db, version);
                    break;
                case RDB_TYPE_SET_INTSET:
                    event = rdbVisitor.applySetIntSet(in, db, version);
                    break;
                case RDB_TYPE_ZSET_ZIPLIST:
                    event = rdbVisitor.applyZSetZipList(in, db, version);
                    break;
                case RDB_TYPE_HASH_ZIPLIST:
                    event = rdbVisitor.applyHashZipList(in, db, version);
                    break;
                case RDB_TYPE_LIST_QUICKLIST:
                    event = rdbVisitor.applyListQuickList(in, db, version);
                    break;
                case RDB_TYPE_MODULE:
                    event = rdbVisitor.applyModule(in, db, version);
                    break;
                case RDB_TYPE_MODULE_2:
                    event = rdbVisitor.applyModule2(in, db, version);
                    break;
                default:
                    throw new AssertionError("unexpected value type:" + type + ", check your ModuleParser or ValueIterableRdbVisitor.");
            }
            if (event == null) continue;
            if (replicator.verbose() && logger.isDebugEnabled()) logger.debug("{}", event);
            this.replicator.submitEvent(event);
        }
        return in.total();
    }
}

