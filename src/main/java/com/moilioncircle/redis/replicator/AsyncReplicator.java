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

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.rdb.RdbVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Leon Chen
 * @since 3.0.0
 */
public interface AsyncReplicator extends ReplicatorListener {
    /*
     * Command
     */
    void builtInCommandParserRegister();

    CommandParser<? extends Command> getCommandParser(CommandName command);

    <T extends Command> void addCommandParser(CommandName command, CommandParser<T> parser);

    CommandParser<? extends Command> removeCommandParser(CommandName command);

    /*
     * Module
     */
    ModuleParser<? extends Module> getModuleParser(String moduleName, int moduleVersion);

    <T extends Module> void addModuleParser(String moduleName, int moduleVersion, ModuleParser<T> parser);

    ModuleParser<? extends Module> removeModuleParser(String moduleName, int moduleVersion);

    /*
     * Rdb
     */
    void setRdbVisitor(RdbVisitor rdbVisitor);

    RdbVisitor getRdbVisitor();

    boolean verbose();

    Status getStatus();

    Configuration getConfiguration();

    CompletableFuture<Void> open(Executor executor);

    CompletableFuture<Void> close(Executor executor);
}