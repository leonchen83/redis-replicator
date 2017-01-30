package com.moilioncircle.redis.replicator.rdb.datatype;

import com.moilioncircle.redis.replicator.io.RedisInputStream;

/**
 * Created by Administrator on 2017/1/31.
 */
public interface Module {
    String moduleName();

    int moduleVersion();

    <T> T rdbLoad(RedisInputStream in);

    abstract class AbstractModule implements Module {
        protected final String name;
        protected final int version;

        public AbstractModule(String name, int version) {
            this.name = name;
            this.version = version;
        }

        public String moduleName() {
            return this.name;
        }

        public int moduleVersion() {
            return this.version;
        }
    }
}
