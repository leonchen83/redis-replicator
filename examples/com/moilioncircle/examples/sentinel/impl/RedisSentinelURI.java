/*
 * Copyright 2016-2019 Baoyi Chen
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

package com.moilioncircle.examples.sentinel.impl;

import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import redis.clients.jedis.HostAndPort;

/**
 * @author Baoyi Chen
 * {@code 
 *   String uri = redis-sentinel://127.0.0.1:26379,127.0.0.1:26380?master=mymaster;
 *   String sslUri = redis-sentinels://sentinel:password@127.0.0.1:26379,127.0.0.1:26380?master=mymaster;
 * }
 */
public class RedisSentinelURI implements Comparable<RedisSentinelURI>, Serializable {
    //
    private static final long serialVersionUID = 1234567890123456789L;

    //
    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final int DEFAULT_PORT = 26379;
    
    /**
     * 
     */
    private String uri;
    private transient String user;
    private transient boolean ssl;
    private transient String path;
    private transient String query;
    private transient String scheme;
    private transient String userInfo;
    private transient String password;
    private transient String fragment;
    private transient List<URI> uris = new ArrayList<>();
    private transient List<HostAndPort> hosts = new ArrayList<>();
    private transient Map<String, String> parameters = new HashMap<>();

    /**
     *
     */
    public RedisSentinelURI(String uri) throws URISyntaxException {
        parse(Objects.requireNonNull(uri));
        this.uri = uri;
    }

    public boolean isSsl() {
        return ssl;
    }

    public String getPath() {
        return this.path;
    }

    public String getUser() {
        return user;
    }

    public String getQuery() {
        return this.query;
    }

    public String getScheme() {
        return this.scheme;
    }

    public List<URI> getUris() {
        return this.uris;
    }

    public String getUserInfo() {
        return this.userInfo;
    }

    public String getFragment() {
        return this.fragment;
    }

    public String getPassword() {
        return password;
    }

    public List<HostAndPort> getHosts() {
        return this.hosts;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    /**
     *
     */
    @Override
    public String toString() {
        return this.uri;
    }

    @Override
    public int hashCode() {
        return this.uri.hashCode();
    }

    @Override
    public int compareTo(RedisSentinelURI that) {
        return this.uri.compareTo(that.uri);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof RedisSentinelURI) && uri.equals(((RedisSentinelURI) o).uri);
    }

