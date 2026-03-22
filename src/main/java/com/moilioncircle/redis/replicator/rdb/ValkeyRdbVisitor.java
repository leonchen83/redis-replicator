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

package com.moilioncircle.redis.replicator.rdb;

import static java.lang.Integer.parseInt;

import java.io.IOException;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.ContextKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;
import com.moilioncircle.redis.replicator.rdb.datatype.Slot;

/**
 * RdbVisitor for Valkey RDB format.
 *
 * Reads the 6-byte "VALKEY" magic string and the 3-byte version number
 * (e.g. "080" = 80 for Valkey 8.0). All other parsing is delegated to
 * a wrapped {@link DefaultRdbVisitor}.
 *
 * Activated automatically when {@link com.moilioncircle.redis.replicator.Configuration#setFlavor}
 * is set to {@link com.moilioncircle.redis.replicator.Flavor#VALKEY}.
 *
 * @since 3.12.0
 */
public class ValkeyRdbVisitor extends RdbVisitor {

    private final DefaultRdbVisitor visitor;

    public ValkeyRdbVisitor(Replicator replicator) {
        this.visitor = new DefaultRdbVisitor(replicator);
    }

    public ValkeyRdbVisitor(Replicator replicator, RdbValueVisitor valueVisitor) {
        this.visitor = new DefaultRdbVisitor(replicator, valueVisitor);
    }

    @Override
    public String applyMagic(RedisInputStream in) throws IOException {
        String magic = BaseRdbParser.StringHelper.str(in, 6); // "VALKEY"
        if (!magic.equals("VALKEY")) {
            throw new UnsupportedOperationException("can't read MAGIC STRING [VALKEY], value: " + magic);
        }
        return magic;
    }

    @Override
    public int applyVersion(RedisInputStream in) throws IOException {
        // Valkey uses a 3-byte ASCII version string: "080" = 80, "090" = 90, ...
        int version = parseInt(BaseRdbParser.StringHelper.str(in, 3));
        if (version < 80) {
            throw new UnsupportedOperationException("can't handle Valkey RDB format version " + version);
        }
        return version;
    }

    @Override
    public int applyType(RedisInputStream in) throws IOException {
        return visitor.applyType(in);
    }

    @Override
    public Event applyFunction(RedisInputStream in, int version) throws IOException {
        return visitor.applyFunction(in, version);
    }

    @Override
    public Event applyFunction2(RedisInputStream in, int version) throws IOException {
        return visitor.applyFunction2(in, version);
    }

    @Override
    public DB applySelectDB(RedisInputStream in, int version) throws IOException {
        return visitor.applySelectDB(in, version);
    }

    @Override
    public DB applyResizeDB(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyResizeDB(in, version, context);
    }

    @Override
    public Slot applySlotInfo(RedisInputStream in, int version) throws IOException {
        return visitor.applySlotInfo(in, version);
    }

    @Override
    public long applyEof(RedisInputStream in, int version) throws IOException {
        return visitor.applyEof(in, version);
    }

    @Override
    public Event applyAux(RedisInputStream in, int version) throws IOException {
        return visitor.applyAux(in, version);
    }

    @Override
    public Event applyModuleAux(RedisInputStream in, int version) throws IOException {
        return visitor.applyModuleAux(in, version);
    }

    @Override
    public Event applyExpireTime(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyExpireTime(in, version, context);
    }

    @Override
    public Event applyExpireTimeMs(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyExpireTimeMs(in, version, context);
    }

    @Override
    public Event applyFreq(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyFreq(in, version, context);
    }

    @Override
    public Event applyIdle(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyIdle(in, version, context);
    }

    @Override
    public Event applyString(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyString(in, version, context);
    }

    @Override
    public Event applyList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyList(in, version, context);
    }

    @Override
    public Event applySet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applySet(in, version, context);
    }

    @Override
    public Event applySetListPack(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applySetListPack(in, version, context);
    }

    @Override
    public Event applyZSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyZSet(in, version, context);
    }

    @Override
    public Event applyZSet2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyZSet2(in, version, context);
    }

    @Override
    public Event applyHash(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyHash(in, version, context);
    }

    @Override
    public Event applyHashZipMap(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyHashZipMap(in, version, context);
    }

    @Override
    public Event applyListZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyListZipList(in, version, context);
    }

    @Override
    public Event applySetIntSet(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applySetIntSet(in, version, context);
    }

    @Override
    public Event applyZSetZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyZSetZipList(in, version, context);
    }

    @Override
    public Event applyZSetListPack(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyZSetListPack(in, version, context);
    }

    @Override
    public Event applyHashZipList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyHashZipList(in, version, context);
    }

    @Override
    public Event applyHashListPack(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyHashListPack(in, version, context);
    }

    @Override
    public Event applyListQuickList(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyListQuickList(in, version, context);
    }

    @Override
    public Event applyListQuickList2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyListQuickList2(in, version, context);
    }

    @Override
    public Event applyModule(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyModule(in, version, context);
    }

    @Override
    public Event applyModule2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyModule2(in, version, context);
    }

    @Override
    public Event applyStreamListPacks(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyStreamListPacks(in, version, context);
    }

    @Override
    public Event applyStreamListPacks2(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyStreamListPacks2(in, version, context);
    }

    @Override
    public Event applyStreamListPacks3(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyStreamListPacks3(in, version, context);
    }

    @Override
    public Event applyHashMetadata(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyHashMetadata(in, version, context);
    }

    @Override
    public Event applyHashListPackEx(RedisInputStream in, int version, ContextKeyValuePair context) throws IOException {
        return visitor.applyHashListPackEx(in, version, context);
    }
}
