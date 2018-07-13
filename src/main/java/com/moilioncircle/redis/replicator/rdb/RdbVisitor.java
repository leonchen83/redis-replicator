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

package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.ContextKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;

import java.io.IOException;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public abstract class RdbVisitor {

    /*
     * rdb prefix
     */
    public String applyMagic(RedisInputStream in) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public int applyVersion(RedisInputStream in) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public int applyType(RedisInputStream in) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    /*
     * DB
     */
    public DB applySelectDB(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public DB applyResizeDB(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    /*
     * checksum
     */
    public long applyEof(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    /*
     * aux
     */
    public Event applyAux(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyModuleAux(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    /*
     * entity
     */
    public Event applyExpireTime(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyExpireTimeMs(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyFreq(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyIdle(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyString(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applySet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyZSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyZSet2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyHash(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyHashZipMap(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyListZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applySetIntSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyZSetZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyHashZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyListQuickList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyModule(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyModule2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyStreamListPacks(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
}
