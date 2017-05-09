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

import java.io.*;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.ReplyParser;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.AuxFieldListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.RdbVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class RedisAofReplicator extends AbstractReplicator {

    protected static final Log logger = LogFactory.getLog(RedisAofReplicator.class);
    protected final ReplyParser replyParser;

    public RedisAofReplicator(File file, Configuration configuration) throws FileNotFoundException {
        this(new FileInputStream(file), configuration);
    }

    public RedisAofReplicator(InputStream in, Configuration configuration) {
        this.configuration = configuration;
        this.inputStream = new RedisInputStream(in, this.configuration.getBufferSize());
        this.inputStream.addRawByteListener(this);
        this.replyParser = new ReplyParser(inputStream);
        builtInCommandParserRegister();
        addExceptionListener(new DefaultExceptionListener());
    }

    @Override
    public void open() throws IOException {
        try {
            doOpen();
        } catch (EOFException ignore) {
        } finally {
            close();
        }
    }

    protected void doOpen() throws IOException {
        while (true) {
            // got EOFException to break the loop
            Object obj = replyParser.parse();

            if (obj instanceof Object[]) {
                if (configuration.isVerbose() && logger.isDebugEnabled()) {
                    logger.debug(Arrays.deepToString((Object[]) obj));
                }
                Object[] command = (Object[]) obj;
                CommandName cmdName = CommandName.name((String) command[0]);
                final CommandParser<? extends Command> operations;
                //if command do not register. ignore
                if ((operations = commands.get(cmdName)) == null) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("command [" + cmdName + "] not register. raw command:[" + Arrays.deepToString((Object[]) obj) + "]");
                    }
                    continue;
                }
                //do command replyParser
                Command parsedCommand = operations.parse(command);
                //submit event
                this.submitEvent(parsedCommand);
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("redis reply:" + obj);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        doClose();
    }

    @Override
    public ModuleParser<? extends Module> getModuleParser(String moduleName, int moduleVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Module> void addModuleParser(String moduleName, int moduleVersion, ModuleParser<T> parser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModuleParser<? extends Module> removeModuleParser(String moduleName, int moduleVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRdbVisitor(RdbVisitor rdbVisitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RdbVisitor getRdbVisitor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addRdbListener(RdbListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeRdbListener(RdbListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAuxFieldListener(AuxFieldListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAuxFieldListener(AuxFieldListener listener) {
        throw new UnsupportedOperationException();
    }
}