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

package com.moilioncircle.redis.replicator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.RdbVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;

/**
 * @author Leon Chen
 * @since 3.0.0
 */
public class AsyncRedisReplicator implements AsyncReplicator {

    protected final Replicator replicator;

    public AsyncRedisReplicator(File file, FileType fileType, Configuration configuration) throws FileNotFoundException {
        this.replicator = new RedisReplicator(file, fileType, configuration);
    }

    public AsyncRedisReplicator(InputStream in, FileType fileType, Configuration configuration) {
        this.replicator = new RedisReplicator(in, fileType, configuration);
    }

    public AsyncRedisReplicator(String host, int port, Configuration configuration) {
        this.replicator = new RedisReplicator(host, port, configuration);
    }

    public AsyncRedisReplicator(String uri) throws URISyntaxException, IOException {
        this.replicator = new RedisReplicator(uri);
    }

    public AsyncRedisReplicator(RedisURI uri) throws IOException {
        this.replicator = new RedisReplicator(uri);
    }

    /**
     * @param uri redis uri.
     * @param sslConfiguration ssl configuration.
     * @throws IOException read timeout or read EOF.
     * @see RedisURI
     * @see SslConfiguration
     * @since 3.4.2
     */
    public AsyncRedisReplicator(String uri, SslConfiguration sslConfiguration) throws URISyntaxException, IOException {
        this.replicator = new RedisReplicator(uri, sslConfiguration);
    }

    /**
     * @param uri redis uri.
     * @param sslConfiguration ssl configuration.
     * @throws IOException read timeout or read EOF.
     * @see RedisURI
     * @see SslConfiguration
     * @since 3.4.2
     */
    public AsyncRedisReplicator(RedisURI uri, SslConfiguration sslConfiguration) throws IOException {
        this.replicator = new RedisReplicator(uri, sslConfiguration);
    }

    @SuppressWarnings("unchecked")
    public <T extends Replicator> T getReplicator() {
        return (T) this.replicator;
    }

    @Override
    public boolean addRawByteListener(RawByteListener listener) {
        return replicator.addRawByteListener(listener);
    }

    @Override
    public boolean removeRawByteListener(RawByteListener listener) {
        return replicator.removeRawByteListener(listener);
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

    @Override
    public void setRdbVisitor(RdbVisitor rdbVisitor) {
        replicator.setRdbVisitor(rdbVisitor);
    }

    @Override
    public RdbVisitor getRdbVisitor() {
        return replicator.getRdbVisitor();
    }

    @Override
    public boolean addEventListener(EventListener listener) {
        return replicator.addEventListener(listener);
    }

    @Override
    public boolean removeEventListener(EventListener listener) {
        return replicator.removeEventListener(listener);
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
    public boolean addStatusListener(StatusListener listener) {
        return replicator.addStatusListener(listener);
    }

    @Override
    public boolean removeStatusListener(StatusListener listener) {
        return replicator.removeStatusListener(listener);
    }

    @Override
    public boolean verbose() {
        return replicator.verbose();
    }

    @Override
    public Status getStatus() {
        return replicator.getStatus();
    }

    @Override
    public Configuration getConfiguration() {
        return replicator.getConfiguration();
    }

    @Override
    public CompletableFuture<Void> open(Executor executor) {
        Runnable runnable = () -> Replicators.open(replicator);
        if (executor == null) {
            return CompletableFuture.runAsync(runnable);
        } else {
            return CompletableFuture.runAsync(runnable, executor);
        }
    }

    @Override
    public CompletableFuture<Void> close(Executor executor) {
        Runnable runnable = () -> Replicators.close(replicator);
        if (executor == null) {
            return CompletableFuture.runAsync(runnable);
        } else {
            return CompletableFuture.runAsync(runnable, executor);
        }
    }
}
