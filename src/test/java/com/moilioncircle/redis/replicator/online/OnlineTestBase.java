/*
 * Copyright 2026
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

package com.moilioncircle.redis.replicator.online;

import org.junit.Rule;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.Flavor;

import redis.clients.jedis.Jedis;

/**
 * Base class for online tests.
 *
 * Set -Dtest.flavor=valkey to run tests against a Valkey server.
 *
 * @since 3.12.0
 */
public abstract class OnlineTestBase {

    protected static final String HOST = "127.0.0.1";
    protected static final int PORT = 6379;
    protected static final Flavor FLAVOR = "valkey".equalsIgnoreCase(System.getProperty("test.flavor"))
            ? Flavor.VALKEY : null;

    @Rule
    public final FlavorRule flavorRule = new FlavorRule();

    protected Configuration config() {
        Configuration config = Configuration.defaultSetting().setRetries(0);
        if (FLAVOR != null) config.setFlavor(FLAVOR);
        return config;
    }

    protected Jedis jedis() {
        return new Jedis(HOST, PORT);
    }
}
