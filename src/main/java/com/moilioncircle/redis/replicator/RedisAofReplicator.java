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
import com.moilioncircle.redis.replicator.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static com.moilioncircle.redis.replicator.Status.CONNECTED;
import static com.moilioncircle.redis.replicator.Status.DISCONNECTED;
import static java.nio.charset.StandardCharsets.UTF_8;

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
        Objects.requireNonNull(in);
        Objects.requireNonNull(configuration);
        this.configuration = configuration;
        this.inputStream = new RedisInputStream(in, this.configuration.getBufferSize());
        this.inputStream.setRawByteListeners(this.rawByteListeners);
        this.replyParser = new ReplyParser(inputStream);
        builtInCommandParserRegister();
        if (configuration.isUseDefaultExceptionListener())
            addExceptionListener(new DefaultExceptionListener());
    }

    @Override
    public void open() throws IOException {
        if (!this.connected.compareAndSet(DISCONNECTED, CONNECTED)) return;
        try {
            doOpen();
        } catch (EOFException ignore) {
        } catch (UncheckedIOException e) {
            if (!(e.getCause() instanceof EOFException)) throw e.getCause();
        } finally {
            close();
        }
    }

    protected void doOpen() throws IOException {
        while (getStatus() == CONNECTED) {
            Object obj = replyParser.parse();

            if (obj instanceof Object[]) {
                if (configuration.isVerbose() && logger.isDebugEnabled())
                    logger.debug(Arrays.deepToString((Object[]) obj));
                Object[] command = (Object[]) obj;
                CommandName cmdName = CommandName.name(new String((byte[]) command[0], UTF_8));
                final CommandParser<? extends Command> operations;
                if ((operations = commands.get(cmdName)) == null) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("command [" + cmdName + "] not register. raw command:[" + Arrays.deepToString(command) + "]");
                    }
                    continue;
                }
                Command parsedCommand = operations.parse(command);
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