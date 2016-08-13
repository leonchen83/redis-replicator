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
import com.moilioncircle.redis.replicator.io.RedisOutputStream;
import com.moilioncircle.redis.replicator.rdb.RdbParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.moilioncircle.redis.replicator.Constants.DOLLAR;
import static com.moilioncircle.redis.replicator.Constants.STAR;

/**
 * Created by leon on 8/9/16.
 */
public class RedisSocketReplicator extends AbstractReplicator {

    private static final Log logger = LogFactory.getLog(RedisSocketReplicator.class);

    private final String host;
    private final int port;
    private RedisOutputStream outputStream;
    private Socket socket;
    private ReplyParser replyParser;

    private final AtomicBoolean connected = new AtomicBoolean(false);

    public RedisSocketReplicator(String host, int port, String password) throws IOException {
        this.host = host;
        this.port = port;
        if (password != null) {
            auth(password);
        }
        buildInCommandParserRegister();
    }

    public RedisSocketReplicator(String host, int port) throws IOException {
        this(host, port, null);
    }

    private void connect() {
        if (!connected.get()) {
            try {
                socket = new Socket();
                socket.setReuseAddress(true);
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);
                socket.setSoLinger(true, 0);
                socket.connect(new InetSocketAddress(host, port), 10000);
                outputStream = new RedisOutputStream(socket.getOutputStream());
                inputStream = new RedisInputStream(socket.getInputStream());
                replyParser = new ReplyParser(inputStream);
                connected.compareAndSet(false, true);
            } catch (IOException ex) {
                try {
                    close();
                } catch (IOException e) {
                    logger.error("Error", e);
                }
            }
        }
    }

    public void open() throws IOException {
        send("SYNC".getBytes());
        final Replicator replicator = this;
        //sync dump
        String reply = (String) replyParser.parse(new BulkReplyHandler() {
            @Override
            public String handle(long len, RedisInputStream in) throws IOException {
                RdbParser parser = new RdbParser(in, replicator);
                parser.parse();
                return "OK";
            }
        });
        //sync command
        if (!reply.equals("OK")) throw new AssertionError("SYNC failed." + reply);
        while (connected.get()) {
            Object obj = replyParser.parse();
            //command
            if (obj instanceof Object[]) {
                if (logger.isDebugEnabled()) logger.debug(Arrays.deepToString((Object[]) obj));

                Object[] command = (Object[]) obj;
                CommandName cmdName = CommandName.name((String) command[0]);
                Object[] params = new Object[command.length - 1];
                System.arraycopy(command, 1, params, 0, params.length);

                //no register .ignore
                if (commands.get(cmdName) == null) continue;

                //do command replyParser
                CommandParser<? extends Command> operations = commands.get(cmdName);
                Command parsedCommand = operations.parse(cmdName, params);

                //do command filter
                if (!doCommandFilter(parsedCommand)) continue;

                //do command handler
                doCommandHandler(parsedCommand);
            } else {
                throw new AssertionError("Reply not a command:" + obj);
            }
        }
    }

    public void auth(String password) throws IOException {
        if (password != null) {
            send("AUTH".getBytes(), password.getBytes());
            String reply = (String) replyParser.parse();
            if (reply.equals("OK")) return;
            throw new AssertionError("AUTH failed." + reply);
        }
    }

    public void send(byte[] command) throws IOException {
        send(command, new byte[0][]);
    }

    public void send(byte[] command, final byte[]... args) throws IOException {
        connect();
        outputStream.write(STAR);
        outputStream.write(String.valueOf(args.length + 1).getBytes());
        outputStream.writeCrLf();
        outputStream.write(DOLLAR);
        outputStream.write(String.valueOf(command.length).getBytes());
        outputStream.writeCrLf();
        outputStream.write(command);
        outputStream.writeCrLf();
        for (final byte[] arg : args) {
            outputStream.write(DOLLAR);
            outputStream.write(String.valueOf(arg.length).getBytes());
            outputStream.writeCrLf();
            outputStream.write(arg);
            outputStream.writeCrLf();
        }
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        if (!connected.compareAndSet(true, false)) return;
        inputStream.close();
        outputStream.close();
        socket.close();
        logger.info("channel closed");
    }
}
