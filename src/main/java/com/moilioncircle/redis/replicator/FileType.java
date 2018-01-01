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

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public enum FileType {
    AOF, RDB, MIXED;

    /**
     * @param type string type
     * @return FileType
     * @since 2.4.0
     */
    static FileType parse(String type) {
        if (type == null) {
            return null;
        } else if (type.equalsIgnoreCase("aof")) {
            return AOF;
        } else if (type.equalsIgnoreCase("rdb")) {
            return RDB;
        } else if (type.equalsIgnoreCase("mix")) {
            return MIXED;
        } else if (type.equalsIgnoreCase("mixed")) {
            return MIXED;
        } else {
            return null;
        }
    }
}
