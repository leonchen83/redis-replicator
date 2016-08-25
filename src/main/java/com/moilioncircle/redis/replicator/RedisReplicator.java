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
import com.moilioncircle.redis.replicator.rdb.RdbFilter;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by leon on 8/13/16.
 */
public class RedisReplicator implements Replicator {
    private final Replicator replicator;

    public RedisReplicator(File file, Configuration configuration) throws FileNotFoundException {
        replicator = new RedisFileReplicator(file, configuration);
    }

    public RedisReplicator(InputStream in, Configuration configuration) {
        replicator = new RedisFileReplicator(in, configuration);
    }

    public RedisReplicator(String host, int port, Configuration configuration) throws IOException {
        replicator = new RedisSocketReplicator(host, port, configuration);
    }

    @Override
    public void addRdbFilter(RdbFilter filter) {
        replicator.addRdbFilter(filter);
    }

    @Override
    public void removeRdbFilter(RdbFilter filter) {
        replicator.removeRdbFilter(filter);
    }

    @Override
    public void addRdbListener(RdbListener listener) {
        replicator.addRdbListener(listener);
    }

    @Override
    public void removeRdbListener(RdbListener listener) {
        replicator.removeRdbListener(listener);
    }

    @Override
    public void buildInCommandParserRegister() {
        replicator.buildInCommandParserRegister();
    }

    @Override
    public <T extends Command> void addCommandParser(CommandName command, CommandParser<T> parser) {
        replicator.addCommandParser(command, parser);
    }

    @Override
    public <T extends Command> void removeCommandParser(CommandName command, CommandParser<T> parser) {
        replicator.removeCommandParser(command, parser);
    }

    @Override
    public void addCommandFilter(CommandFilter filter) {
        replicator.addCommandFilter(filter);
    }

    @Override
    public void removeCommandFilter(CommandFilter filter) {
        replicator.removeCommandFilter(filter);
    }

    @Override
    public void addCommandListener(CommandListener listener) {
        replicator.addCommandListener(listener);
    }

    @Override
    public void removeCommandListener(CommandListener listener) {
        replicator.removeCommandListener(listener);
    }

    @Override
    public void doCommandHandler(Command command) {
        replicator.doCommandHandler(command);
    }

    @Override
    public boolean doCommandFilter(Command command) {
        return replicator.doCommandFilter(command);
    }

    @Override
    public void doRdbHandler(KeyValuePair<?> kv) {
        replicator.doRdbHandler(kv);
    }

    @Override
    public boolean doRdbFilter(KeyValuePair<?> kv) {
        return replicator.doRdbFilter(kv);
    }

    @Override
    public void doPreFullSync() {
        replicator.doPreFullSync();
    }

    @Override
    public void doPostFullSync() {
        replicator.doPostFullSync();
    }

    @Override
    public void open() throws IOException {
        replicator.open();
    }

    @Override
    public void close() throws IOException {
        replicator.close();
    }
}
