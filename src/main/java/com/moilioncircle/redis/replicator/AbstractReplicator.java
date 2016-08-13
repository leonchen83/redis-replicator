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
        addCommandParser(CommandName.name("MSET"), new MSetParser());
        addCommandParser(CommandName.name("DEL"), new DelParser());
        addCommandParser(CommandName.name("SADD"), new SAddParser());
    }
}
