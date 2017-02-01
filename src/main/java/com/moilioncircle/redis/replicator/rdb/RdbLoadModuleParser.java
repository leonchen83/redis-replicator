package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.Constants;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.util.ByteArray;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by leon on 2017/1/31.
 */
public class RdbLoadModuleParser {

    private final RedisInputStream in;
    private final BaseRdbParser parser;

    public RdbLoadModuleParser(RedisInputStream in) {
        this.in = in;
        this.parser = new BaseRdbParser(in);
    }

    public RedisInputStream inputStream() {
        return this.in;
    }

    public long loadSigned() throws IOException {
        return parser.rdbLoadLen().len;
    }

    public BigInteger loadUnSigned() throws IOException {
        return BigInteger.valueOf(loadSigned() & 0xFFFFFFFFFFFFFFFFL);
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
