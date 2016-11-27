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

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
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
    private Timer heartBeat;

    private final AtomicBoolean connected = new AtomicBoolean(false);

    public RedisSocketReplicator(String host, int port, Configuration configuration) {
        this.host = host;
        this.port = port;
        this.configuration = configuration;
        this.eventQueue = new ArrayBlockingQueue<>(configuration.getEventQueueSize());
        builtInCommandParserRegister();
    }

    /**
     * PSYNC
     *
     * @throws IOException when read timeout or connect timeout
     */
    @Override
    public void open() throws IOException {
        worker.start();
        for (int i = 0; i < configuration.getRetries() || configuration.getRetries() <= 0; i++) {
            try {
                connect();

                if (configuration.getAuthPassword() != null) auth(configuration.getAuthPassword());

                sendSlavePort();

                sendSlaveIp();

                sendSlaveCapa();

                //reset retries
                i = 0;

                logger.info("PSYNC " + configuration.getMasterRunId() + " " + String.valueOf(configuration.getOffset()));
                send("PSYNC".getBytes(), configuration.getMasterRunId().getBytes(), String.valueOf(configuration.getOffset()).getBytes());
                final String reply = (String) reply();

                SyncMode syncMode = trySync(reply);
                //bug fix.
                if (syncMode == SyncMode.PSYNC && connected.get()) {
                    //heart beat send REPLCONF ACK ${slave offset}

                    heartBeat = new Timer("heart beat");
                    heartBeat.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                send("REPLCONF".getBytes(), "ACK".getBytes(), String.valueOf(configuration.getOffset()).getBytes());
                            } catch (IOException e) {
                                //NOP
                            }
                        }
                    }, configuration.getHeartBeatPeriod(), configuration.getHeartBeatPeriod());
                    logger.info("heart beat started.");
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
                        if (logger.isInfoEnabled()) logger.info("Redis reply:" + obj);
                    }
                }
                //connected = false
                break;
            } catch (SocketException | SocketTimeoutException | InterruptedException | EOFException e) {
                //close socket manual
                if (!connected.get()) {
                    break;
                }
                logger.error("socket error", e);
                //connect refused
                //connect timeout
                //read timeout
                //connect abort
                //server disconnect connection EOFException
                close();
                //retry psync in next loop.
                logger.info("reconnect to redis-server. retry times:" + (i + 1));
                try {
                    Thread.sleep(configuration.getRetryTimeInterval());
                } catch (InterruptedException e1) {
                    //non interrupted
                    logger.error("error", e1);
                }
            }
        }
        //
        if (worker != null && !worker.isClosed()) worker.close();
    }

    private SyncMode trySync(final String reply) throws IOException {
        logger.info(reply);
        if (reply.startsWith("FULLRESYNC")) {
            //sync rdb dump file
            parseDump(this);
            //after parsed dump file,cache master run id and offset so that next psync.
            String[] ary = reply.split(" ");
            configuration.setMasterRunId(ary[1]);
            configuration.setOffset(Long.parseLong(ary[2]));
            return SyncMode.PSYNC;
        } else if (reply.equals("CONTINUE")) {
            // do nothing
            return SyncMode.PSYNC;
        } else {
            //server don't support psync
            logger.info("SYNC");
            send("SYNC".getBytes());
            parseDump(this);
            return SyncMode.SYNC;
        }
    }

    private void parseDump(final AbstractReplicator replicator) throws IOException {
        //sync dump
        String reply = (String) replyParser.parse(new BulkReplyHandler() {
            @Override
            public String handle(long len, RedisInputStream in) throws IOException {
                logger.info("RDB dump file size:" + len);
                if (configuration.isDiscardRdbEvent()) {
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
        if (reply.equals("OK")) return;
        throw new AssertionError("SYNC failed." + reply);
    }

    private void auth(String password) throws IOException {
        if (password != null) {
            logger.info("AUTH " + password);
            send("AUTH".getBytes(), password.getBytes());
            final String reply = (String) reply();
            logger.info(reply);
            if (reply.equals("OK")) return;
            throw new AssertionError("[AUTH " + password + "] failed." + reply);
        }
    }

    private void sendSlavePort() throws IOException {
        //REPLCONF listening-prot ${port}
        logger.info("REPLCONF listening-port " + socket.getLocalPort());
        send("REPLCONF".getBytes(), "listening-port".getBytes(), String.valueOf(socket.getLocalPort()).getBytes());
        final String reply = (String) reply();
        logger.info(reply);
        if (reply.equals("OK")) return;
        logger.warn("[REPLCONF listening-port " + socket.getLocalPort() + "] failed." + reply);
    }

    private void sendSlaveIp() throws IOException {
        //REPLCONF ip-address ${address}
        logger.info("REPLCONF ip-address " + socket.getLocalAddress().getHostAddress());
        send("REPLCONF".getBytes(), "ip-address".getBytes(), socket.getLocalAddress().getHostAddress().getBytes());
        final String reply = (String) reply();
        logger.info(reply);
        if (reply.equals("OK")) return;
        //redis 3.2+
        logger.warn("[REPLCONF ip-address " + socket.getLocalAddress().getHostAddress() + "] failed." + reply);
    }

    private void sendSlaveCapa() throws IOException {
        //REPLCONF capa eof
        logger.info("REPLCONF capa eof");
        send("REPLCONF".getBytes(), "capa".getBytes(), "eof".getBytes());
        final String reply = (String) reply();
        logger.info(reply);
        if (reply.equals("OK")) return;
        logger.warn("[REPLCONF capa eof] failed." + reply);
    }

    public void send(byte[] command) throws IOException {
        send(command, new byte[0][]);
    }

    public void send(byte[] command, final byte[]... args) throws IOException {
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
        if (configuration.isSsl()) {
            SSLSocketFactory sslSocketFactory = configuration.getSslSocketFactory();
            socket = sslSocketFactory.createSocket(socket, host, port, true);

            if (configuration.getSslParameters() != null) {
                ((SSLSocket) socket).setSSLParameters(configuration.getSslParameters());
            }

            if (configuration.getHostnameVerifier() != null && !configuration.getHostnameVerifier().verify(host, ((SSLSocket) socket).getSession())) {
                throw new SocketException("the connection to " + host + " failed ssl/tls hostname verification.");
            }
        }
        outputStream = new RedisOutputStream(socket.getOutputStream());
        inputStream = new RedisInputStream(socket.getInputStream(), configuration.getBufferSize());
        replyParser = new ReplyParser(inputStream);
    }

    @Override
    public void close() {
        if (!connected.compareAndSet(true, false)) return;
        if (heartBeat != null) {
            heartBeat.cancel();
            heartBeat = null;
            logger.info("heart beat canceled.");
        }
        try {
            if (inputStream != null) inputStream.close();
        } catch (IOException e) {
            //NOP
        }
        try {
            if (outputStream != null) outputStream.close();
        } catch (IOException e) {
            //NOP
        }
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            //NOP
        }
        logger.info("channel closed");
    }

    private enum SyncMode {SYNC, PSYNC}
}
