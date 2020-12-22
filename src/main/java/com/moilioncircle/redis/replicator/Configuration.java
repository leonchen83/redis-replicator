/*
 * Copyright 2016-2018 Leon Chen
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

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import com.moilioncircle.redis.replicator.net.SslContextFactory;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class Configuration {

    private Configuration() {
    }

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
     * same as redis.conf repl-timeout
     */
    private int connectionTimeout = 60000;

    /**
     * socket input stream read timeout
     * same as redis.conf repl-timeout
     */
    private int readTimeout = 60000;

    /**
     * socket receive buffer size
     */
    private int receiveBufferSize = 0;

    /**
     * socket send buffer size
     */
    private int sendBufferSize = 0;

    /**
     * connection retry times. if retries <= 0 then always retry
     */
    private int retries = 5;

    /**
     * retry time interval
     */
    private int retryTimeInterval = 1000;

    /**
     * redis input stream buffer size
     */
    private int bufferSize = 8 * 1024;
    
    /**
     * auth user (redis 6.0)
     * 
     * @since 3.4.0
     */
    private String authUser = null;

    /**
     * auth password
     */
    private String authPassword = null;

    /**
     * discard rdb event
     */
    private boolean discardRdbEvent = false;

    /**
     * async buffer size
     */
    private int asyncCachedBytes = 512 * 1024;

    /**
     * rate limit (unit : bytes/second)
     *
     * @since 2.3.2
     */
    private int rateLimit = 0;

    /**
     * trace event log
     */
    private boolean verbose = false;

    /**
     * used in psync heartbeat
     */
    private int heartbeatPeriod = 1000;

    /**
     * use default exception handler
     *
     * @since 2.2.0
     */
    private boolean useDefaultExceptionListener = true;

    /**
     * open ssl connection
     */
    private boolean ssl = false;

    /**
     * ssl socket factory
     */
    private SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    
    /**
     * ssl context factory
     * 
     * @since 3.4.0
     */
    private SslContextFactory sslContextFactory;

    /**
     * ssl parameters
     */
    private SSLParameters sslParameters;

    /**
     * hostname verifier
     */
    private HostnameVerifier hostnameVerifier;

    /**
     * psync master repl_id
     */
    private String replId = "?";

    /**
     * psync2 repl_stream_db
     */
    private int replStreamDB = -1;

    /**
     * psync offset
     */
    private final AtomicLong replOffset = new AtomicLong(-1);
    
    /**
     * heartbeat executor
     */
    private ScheduledExecutorService executor;
    
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

    public String getAuthUser() {
        return authUser;
    }

    public Configuration setAuthUser(String authUser) {
        this.authUser = authUser;
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

    public boolean isDiscardRdbEvent() {
        return discardRdbEvent;
    }

    public Configuration setDiscardRdbEvent(boolean discardRdbEvent) {
        this.discardRdbEvent = discardRdbEvent;
        return this;
    }

    public String getReplId() {
        return replId;
    }

    public Configuration setReplId(String replId) {
        this.replId = replId;
        return this;
    }

    public int getReplStreamDB() {
        return replStreamDB;
    }

    public Configuration setReplStreamDB(int replStreamDB) {
        this.replStreamDB = replStreamDB;
        return this;
    }

    public long getReplOffset() {
        return replOffset.get();
    }

    public Configuration setReplOffset(long replOffset) {
        this.replOffset.set(replOffset);
        return this;
    }

    public Configuration addOffset(long offset) {
        this.replOffset.addAndGet(offset);
        return this;
    }

    public int getAsyncCachedBytes() {
        return asyncCachedBytes;
    }

    public Configuration setAsyncCachedBytes(int asyncCachedBytes) {
        this.asyncCachedBytes = asyncCachedBytes;
        return this;
    }

    public int getRateLimit() {
        return rateLimit;
    }

    public Configuration setRateLimit(int rateLimit) {
        this.rateLimit = rateLimit;
        return this;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public Configuration setVerbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    public int getHeartbeatPeriod() {
        return heartbeatPeriod;
    }

    public Configuration setHeartbeatPeriod(int heartbeatPeriod) {
        this.heartbeatPeriod = heartbeatPeriod;
        return this;
    }

    public boolean isUseDefaultExceptionListener() {
        return useDefaultExceptionListener;
    }

    public Configuration setUseDefaultExceptionListener(boolean useDefaultExceptionListener) {
        this.useDefaultExceptionListener = useDefaultExceptionListener;
        return this;
    }

    public int getRetryTimeInterval() {
        return retryTimeInterval;
    }

    public Configuration setRetryTimeInterval(int retryTimeInterval) {
        this.retryTimeInterval = retryTimeInterval;
        return this;
    }

    public boolean isSsl() {
        return ssl;
    }

    public Configuration setSsl(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public Configuration setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public SslContextFactory getSslContextFactory() {
        return sslContextFactory;
    }

    public Configuration setSslContextFactory(SslContextFactory sslContextFactory) {
        this.sslContextFactory = sslContextFactory;
        return this;
    }

    public SSLParameters getSslParameters() {
        return sslParameters;
    }

    public Configuration setSslParameters(SSLParameters sslParameters) {
        this.sslParameters = sslParameters;
        return this;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public Configuration setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }
    
    public ScheduledExecutorService getExecutor() {
        return executor;
    }
    
    public Configuration setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
        return this;
    }
    
    public Configuration merge(SslConfiguration sslConfiguration) {
        if (sslConfiguration == null) return this;
        this.setSslParameters(sslConfiguration.getSslParameters());
        this.setSslSocketFactory(sslConfiguration.getSslSocketFactory());
        this.setHostnameVerifier(sslConfiguration.getHostnameVerifier());
        this.setSslContextFactory(sslConfiguration.getSslContextFactory());
        return this;
    }

    public static Configuration valueOf(RedisURI uri) {
        Configuration configuration = defaultSetting();
        Map<String, String> parameters = uri.parameters;
        if (parameters.containsKey("connectionTimeout")) {
            configuration.setConnectionTimeout(getInt(parameters.get("connectionTimeout"), 60000));
        }
        if (parameters.containsKey("readTimeout")) {
            configuration.setReadTimeout(getInt(parameters.get("readTimeout"), 60000));
        }
        if (parameters.containsKey("receiveBufferSize")) {
            configuration.setReceiveBufferSize(getInt(parameters.get("receiveBufferSize"), 0));
        }
        if (parameters.containsKey("sendBufferSize")) {
            configuration.setSendBufferSize(getInt(parameters.get("sendBufferSize"), 0));
        }
        if (parameters.containsKey("retries")) {
            configuration.setRetries(getInt(parameters.get("retries"), 5));
        }
        if (parameters.containsKey("retryTimeInterval")) {
            configuration.setRetryTimeInterval(getInt(parameters.get("retryTimeInterval"), 1000));
        }
        if (parameters.containsKey("bufferSize")) {
            configuration.setBufferSize(getInt(parameters.get("bufferSize"), 8 * 1024));
        }
        if (parameters.containsKey("authUser")) {
            configuration.setAuthUser(parameters.get("authUser"));
        }
        if (parameters.containsKey("authPassword")) {
            configuration.setAuthPassword(parameters.get("authPassword"));
        }
        if (parameters.containsKey("discardRdbEvent")) {
            configuration.setDiscardRdbEvent(getBool(parameters.get("discardRdbEvent"), false));
        }
        if (parameters.containsKey("asyncCachedBytes")) {
            configuration.setAsyncCachedBytes(getInt(parameters.get("asyncCachedBytes"), 512 * 1024));
        }
        if (parameters.containsKey("rateLimit")) {
            configuration.setRateLimit(getInt(parameters.get("rateLimit"), 0));
        }
        if (parameters.containsKey("verbose")) {
            configuration.setVerbose(getBool(parameters.get("verbose"), false));
        }
        if (parameters.containsKey("heartbeatPeriod")) {
            configuration.setHeartbeatPeriod(getInt(parameters.get("heartbeatPeriod"), 1000));
        }
        if (parameters.containsKey("useDefaultExceptionListener")) {
            configuration.setUseDefaultExceptionListener(getBool(parameters.get("useDefaultExceptionListener"), false));
        }
        if (parameters.containsKey("ssl")) {
            configuration.setSsl(getBool(parameters.get("ssl"), false));
        }
        if (parameters.containsKey("replId")) {
            configuration.setReplId(parameters.get("replId"));
        }
        if (parameters.containsKey("replStreamDB")) {
            configuration.setReplStreamDB(getInt(parameters.get("replStreamDB"), -1));
        }
        if (parameters.containsKey("replOffset")) {
            configuration.setReplOffset(getLong(parameters.get("replOffset"), -1L));
        }
        // redis 6
        if (uri.isSsl()) {
            configuration.setSsl(true);
        }
        if (uri.getUser() != null) {
            configuration.setAuthUser(uri.getUser());
        }
        if (uri.getPassword() != null) {
            configuration.setAuthPassword(uri.getPassword());
        }
        return configuration;
    }

    private static boolean getBool(String value, boolean defaultValue) {
        if (value == null)
            return defaultValue;
        if (value.equals("false") || value.equals("no"))
            return false;
        if (value.equals("true") || value.equals("yes"))
            return true;
        return defaultValue;
    }

    private static int getInt(String value, int defaultValue) {
        if (value == null)
            return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static long getLong(String value, long defaultValue) {
        if (value == null)
            return defaultValue;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "connectionTimeout=" + connectionTimeout +
                ", readTimeout=" + readTimeout +
                ", receiveBufferSize=" + receiveBufferSize +
                ", sendBufferSize=" + sendBufferSize +
                ", retries=" + retries +
                ", retryTimeInterval=" + retryTimeInterval +
                ", bufferSize=" + bufferSize +
                ", authUser='" + authUser + '\'' +
                ", authPassword='" + Strings.mask(authPassword) + '\'' +
                ", discardRdbEvent=" + discardRdbEvent +
                ", asyncCachedBytes=" + asyncCachedBytes +
                ", rateLimit=" + rateLimit +
                ", verbose=" + verbose +
                ", heartbeatPeriod=" + heartbeatPeriod +
                ", useDefaultExceptionListener=" + useDefaultExceptionListener +
                ", ssl=" + ssl +
                ", sslSocketFactory=" + sslSocketFactory +
                ", sslContextFactory=" + sslContextFactory +
                ", sslParameters=" + sslParameters +
                ", hostnameVerifier=" + hostnameVerifier +
                ", replId='" + replId + '\'' +
                ", replStreamDB=" + replStreamDB +
                ", replOffset=" + replOffset +
                '}';
    }
}
