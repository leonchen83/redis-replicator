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
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.RdbParser;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by leon on 8/13/16.
 */
public class RedisRdbReplicator extends AbstractReplicator {

    public RedisRdbReplicator(File file, Configuration configuration) throws FileNotFoundException {
        //bug fix http://git.oschina.net/leonchen83/redis-replicator/issues/2
        this(new FileInputStream(file), configuration);
    }

    public RedisRdbReplicator(InputStream in, Configuration configuration) {
        this.configuration = configuration;
        this.inputStream = new RedisInputStream(in, this.configuration.getBufferSize());
        this.eventQueue = new ArrayBlockingQueue<>(this.configuration.getEventQueueSize());
    }

    @Override
    public void open() throws IOException {
        try {
            doOpen();
        } catch (IOException e) {
        } finally {
            close();
        }
    }

    private void doOpen() throws IOException {
        worker.start();
        RdbParser parser = new RdbParser(inputStream, this);
        parser.parse();
    }

    @Override
    public void close() throws IOException {
        doClose();
    }

    @Override
    public void addCommandFilter(CommandFilter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeCommandFilter(CommandFilter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addCommandListener(CommandListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeCommandListener(CommandListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Command> void addCommandParser(CommandName command, CommandParser<T> parser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Command> void removeCommandParser(CommandName command, CommandParser<T> parser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doCommandHandler(Command command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean doCommandFilter(Command command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void builtInCommandParserRegister() {
        throw new UnsupportedOperationException();
    }
}
