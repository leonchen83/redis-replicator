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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import com.moilioncircle.redis.replicator.net.SslContextFactory;

/**
 * @author Leon Chen
 * @since 3.4.0
 */
public class SslConfiguration {

    private SslConfiguration() {
    }

    /**
     * factory
     *
     * @return SslConfiguration
     */
    public static SslConfiguration defaultSetting() {
        return new SslConfiguration();
    }
    
    /**
     * ssl socket factory
     */
    private SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

    /**
     * ssl context factory
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

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public SslConfiguration setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public SslContextFactory getSslContextFactory() {
        return sslContextFactory;
    }

    public SslConfiguration setSslContextFactory(SslContextFactory sslContextFactory) {
        this.sslContextFactory = sslContextFactory;
        return this;
    }

    public SSLParameters getSslParameters() {
        return sslParameters;
    }

    public SslConfiguration setSslParameters(SSLParameters sslParameters) {
        this.sslParameters = sslParameters;
        return this;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public SslConfiguration setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public String toString() {
        return "SslConfiguration{" +
                "sslSocketFactory=" + sslSocketFactory +
                ", sslContextFactory=" + sslContextFactory +
                ", sslParameters=" + sslParameters +
                ", hostnameVerifier=" + hostnameVerifier +
                '}';
    }
}
