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

package com.moilioncircle.redis.replicator;

import static com.moilioncircle.redis.replicator.Constants.RDB_VERSION;
import static com.moilioncircle.redis.replicator.Constants.VALKEY_VERSION;
import static com.moilioncircle.redis.replicator.util.Strings.lappend;

import java.util.HashMap;
import java.util.Map;

import com.moilioncircle.redis.replicator.rdb.DefaultRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.RdbVisitor;

/**
 * @since 3.12.0
 */
public enum Flavor implements FlavorSupport {
    REDIS {
        @Override
        public String magic() {
            return "REDIS";
        }

        @Override
        public int versionDigits() {
            return 4;
        }

        @Override
        public String formatRdbVersion(int version) {
            return lappend(version, versionDigits(), '0');
        }

        @Override
        public int resolveRdbVersion(String version) {
            return REDIS_VERSIONS.get(version);
        }

        @Override
        public void validateRdbVersion(int version) {
            if (version < 2 || version > RDB_VERSION) {
                throw new UnsupportedOperationException("can't handle RDB format version " + version);
            }
        }

        @Override
        public String slaveRdbVersion() {
            throw new UnsupportedOperationException(this + " does not use slave RDB version negotiation");
        }

        @Override
        public RdbVisitor rdbVisitor(Replicator replicator) {
            return new DefaultRdbVisitor(replicator);
        }
    }, VALKEY {
        @Override
        public String magic() {
            return "VALKEY";
        }

        @Override
        public int versionDigits() {
            return 3;
        }

        @Override
        public String formatRdbVersion(int version) {
            return lappend(version, versionDigits(), '0');
        }

        @Override
        public int resolveRdbVersion(String version) {
            return VALKEY_VERSIONS.get(version);
        }

        @Override
        public void validateRdbVersion(int version) {
            if (version != VALKEY_VERSION) {
                throw new UnsupportedOperationException("can't handle RDB format version " + version);
            }
        }

        // "9.0.0" is the minimum Valkey server version whose RDB format this replicator supports.
        // If Valkey's versioning evolves to require different RDB formats per release,
        // consider introducing a more granular Flavor (e.g., VALKEY_9, VALKEY_10) at that point.
        @Override
        public String slaveRdbVersion() {
            return "9.0.0";
        }

        @Override
        public RdbVisitor rdbVisitor(Replicator replicator) {
            return new DefaultRdbVisitor(replicator);
        }
    };

    public static Flavor toFlavor(String flavor) {
        if (flavor.equals(REDIS.magic().toLowerCase())) return REDIS;
        if (flavor.equals(VALKEY.magic().toLowerCase())) return VALKEY;
        throw new AssertionError(flavor);
    }

    //
    private static final Map<String, Integer> REDIS_VERSIONS = new HashMap<>();
    private static final Map<String, Integer> VALKEY_VERSIONS = new HashMap<>();

    static {
        REDIS_VERSIONS.put("2.6", 6);
        REDIS_VERSIONS.put("2.8", 6);
        REDIS_VERSIONS.put("3.0", 6);
        REDIS_VERSIONS.put("3.2", 7);
        REDIS_VERSIONS.put("4.0", 8);
        REDIS_VERSIONS.put("5.0", 9);
        REDIS_VERSIONS.put("6.0", 9);
        REDIS_VERSIONS.put("6.2", 9);
        REDIS_VERSIONS.put("7.0", 10);
        REDIS_VERSIONS.put("7.2", 11);
        REDIS_VERSIONS.put("7.4", RDB_VERSION);
        REDIS_VERSIONS.put("8.0", RDB_VERSION);
        REDIS_VERSIONS.put("8.2", RDB_VERSION);
        REDIS_VERSIONS.put("8.4", RDB_VERSION);

        // VALKEY_VERSIONS.put("7.2", 11);
        // VALKEY_VERSIONS.put("8.0", 11);
        // VALKEY_VERSIONS.put("8.1", 11);
        VALKEY_VERSIONS.put("9.0", VALKEY_VERSION);
        VALKEY_VERSIONS.put("9.1", VALKEY_VERSION);
    }
}
