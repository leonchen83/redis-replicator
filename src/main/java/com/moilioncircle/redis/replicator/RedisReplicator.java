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
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.AuxFieldListener;
import com.moilioncircle.redis.replicator.rdb.RdbFilter;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.RdbVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by leon on 8/13/16.
 */
public class RedisReplicator implements Replicator {
    protected final Replicator replicator;

    public RedisReplicator(File file, Configuration configuration) throws FileNotFoundException {
        this(file, configuration, true);
    }

    public RedisReplicator(InputStream in, Configuration configuration) {
        this(in, configuration, true);
    }

    public RedisReplicator(File file, Configuration configuration, boolean rdb) throws FileNotFoundException {
        replicator = rdb ? new RedisRdbReplicator(file, configuration) : new RedisAofReplicator(file, configuration);
    }

    public RedisReplicator(InputStream in, Configuration configuration, boolean rdb) {
        replicator = rdb ? new RedisRdbReplicator(in, configuration) : new RedisAofReplicator(in, configuration);
    }

    public RedisReplicator(String host, int port, Configuration configuration) {
        replicator = new RedisSocketReplicator(host, port, configuration);
    }

    @Override
    public boolean addRdbFilter(RdbFilter filter) {
        return replicator.addRdbFilter(filter);
    }

    @Override
    public boolean removeRdbFilter(RdbFilter filter) {
        return replicator.removeRdbFilter(filter);
    }

    @Override
    public boolean addRdbListener(RdbListener listener) {
        return replicator.addRdbListener(listener);
    }

    @Override
    public boolean removeRdbListener(RdbListener listener) {
        return replicator.removeRdbListener(listener);
    }

    @Override
    public boolean addAuxFieldListener(AuxFieldListener listener) {
        return replicator.addAuxFieldListener(listener);
    }

    @Override
    public boolean removeAuxFieldListener(AuxFieldListener listener) {
        return replicator.removeAuxFieldListener(listener);
    }

    @Override
    public boolean addRdbRawByteListener(RawByteListener listener) {
        return replicator.addRdbRawByteListener(listener);
    }

    @Override
    public boolean removeRdbRawByteListener(RawByteListener listener) {
        return replicator.removeRdbRawByteListener(listener);
    }

    @Override
    public void builtInCommandParserRegister() {
        replicator.builtInCommandParserRegister();
    }

    @Override
    public CommandParser<? extends Command> getCommandParser(CommandName command) {
        return replicator.getCommandParser(command);
    }

    @Override
    public <T extends Command> void addCommandParser(CommandName command, CommandParser<T> parser) {
        replicator.addCommandParser(command, parser);
    }

    @Override
    public CommandParser<? extends Command> removeCommandParser(CommandName command) {
        return replicator.removeCommandParser(command);
    }

    @Override
    public ModuleParser<? extends Module> getModuleParser(String moduleName, int moduleVersion) {
        return replicator.getModuleParser(moduleName, moduleVersion);
    }

    @Override
    public <T extends Module> void addModuleParser(String moduleName, int moduleVersion, ModuleParser<T> parser) {
        replicator.addModuleParser(moduleName, moduleVersion, parser);
    }

    @Override
    public ModuleParser<? extends Module> removeModuleParser(String moduleName, int moduleVersion) {
        return replicator.removeModuleParser(moduleName, moduleVersion);
    }

    public void setRdbVisitor(RdbVisitor rdbVisitor) {
        replicator.setRdbVisitor(rdbVisitor);
    }

    public RdbVisitor getRdbVisitor() {
        return replicator.getRdbVisitor();
    }

    @Override
    public boolean addCommandFilter(CommandFilter filter) {
        return replicator.addCommandFilter(filter);
    }

    @Override
    public boolean removeCommandFilter(CommandFilter filter) {
        return replicator.removeCommandFilter(filter);
    }

    @Override
    public boolean addCommandListener(CommandListener listener) {
        return replicator.addCommandListener(listener);
    }

    @Override
    public boolean removeCommandListener(CommandListener listener) {
        return replicator.removeCommandListener(listener);
    }

    @Override
    public boolean addCloseListener(CloseListener listener) {
        return replicator.addCloseListener(listener);
    }

    @Override
    public boolean removeCloseListener(CloseListener listener) {
        return replicator.removeCloseListener(listener);
    }

    @Override
    public boolean addExceptionListener(ExceptionListener listener) {
        return replicator.addExceptionListener(listener);
    }

    @Override
    public boolean removeExceptionListener(ExceptionListener listener) {
        return replicator.removeExceptionListener(listener);
    }

    @Override
    public boolean verbose() {
        return replicator.verbose();
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
