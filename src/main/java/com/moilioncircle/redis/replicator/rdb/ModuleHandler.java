package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;

/**
 * Created by Administrator on 2017/1/31.
 */
public interface ModuleHandler {
    Module rdbLoad(RedisInputStream in);
}
