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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
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
    private final Configuration configuration;
    private RedisOutputStream outputStream;
    private Socket socket;
    private ReplyParser replyParser;
    private Timer heartBeat;

    private final AtomicBoolean connected = new AtomicBoolean(false);

    public RedisSocketReplicator(String host, int port, Configuration configuration) throws IOException {
        this.host = host;
        this.port = port;
        this.configuration = configuration;
        buildInCommandParserRegister();
    }

    /**
     * PSYNC
     *
     * @throws IOException
     */
    @Override
    public void open() throws IOException {
        for (int i = 0; i < configuration.getRetries(); i++) {
            try {

                if (configuration.getAuthPassword() != null) auth(configuration.getAuthPassword());

                sendSlavePort();

                sendSlaveIp();

                sendSlaveCapa();

                logger.info("PSYNC " + configuration.getMasterRunId() + " " + String.valueOf(configuration.getOffset()));
                send("PSYNC".getBytes(), configuration.getMasterRunId().getBytes(), String.valueOf(configuration.getOffset()).getBytes());
                final String reply = (String) reply();

                Sync syncMode = trySync(reply);
                if (syncMode == Sync.PSYNC) {
                    //heart beat send REPLCONF ACK ${slave offset}
                    heartBeat = new Timer("heart beat");
                    heartBeat.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                send("REPLCONF".getBytes(), "ACK".getBytes(), String.valueOf(configuration.getOffset()).getBytes());
                            } catch (IOException e) {
                                //NOP
                                logger.error(e);
                            }

                        }
                    }, 1000, 1000);
                }
                //sync command
                while (connected.get()) {
                    Object obj = replyParser.parse(new OffsetHandler() {
                        @Override
                        public void handle(long len) {
                            configuration.addOffset(len);
                        }
                    });
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
                        if (logger.isInfoEnabled()) logger.info("Redis reply:" + obj);
                    }
                }
                break;
            } catch (SocketException | SocketTimeoutException e) {
                //connect timeout
                //read timeout
                //connect abort
                close();
                //retry psync in next loop.
                logger.info("retry connect to redis.");
            }
        }
    }

    private Sync trySync(final String reply) throws IOException {
        logger.info(reply);
        if (reply.startsWith("FULLRESYNC")) {
            //sync dump
            parseDump(this);
            //after parsed dump file,cache master run id and offset so that next psync.
            String[] ary = reply.split(" ");
            configuration.setMasterRunId(ary[1]);
            configuration.setOffset(Long.parseLong(ary[2]));
            return Sync.PSYNC;
        } else if (reply.equals("CONTINUE")) {
            // do nothing
            return Sync.PSYNC;
        } else {
            //server don't support psync
            logger.info("SYNC");
            send("SYNC".getBytes());
            parseDump(this);
            return Sync.SYNC;
        }
    }

    private void parseDump(final Replicator replicator) throws IOException {
        //sync dump
        String reply = (String) replyParser.parse(new BulkReplyHandler() {
            @Override
            public String handle(long len, RedisInputStream in) throws IOException {
                if (logger.isDebugEnabled()) logger.debug("RDB dump file size:" + len);
                if (configuration.isDiscardRdbParser()) {
                    logger.info("Discard " + len + " bytes");
                    in.skip(len);
                } else {
                    RdbParser parser = new RdbParser(in, replicator);
                    parser.parse();
                }
                return "OK";
            }
        });
        //sync command
        if (!reply.equals("OK")) throw new AssertionError("SYNC failed." + reply);
    }

    private void auth(String password) throws IOException {
        if (password != null) {
            logger.info("AUTH " + password);
            send("AUTH".getBytes(), password.getBytes());
            String reply = (String) replyParser.parse();
            logger.info(reply);
            if (reply.equals("OK")) return;
            throw new AssertionError("AUTH failed." + reply);
        }
    }

    private void sendSlavePort() throws IOException {
        //REPLCONF listening-prot 6380
        logger.info("REPLCONF listening-port " + socket.getLocalPort());
        send("REPLCONF".getBytes(), "listening-port".getBytes(), String.valueOf(socket.getLocalPort()).getBytes());
        final String reply = (String) reply();
        logger.info(reply);
        if (reply.equals("OK")) return;
        throw new AssertionError("REPLCONF listening-port " + socket.getLocalPort() + " failed." + reply);
    }

    private void sendSlaveIp() throws IOException {
        //REPLCONF capa eof
        logger.info("REPLCONF ip-address " + socket.getLocalAddress().getHostAddress());
        send("REPLCONF".getBytes(), "ip-address".getBytes(), socket.getLocalAddress().getHostAddress().getBytes());
        final String reply = (String) reply();
        logger.info(reply);
        if (reply.equals("OK")) return;
        throw new AssertionError("REPLCONF ip-address " + socket.getLocalAddress().getHostAddress() + " failed." + reply);
    }

    private void sendSlaveCapa() throws IOException {
        //REPLCONF capa eof
        logger.info("REPLCONF capa eof");
        send("REPLCONF".getBytes(), "capa".getBytes(), "eof".getBytes());
        final String reply = (String) reply();
        logger.info(reply);
        if (reply.equals("OK")) return;
        throw new AssertionError("REPLCONF capa eof failed." + reply);
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

    public Object reply() throws IOException {
        return replyParser.parse();
    }

    public Object reply(BulkReplyHandler handler) throws IOException {
        return replyParser.parse(handler);
    }

    private void connect() throws IOException {
        if (!connected.compareAndSet(false, true)) return;

        socket = new Socket();
        socket.setReuseAddress(true);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        socket.setSoLinger(true, 0);
        if (configuration.getReadTimeout() > 0) {
            socket.setSoTimeout(configuration.getReadTimeout());
        }
        if (configuration.getReceiveBufferSize() > 0) {
            socket.setReceiveBufferSize(configuration.getReceiveBufferSize());
        }
        if (configuration.getSendBufferSize() > 0) {
            socket.setSendBufferSize(configuration.getSendBufferSize());
        }
        socket.connect(new InetSocketAddress(host, port), configuration.getConnectionTimeout());
        outputStream = new RedisOutputStream(socket.getOutputStream());
        inputStream = new RedisInputStream(socket.getInputStream(), configuration.getBufferSize(), configuration.getRetries());
        replyParser = new ReplyParser(inputStream);
    }

    @Override
    public void close() throws IOException {
        if (!connected.compareAndSet(true, false)) return;
        if (inputStream != null) inputStream.close();
        if (outputStream != null) outputStream.close();
        if (socket != null && !socket.isClosed()) socket.close();
        if (heartBeat != null) {
            heartBeat.cancel();
            heartBeat = null;
        }
        if (logger.isInfoEnabled()) logger.info("channel closed");
    }

    private enum Sync {
        SYNC, PSYNC
    }
}
