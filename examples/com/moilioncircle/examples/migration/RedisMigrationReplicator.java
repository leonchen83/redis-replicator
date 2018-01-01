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

package com.moilioncircle.examples.migration;

import com.moilioncircle.examples.migration.cmd.DefaultCommandParser;
import com.moilioncircle.examples.migration.rdb.MigrationRdbVisitor;
import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisSocketReplicator;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.parser.PingParser;

/**
 * @author Leon Chen
 * @since 2.4.3
 */
public class RedisMigrationReplicator extends RedisSocketReplicator {

    public RedisMigrationReplicator(String host, int port, Configuration configuration) {
        super(host, port, configuration);
        setRdbVisitor(new MigrationRdbVisitor(this));
    }

    @Override
    public void builtInCommandParserRegister() {
        addCommandParser(CommandName.name("PING"), new PingParser());
        addCommandParser(CommandName.name("APPEND"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SET"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SETEX"), new DefaultCommandParser());
        addCommandParser(CommandName.name("MSET"), new DefaultCommandParser());
        addCommandParser(CommandName.name("DEL"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SADD"), new DefaultCommandParser());
        addCommandParser(CommandName.name("HMSET"), new DefaultCommandParser());
        addCommandParser(CommandName.name("HSET"), new DefaultCommandParser());
        addCommandParser(CommandName.name("LSET"), new DefaultCommandParser());
        addCommandParser(CommandName.name("EXPIRE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("EXPIREAT"), new DefaultCommandParser());
        addCommandParser(CommandName.name("GETSET"), new DefaultCommandParser());
        addCommandParser(CommandName.name("HSETNX"), new DefaultCommandParser());
        addCommandParser(CommandName.name("MSETNX"), new DefaultCommandParser());
        addCommandParser(CommandName.name("PSETEX"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SETNX"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SETRANGE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("HDEL"), new DefaultCommandParser());
        addCommandParser(CommandName.name("LPOP"), new DefaultCommandParser());
        addCommandParser(CommandName.name("LPUSH"), new DefaultCommandParser());
        addCommandParser(CommandName.name("LPUSHX"), new DefaultCommandParser());
        addCommandParser(CommandName.name("LRem"), new DefaultCommandParser());
        addCommandParser(CommandName.name("RPOP"), new DefaultCommandParser());
        addCommandParser(CommandName.name("RPUSH"), new DefaultCommandParser());
        addCommandParser(CommandName.name("RPUSHX"), new DefaultCommandParser());
        addCommandParser(CommandName.name("ZREM"), new DefaultCommandParser());
        addCommandParser(CommandName.name("RENAME"), new DefaultCommandParser());
        addCommandParser(CommandName.name("INCR"), new DefaultCommandParser());
        addCommandParser(CommandName.name("DECR"), new DefaultCommandParser());
        addCommandParser(CommandName.name("INCRBY"), new DefaultCommandParser());
        addCommandParser(CommandName.name("DECRBY"), new DefaultCommandParser());
        addCommandParser(CommandName.name("PERSIST"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SELECT"), new DefaultCommandParser());
        addCommandParser(CommandName.name("FLUSHALL"), new DefaultCommandParser());
        addCommandParser(CommandName.name("FLUSHDB"), new DefaultCommandParser());
        addCommandParser(CommandName.name("HINCRBY"), new DefaultCommandParser());
        addCommandParser(CommandName.name("ZINCRBY"), new DefaultCommandParser());
        addCommandParser(CommandName.name("MOVE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SMOVE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("PFADD"), new DefaultCommandParser());
        addCommandParser(CommandName.name("PFCOUNT"), new DefaultCommandParser());
        addCommandParser(CommandName.name("PFMERGE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SDIFFSTORE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SINTERSTORE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SUNIONSTORE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("ZADD"), new DefaultCommandParser());
        addCommandParser(CommandName.name("ZINTERSTORE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("ZUNIONSTORE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("BRPOPLPUSH"), new DefaultCommandParser());
        addCommandParser(CommandName.name("LINSERT"), new DefaultCommandParser());
        addCommandParser(CommandName.name("RENAMENX"), new DefaultCommandParser());
        addCommandParser(CommandName.name("RESTORE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("PEXPIRE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("PEXPIREAT"), new DefaultCommandParser());
        addCommandParser(CommandName.name("GEOADD"), new DefaultCommandParser());
        addCommandParser(CommandName.name("EVAL"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SCRIPT"), new DefaultCommandParser());
        addCommandParser(CommandName.name("PUBLISH"), new DefaultCommandParser());
        addCommandParser(CommandName.name("BITOP"), new DefaultCommandParser());
        addCommandParser(CommandName.name("BITFIELD"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SETBIT"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SREM"), new DefaultCommandParser());
        addCommandParser(CommandName.name("UNLINK"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SWAPDB"), new DefaultCommandParser());
        addCommandParser(CommandName.name("MULTI"), new DefaultCommandParser());
        addCommandParser(CommandName.name("EXEC"), new DefaultCommandParser());
        addCommandParser(CommandName.name("ZREMRANGEBYSCORE"), new DefaultCommandParser());
        addCommandParser(CommandName.name("ZREMRANGEBYRANK"), new DefaultCommandParser());
        addCommandParser(CommandName.name("ZREMRANGEBYLEX"), new DefaultCommandParser());
        addCommandParser(CommandName.name("LTRIM"), new DefaultCommandParser());
        addCommandParser(CommandName.name("SORT"), new DefaultCommandParser());
        addCommandParser(CommandName.name("RPOPLPUSH"), new DefaultCommandParser());
    }
}
