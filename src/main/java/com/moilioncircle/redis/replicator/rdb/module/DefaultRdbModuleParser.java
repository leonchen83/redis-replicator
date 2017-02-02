package com.moilioncircle.redis.replicator.rdb.module;

import com.moilioncircle.redis.replicator.Constants;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.BaseRdbParser;
import com.moilioncircle.redis.replicator.util.ByteArray;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Created by leon on 2017/1/31.
 */
public class DefaultRdbModuleParser {
    protected static final Log logger = LogFactory.getLog(DefaultRdbModuleParser.class);

    private final RedisInputStream in;
    private final BaseRdbParser parser;

    public DefaultRdbModuleParser(RedisInputStream in) {
        this.in = in;
        this.parser = new BaseRdbParser(in);
    }

    public RedisInputStream inputStream() {
        return this.in;
    }

    public long loadSigned() throws IOException {
        return parser.rdbLoadLen().len;
    }

    public long loadUnSigned() throws IOException {
        logger.warn("Un-support [loadUnSigned]. using [loadSigned] instead");
        return loadSigned();
    }

    public String loadString() throws IOException {
        ByteArray bytes = (ByteArray) parser.rdbGenericLoadStringObject(Constants.RDB_LOAD_NONE);
        return new String(bytes.first(), Constants.CHARSET);
    }

    public String loadStringBuffer() throws IOException {
        ByteArray bytes = (ByteArray) parser.rdbGenericLoadStringObject(Constants.RDB_LOAD_PLAIN);
        return new String(bytes.first(), Constants.CHARSET);
    }

    public double loadDouble() throws IOException {
        return parser.rdbLoadBinaryDoubleValue();
    }
}
