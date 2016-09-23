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
import com.moilioncircle.redis.replicator.event.PostFullSyncEvent;
import com.moilioncircle.redis.replicator.event.PreFullSyncEvent;
import com.moilioncircle.redis.replicator.io.RedisInputStream;

import java.io.IOException;

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
public class RdbParser extends AbstractRdbParser {

    public RdbParser(RedisInputStream in, AbstractReplicator replicator) {
        super(in, replicator);
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
        try {
            /*
         * ----------------------------
         * 52 45 44 49 53              # Magic String "REDIS"
         * 30 30 30 33                 # RDB Version Number in big endian. In this case, version = 0003 = 3
         * ----------------------------
         */
            String magicString = StringHelper.str(in, 5);//REDIS
            if (!magicString.equals("REDIS")) {
                logger.error("Can't read MAGIC STRING [REDIS] ,value:" + magicString);
                return in.total();
            }
            int version = Integer.parseInt(StringHelper.str(in, 4));//0006 or 0007
            AbstractRdbParser rdbParser;
            switch (version) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    rdbParser = new Rdb6Parser(in, replicator);
                    break;
                case 7:
                    rdbParser = new Rdb7Parser(in, replicator);
                    break;
                default:
                    logger.error("Can't handle RDB format version " + version);
                    return in.total();
            }
            this.replicator.submitEvent(new PreFullSyncEvent());
            long checksum = rdbParser.rdbLoad(version);
            this.replicator.submitEvent(new PostFullSyncEvent(checksum));
            return in.total();
        } catch (InterruptedException e) {
            logger.error(e);
            Thread.currentThread().interrupt();
            return -1;
        }
    }
}

