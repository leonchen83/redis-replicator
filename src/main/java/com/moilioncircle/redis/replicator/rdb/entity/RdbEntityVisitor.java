package com.moilioncircle.redis.replicator.rdb.entity;

import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.DB;

import java.io.IOException;

/**
 * Created by leon on 2/1/17.
 */
public interface RdbEntityVisitor {
    /*
     * rdb prefix
     */
    String applyMagic(RedisInputStream in) throws IOException;

    int applyVersion(RedisInputStream in) throws IOException;

    int applyType(RedisInputStream in) throws IOException;

    /*
     * DB
     */
    DB applySelectDB(RedisInputStream in, int version) throws IOException;

    DB applyResizeDB(RedisInputStream in, DB db, int version) throws IOException;

    /*
     * checksum
     */
    long applyEof(RedisInputStream in, int version) throws IOException;

    /*
     * aux
     */
    Event applyAux(RedisInputStream in, int version) throws IOException;

    /*
     * entity
     */
    Event applyExpireTime(RedisInputStream in, DB db, int version) throws IOException;

    Event applyExpireTimeMs(RedisInputStream in, DB db, int version) throws IOException;

    Event applyString(RedisInputStream in, DB db, int version) throws IOException;

    Event applyList(RedisInputStream in, DB db, int version) throws IOException;

    Event applySet(RedisInputStream in, DB db, int version) throws IOException;

    Event applyZSet(RedisInputStream in, DB db, int version) throws IOException;

    Event applyZSet2(RedisInputStream in, DB db, int version) throws IOException;

    Event applyHash(RedisInputStream in, DB db, int version) throws IOException;

    Event applyHashZipMap(RedisInputStream in, DB db, int version) throws IOException;

    Event applyListZipList(RedisInputStream in, DB db, int version) throws IOException;

    Event applySetIntSet(RedisInputStream in, DB db, int version) throws IOException;

    Event applyZSetZipList(RedisInputStream in, DB db, int version) throws IOException;

    Event applyHashZipList(RedisInputStream in, DB db, int version) throws IOException;

    Event applyListQuickList(RedisInputStream in, DB db, int version) throws IOException;

    Event applyModule(RedisInputStream in, DB db, int version) throws IOException;
}
