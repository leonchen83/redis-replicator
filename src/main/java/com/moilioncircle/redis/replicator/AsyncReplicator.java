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