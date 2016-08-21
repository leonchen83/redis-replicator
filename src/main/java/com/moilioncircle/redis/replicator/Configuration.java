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

/**
 * @author leon.chen
 * @since 2016/8/15
 */
public class Configuration {

    /**
     * factory
     *
     * @return Configuration
     */
    public static Configuration defaultSetting() {
        return new Configuration();
    }

    /**
     * socket connection timeout
     */
    private int connectionTimeout = 30000;

    /**
     * socket input stream read timeout
     */
    private int readTimeout = 30000;

    /**
     * socket receive buffer size
     */
    private int receiveBufferSize = 0;

    /**
     * socket send buffer size
     */
    private int sendBufferSize = 0;

    /**
     * if readTimeout <=0 this property inefficiency.
     * socket input stream read retries
     */
    private int retries = 5;

    /**
     * redis input stream buffer size
     */
    private int bufferSize = 8192;

    /**
     * auth password
     */
    private String authPassword = null;

    /**
     * discard rdb parser
     */
    private boolean discardRdbParser = false;

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public Configuration setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public Configuration setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public int getRetries() {
        return retries;
    }

    public Configuration setRetries(int retries) {
        this.retries = retries;
        return this;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public Configuration setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
        return this;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public Configuration setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
        return this;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public Configuration setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
        return this;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public Configuration setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    public boolean isDiscardRdbParser() {
        return discardRdbParser;
    }

    public Configuration setDiscardRdbParser(boolean discardRdbParser) {
        this.discardRdbParser = discardRdbParser;
        return this;
    }
}
