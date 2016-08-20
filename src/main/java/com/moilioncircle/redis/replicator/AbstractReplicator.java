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

package com.moilioncircle.redis.replicator;

import com.moilioncircle.redis.replicator.cmd.*;
import com.moilioncircle.redis.replicator.cmd.impl.*;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.RdbFilter;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by leon on 8/13/16.
 */
public abstract class AbstractReplicator implements Replicator {
    protected RedisInputStream inputStream;
    protected final ConcurrentHashMap<CommandName, CommandParser<? extends Command>> commands = new ConcurrentHashMap<>();
    protected final List<CommandFilter> filters = new CopyOnWriteArrayList<>();
    protected final List<CommandListener> listeners = new CopyOnWriteArrayList<>();
    protected final List<RdbFilter> rdbFilters = new CopyOnWriteArrayList<>();
    protected final List<RdbListener> rdbListeners = new CopyOnWriteArrayList<>();

    @Override
    public void doCommandHandler(Command command) {
        for (CommandListener listener : listeners) {
            listener.handle(this, command);
        }
    }

    @Override
    public boolean doCommandFilter(Command command) {
        for (CommandFilter filter : filters) {
            if (!filter.accept(command)) return false;
        }
        return true;
    }

    @Override
    public void doRdbHandler(KeyValuePair<?> kv) {
        for (RdbListener listener : rdbListeners) {
            listener.handle(this, kv);
        }
    }

    @Override
    public boolean doRdbFilter(KeyValuePair<?> kv) {
        for (RdbFilter filter : rdbFilters) {
            if (!filter.accept(kv)) return false;
        }
        return true;
    }

    @Override
    public <T extends Command> void addCommandParser(CommandName command, CommandParser<T> parser) {
        commands.putIfAbsent(command, parser);
    }

    @Override
    public <T extends Command> void removeCommandParser(CommandName command, CommandParser<T> parser) {
        commands.remove(command);
    }

    @Override
    public void addCommandFilter(CommandFilter filter) {
        filters.add(filter);
    }

    @Override
    public void removeCommandFilter(CommandFilter filter) {
        filters.remove(filter);
    }

    @Override
    public void addCommandListener(CommandListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeCommandListener(CommandListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void addRdbFilter(RdbFilter filter) {
        rdbFilters.add(filter);
    }

    @Override
    public void removeRdbFilter(RdbFilter filter) {
        rdbFilters.remove(filter);
    }

    @Override
    public void addRdbListener(RdbListener listener) {
        rdbListeners.add(listener);
    }

    @Override
    public void removeRdbListener(RdbListener listener) {
        rdbListeners.remove(listener);
    }

    @Override
    public void buildInCommandParserRegister() {
        addCommandParser(CommandName.name("PING"), new PingParser());
        addCommandParser(CommandName.name("APPEND"), new AppendParser());
        addCommandParser(CommandName.name("SET"), new SetParser());
        addCommandParser(CommandName.name("SETEX"), new SetExParser());
        addCommandParser(CommandName.name("MSET"), new MSetParser());
        addCommandParser(CommandName.name("DEL"), new DelParser());
        addCommandParser(CommandName.name("SADD"), new SAddParser());
        addCommandParser(CommandName.name("HMSET"), new HMSetParser());
        addCommandParser(CommandName.name("HSET"), new HSetParser());
        addCommandParser(CommandName.name("LSET"), new LSetParser());
        addCommandParser(CommandName.name("EXPIRE"), new ExpireParser());
        addCommandParser(CommandName.name("EXPIREAT"), new ExpireAtParser());
        addCommandParser(CommandName.name("GETSET"), new GetSetParser());
        addCommandParser(CommandName.name("HSETNX"), new HSetNxParser());
        addCommandParser(CommandName.name("MSETNX"), new MSetNxParser());
        addCommandParser(CommandName.name("PSETEX"), new PSetExParser());
        addCommandParser(CommandName.name("SETNX"), new SetNxParser());
        addCommandParser(CommandName.name("SETRANGE"), new SetRangeParser());
        addCommandParser(CommandName.name("HDEL"), new HDelParser());
        addCommandParser(CommandName.name("HKEYS"), new HKeysParser());
        addCommandParser(CommandName.name("HVALS"), new HValsParser());
        addCommandParser(CommandName.name("LPOP"), new LPopParser());
        addCommandParser(CommandName.name("LPUSH"), new LPushParser());
        addCommandParser(CommandName.name("LPUSHX"), new LPushXParser());
        addCommandParser(CommandName.name("LRem"), new LRemParser());
        addCommandParser(CommandName.name("RPOP"), new RPopParser());
        addCommandParser(CommandName.name("RPUSH"), new RPushParser());
        addCommandParser(CommandName.name("RPUSHX"), new RPushXParser());
        addCommandParser(CommandName.name("ZREM"), new ZRemParser());
        addCommandParser(CommandName.name("RENAME"), new RenameParser());
        addCommandParser(CommandName.name("INCR"), new IncrParser());
        addCommandParser(CommandName.name("DECR"), new DecrParser());
        addCommandParser(CommandName.name("INCRBY"), new IncrByParser());
        addCommandParser(CommandName.name("PERSIST"), new PersistParser());
        addCommandParser(CommandName.name("SELECT"), new SelectParser());
        addCommandParser(CommandName.name("FLUSHALL"), new FlushAllParser());
        addCommandParser(CommandName.name("FLUSHDB"), new FlushDBParser());
        addCommandParser(CommandName.name("HINCRBY"), new HIncrByParser());
        addCommandParser(CommandName.name("ZINCRBY"), new ZIncrByParser());
        addCommandParser(CommandName.name("MOVE"), new MoveParser());
        addCommandParser(CommandName.name("SMOVE"), new SMoveParser());
        addCommandParser(CommandName.name("PFADD"), new PFAddParser());
        addCommandParser(CommandName.name("PFCOUNT"), new PFCountParser());
        addCommandParser(CommandName.name("PFMERGE"), new PFMergeParser());
        addCommandParser(CommandName.name("SDIFFSTORE"), new SDiffStoreParser());
        addCommandParser(CommandName.name("SINTERSTORE"), new SInterStoreParser());
        addCommandParser(CommandName.name("SUNIONSTORE"), new SUnionStoreParser());
        addCommandParser(CommandName.name("ZADD"), new ZAddParser());
        addCommandParser(CommandName.name("ZINTERSTORE"), new ZInterStoreParser());
        addCommandParser(CommandName.name("ZUNIONSTORE"), new ZUnionStoreParser());
        addCommandParser(CommandName.name("BRPOPLPUSH"), new BRPopLPushParser());
        addCommandParser(CommandName.name("LINSERT"), new LInsertParser());
        addCommandParser(CommandName.name("RENAMENX"), new RenameNxParser());
        addCommandParser(CommandName.name("RESTORE"), new RestoreParser());
        addCommandParser(CommandName.name("PEXPIRE"), new PExpireParser());
        addCommandParser(CommandName.name("PEXPIREAT"), new PExpireAtParser());
        addCommandParser(CommandName.name("GEOADD"), new GEOAddParser());
        addCommandParser(CommandName.name("EVAL"), new EvalParser());
        addCommandParser(CommandName.name("SCRIPT"), new ScriptParser());
        addCommandParser(CommandName.name("PUBLISH"), new PublishParser());
    }
}
