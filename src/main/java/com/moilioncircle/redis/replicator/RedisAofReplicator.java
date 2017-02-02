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

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.ReplyParser;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.AuxFieldListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.RdbVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Arrays;

/**
 * Created by leon on 11/21/16.
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
            Object obj = replyParser.parse();

            if (obj instanceof Object[]) {
                if (configuration.isVerbose() && logger.isDebugEnabled())
                    logger.debug(Arrays.deepToString((Object[]) obj));

                Object[] command = (Object[]) obj;
                CommandName cmdName = CommandName.name((String) command[0]);
                Object[] params = new Object[command.length - 1];
                System.arraycopy(command, 1, params, 0, params.length);

                final CommandParser<? extends Command> operations;
                //if command do not register. ignore
                if ((operations = commands.get(cmdName)) == null) continue;

                //do command replyParser
                Command parsedCommand = operations.parse(cmdName, params);

                //submit event
                this.submitEvent(parsedCommand);
            } else {
                logger.info("Redis reply:" + obj);
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

    @Override
    public boolean addRdbRawByteListener(RawByteListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeRdbRawByteListener(RawByteListener listener) {
        throw new UnsupportedOperationException();
    }
}