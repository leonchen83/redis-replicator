/*
 * Copyright 2016-2017 Leon Chen
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

import static com.moilioncircle.redis.replicator.Status.CONNECTED;
import static com.moilioncircle.redis.replicator.Status.DISCONNECTED;
import static com.moilioncircle.redis.replicator.Status.DISCONNECTING;

import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.io.XPipedInputStream;
import com.moilioncircle.redis.replicator.io.XPipedOutputStream;
import com.moilioncircle.redis.replicator.rdb.RdbParser;
import com.moilioncircle.redis.replicator.rdb.ScanRdbGenerator;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
public class RedisScanReplicator extends AbstractReplicator implements Runnable {
    
    protected static final Logger logger = LoggerFactory.getLogger(RedisScanReplicator.class);
    
    protected final int port;
    protected final String host;
    protected volatile IOException exception;
    protected XPipedOutputStream outputStream;
    
    protected final Thread worker;
    protected final ThreadFactory threadFactory = Executors.defaultThreadFactory();
    
    public RedisScanReplicator(String host, int port, Configuration configuration) {
        Objects.requireNonNull(host);
        if (port <= 0 || port > 65535) throw new IllegalArgumentException("illegal argument port: " + port);
        Objects.requireNonNull(configuration);
        this.host = host;
        this.port = port;
        this.configuration = configuration;
        
        if (configuration.isUseDefaultExceptionListener())
            addExceptionListener(new DefaultExceptionListener());
        
        this.outputStream = new XPipedOutputStream();
        this.inputStream = new RedisInputStream(new XPipedInputStream(outputStream), this.configuration.getBufferSize());
        this.inputStream.setRawByteListeners(this.rawByteListeners);
        
        this.worker = this.threadFactory.newThread(this);
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    @Override
    public void run() {
        try {
            ScanRdbGenerator generator = new ScanRdbGenerator(host, port, configuration, outputStream);
            generator.generate();
        } catch (EOFException ignore) {
        } catch (IOException e) {
            this.exception = e;
        }
    }
    
    @Override
    protected void doOpen() throws IOException {
        this.worker.start();
        try {
            new RdbParser(inputStream, this).parse();
        } catch (EOFException ignore) {
        }
        if (this.exception != null) throw this.exception;
    }
    
    @Override
    protected void doClose() throws IOException {
        compareAndSet(CONNECTED, DISCONNECTING);
        try {
            if (inputStream != null) {
                this.inputStream.setRawByteListeners(null);
                inputStream.close();
            }
        } catch (IOException ignore) {
            /*NOP*/
        } finally {
            setStatus(DISCONNECTED);
        }
    }
}