    /**
     *
     */
    private void writeObject(ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();
    }

    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        is.defaultReadObject();
        try {
            parse(this.uri);
        } catch (URISyntaxException x) {
            IOException y = new InvalidObjectException("invalid uri");
            y.initCause(x);
            throw y;
        }
    }

    /**
     *
     */
    private static int decode(char c) {
        if ((c >= '0') && (c <= '9')) return c - '0';
        if ((c >= 'a') && (c <= 'f')) return c - 'a' + 10;
        if ((c >= 'A') && (c <= 'F')) return c - 'A' + 10;
        return -1;
    }

    private static ByteBuffer normalize(String s) {
        String v = Normalizer.normalize(s, Normalizer.Form.NFC);
        return StandardCharsets.UTF_8.encode(CharBuffer.wrap(v));
    }

    private static final byte decode(char c1, char c2) {
        return (byte) ((((decode(c1) & 0xF) << 4) | ((decode(c2) & 0xF) << 0)));
    }

    private static void encode(StringBuilder s, byte b) {
        s.append('%').append(HEX[(b >> 4) & 0x0F]).append(HEX[(b >> 0) & 0x0F]);
    }

    /**
     *
     */
    public static String encode(String s) {
        //
        int n = s.length();
        if (n == 0) return s;
        int i = 0;
        while (true) {
            if (s.charAt(i) >= '\u0080') break;
            if ((++i) >= n) return s; /* NOP */
        }

        //
        final StringBuilder r = new StringBuilder(n << 1);
        final ByteBuffer bb = normalize(s);
        while (bb.hasRemaining()) {
            int b = bb.get() & 0xFF;
            if (b >= 0x80) encode(r, (byte) b);
            else r.append((char) b);
        }
        return r.toString();
    }

    private static String decode(String s) {
        //
        int n = s.length();
        if (n == 0) return s;
        if (s.indexOf('%') < 0) return s;

        //
        int i = 0;
        char c = s.charAt(0);
        StringBuilder r = new StringBuilder(n);
        ByteBuffer bb = ByteBuffer.allocate(n);
        while (i < n) {
            //
            if (c != '%') {
                r.append(c);
                if (++i >= n) break;
                c = s.charAt(i);
                continue;
            }

            //
            bb.clear();
            while (true) {
                bb.put(decode(s.charAt(++i), s.charAt(++i)));
                if (++i >= n) break;
                c = s.charAt(i);
                if (c != '%') break;
            }
            bb.flip(); /* flip */
            r.append(UTF_8.decode(bb).toString());
        }
        return r.toString();
    }

    /**
     *
     */
    private void uri(URI uri) throws URISyntaxException {
        if (uri.getScheme() != null) {
            if (uri.getScheme().equalsIgnoreCase("redis-sentinel")) {
                this.scheme = "redis-sentinel";
            } else if (uri.getScheme().equalsIgnoreCase("redis-sentinels")) {
                this.scheme = "redis-sentinels";
                this.ssl = true;
            } else {
                throw new URISyntaxException(uri.toString(), "scheme must be [redis-sentinel] or [redis-sentinels].");
            }
        } else {
            throw new URISyntaxException(uri.toString(), "scheme must be [redis-sentinel] or [redis-sentinels].");
        }
        this.uris.add(uri);
        this.path = uri.getPath();
        this.query = uri.getQuery();
        this.userInfo = uri.getUserInfo();
        this.hosts.add(new HostAndPort(uri.getHost(), uri.getPort() == -1 ? DEFAULT_PORT : uri.getPort()));
        this.scheme = uri.getScheme();
        this.fragment = uri.getFragment();

        if (this.userInfo != null) {
            int idx = this.userInfo.indexOf(':');
            if (idx < 0) {
                this.user = this.userInfo;
            } else if (idx == 0) {
                this.password = this.userInfo.substring(idx + 1);
            } else /*（idx > 0）*/{
                this.user = this.userInfo.substring(0, idx);
                String password = this.userInfo.substring(idx + 1);
                if (password != null && password.length() != 0) {
                    this.password = password;
                }
            }
        }

        String rawQuery = uri.getRawQuery();
        if (rawQuery == null)
            return;

        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        StringBuilder sb = key;
        for (char c : rawQuery.toCharArray()) {
            switch (c) {
                case '&':
                case ';':
                    if (key.length() > 0 && value.length() > 0) {
                        parameters.put(decode(key.toString()), decode(value.toString()));
                    }
                    key.setLength(0);
                    value.setLength(0);
                    sb = key;
                    break;
                case '=':
                    sb = value;
                    break;
                default:
                    sb.append(c);
            }
        }
        if (key.length() > 0 && value.length() > 0) {
            parameters.put(decode(key.toString()), decode(value.toString()));
        }
    }

    private void parse(String s) throws URISyntaxException {
        //
        final int st = s.indexOf(',');
        if (st < 0) {
            uri(new URI(s));
            return;
        }

        // 1st
        int ed = s.indexOf('/', st);
        if (ed < 0) ed = s.indexOf('?', st);
        if (ed < 0) ed = s.indexOf('#', st);
        if (ed < 0) ed = s.length(); /*end*/
        final String prefix = s.substring(0, st);
        final String suffix = s.substring(ed, s.length());
        final URI uri = new URI(prefix + suffix);
        uri(uri);

        // 2nd...
        for (String v : s.substring(st + 1, ed).split(",")) {
            //
            String host = v.trim();
            int port = DEFAULT_PORT;
            final int index = v.indexOf(':');
            if (index > 0) {
                host = v.substring(0, index).trim();
                String p = v.substring(index + 1, v.length()).trim();
                if (p != null && p.length() > 0) {
                    try {
                        port = parseInt(p);
                    } catch (NumberFormatException tx) {
                        throw new URISyntaxException(v, "invalid uri port: " + p);
                    }
                }
            }

            //
            final HostAndPort hp = new HostAndPort(host, port);
            this.hosts.add(hp);
            this.uris.add(new URI(uri.getScheme(), uri.getRawUserInfo(), hp.getHost(),
                    hp.getPort(), uri.getRawPath(), uri.getRawQuery(), uri.getRawFragment()));
        }
    }
}
