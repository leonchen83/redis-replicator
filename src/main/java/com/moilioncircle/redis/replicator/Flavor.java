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

/**
 * @since 3.12.0
 */
public enum Flavor {
    REDIS("REDIS", 4), VALKEY("VALKEY", 3);
    
    private String magic;
    private int versionDigits;

    Flavor(String magic, int versionDigits) {
        this.magic = magic;
        this.versionDigits = versionDigits;
    }
    
    public String getMagic() {
        return magic;
    }

    public int getVersionDigits() {
        return versionDigits;
    }
    
    public String convertToRdbVersion(int rdbVer) {
        return lappend(rdbVer, versionDigits, '0');
    }
    
    public int getRdbVersion(String version) {
        if (this == REDIS) {
            if (!REDIS_VERSIONS.containsKey(version)) {
                throw new AssertionError("unsupported redis version :" + version);
            }
            return REDIS_VERSIONS.get(version);
        } else if (this == VALKEY) {
            if (!VALKEY_VERSIONS.containsKey(version)) {
                throw new AssertionError("unsupported valkey version :" + version);
            }
            return VALKEY_VERSIONS.get(version);
        } else {
            throw ERROR;
        }
    }

    // "9.0.0" is the minimum Valkey server version whose RDB format this replicator supports.
    // If Valkey's versioning evolves to require different RDB formats per release,
    // consider introducing a more granular Flavor (e.g., VALKEY_9, VALKEY_10) at that point.
    public String getSlaveRdbVersion() {
        if (this == VALKEY) return "9.0.0";
        throw new UnsupportedOperationException(this + " does not use slave RDB version negotiation");
    }

    public void validateRdbVersion(int version) {
        if (this == REDIS) {
            if (version < 2 || version > RDB_VERSION) {
                throw new UnsupportedOperationException("can't handle RDB format version " + version);
            }
        } else if (this == VALKEY) {
            if (version != VALKEY_VERSION) {
                throw new UnsupportedOperationException("can't handle RDB format version " + version);
            }
        }
    }

    public static Flavor toFlavor(String flavor) {
        if (flavor == null) throw ERROR;
        if (flavor.equals(REDIS.magic.toLowerCase())) return REDIS;
        if (flavor.equals(VALKEY.magic.toLowerCase())) return VALKEY;
        throw ERROR;
    }
    
    //
    private static final Map<String, Integer> REDIS_VERSIONS = new HashMap<>();
    private static final Map<String, Integer> VALKEY_VERSIONS = new HashMap<>();
    private static Error ERROR = new AssertionError("unsupported flavor");
    
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
