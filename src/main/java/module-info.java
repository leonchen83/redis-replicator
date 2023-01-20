/**
 * @author Leon Chen
 * @since 2.6.0
 */
module com.moilioncircle.redis.replicator {
    exports com.moilioncircle.redis.replicator;
    exports com.moilioncircle.redis.replicator.client;
    exports com.moilioncircle.redis.replicator.cmd;
    exports com.moilioncircle.redis.replicator.cmd.impl;
    exports com.moilioncircle.redis.replicator.cmd.parser;
    exports com.moilioncircle.redis.replicator.event;
    exports com.moilioncircle.redis.replicator.io;
    exports com.moilioncircle.redis.replicator.net;
    exports com.moilioncircle.redis.replicator.rdb;
    exports com.moilioncircle.redis.replicator.rdb.datatype;
    exports com.moilioncircle.redis.replicator.rdb.dump;
    exports com.moilioncircle.redis.replicator.rdb.dump.datatype;
    exports com.moilioncircle.redis.replicator.rdb.dump.parser;
    exports com.moilioncircle.redis.replicator.rdb.iterable;
    exports com.moilioncircle.redis.replicator.rdb.iterable.datatype;
    exports com.moilioncircle.redis.replicator.rdb.module;
    exports com.moilioncircle.redis.replicator.rdb.skip;
    exports com.moilioncircle.redis.replicator.util;
    exports com.moilioncircle.redis.replicator.util.type;
    requires org.slf4j;
}