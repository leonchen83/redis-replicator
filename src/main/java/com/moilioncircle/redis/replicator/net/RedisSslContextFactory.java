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

package com.moilioncircle.redis.replicator.net;

import static java.util.Objects.requireNonNull;
import static javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author Leon Chen
 * @since 3.4.0
 */
public class RedisSslContextFactory implements SslContextFactory {

    private String protocol = "TLS";

    private String keyStorePath;
    private String keyStoreType;
    private String keyStorePassword;

    private String trustStorePath;
    private String trustStoreType;
    private String trustStorePassword;
    
    /*
     * if loader == null use absolute file path to load keystore and truststore
     * else use classpath to load keystore and truststore. by default use file path.
     */
    private final ClassLoader loader; 
    
    public RedisSslContextFactory() {
        this(null);
    }
    
    public RedisSslContextFactory(ClassLoader loader) {
        this.loader = loader;
    }

    public String getProtocol() {
        return protocol;
    }

    public RedisSslContextFactory setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public RedisSslContextFactory setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
        return this;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public RedisSslContextFactory setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
        return this;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public RedisSslContextFactory setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return this;
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public RedisSslContextFactory setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
        return this;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public RedisSslContextFactory setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
        return this;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public RedisSslContextFactory setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
        return this;
    }

    @Override
    public SSLContext create() {
        try {
            SSLContext context = SSLContext.getInstance(requireNonNull(this.protocol));

            KeyManager[] kms = null;
            TrustManager[] tms = new TrustManager[]{new TrustAllManager()};

            if (keyStorePath != null) {
                KeyStore ks = KeyStore.getInstance(requireNonNull(keyStoreType));
                try(InputStream in = getInputStream(loader, keyStorePath)) {
                    ks.load(in, requireNonNull(keyStorePassword).toCharArray());
                    KeyManagerFactory kmf = KeyManagerFactory.getInstance(getDefaultAlgorithm());
                    kmf.init(ks, requireNonNull(keyStorePassword).toCharArray());
                    kms = kmf.getKeyManagers();
                }
            }

            if (trustStorePath != null) {
                KeyStore ks = KeyStore.getInstance(requireNonNull(trustStoreType));
                try(InputStream in = getInputStream(loader, keyStorePath)) {
                    ks.load(in, requireNonNull(trustStorePassword).toCharArray());
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(getDefaultAlgorithm());
                    tmf.init(ks);
                    tms = tmf.getTrustManagers();
                }
            }

            context.init(kms, tms, null);
            return context;
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
    
    protected InputStream getInputStream(ClassLoader loader, String path) throws Exception {
        if (loader == null) {
            return new FileInputStream(path);
        } else {
            return loader.getResourceAsStream(path);
        }
    }

    private static class TrustAllManager implements X509TrustManager {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }
}
