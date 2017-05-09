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
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.RdbParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Arrays;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class RedisMixReplicator extends AbstractReplicator {
    protected static final Log logger = LogFactory.getLog(RedisAofReplicator.class);
    protected final ReplyParser replyParser;

    public RedisMixReplicator(File file, Configuration configuration) throws FileNotFoundException {
        this(new FileInputStream(file), configuration);
    }

    public RedisMixReplicator(InputStream in, Configuration configuration) {
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
        RdbParser parser = new RdbParser(inputStream, this);
        parser.parse();
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
}
