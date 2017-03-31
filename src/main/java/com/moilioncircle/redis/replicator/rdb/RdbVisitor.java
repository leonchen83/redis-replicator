package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;

import java.io.IOException;

/**
 * Created by leon on 2/1/17.
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

    public DB applyResizeDB(RedisInputStream in, DB db, int version) throws IOException {
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

    /*
     * entity
     */
    public Event applyExpireTime(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyExpireTimeMs(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyString(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyList(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applySet(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyZSet(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyZSet2(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyHash(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyHashZipMap(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyListZipList(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applySetIntSet(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyZSetZipList(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyHashZipList(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyListQuickList(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public Event applyModule(RedisInputStream in, DB db, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
}
