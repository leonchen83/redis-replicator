package com.moilioncircle.redis.replicator.rdb;

import com.moilioncircle.redis.replicator.io.RedisInputStream;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by Administrator on 2017/1/31.
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
        byte[] bytes = parser.rdbLoadRawStringObject().first();
        return new String(bytes);
    }

    public String loadStringBuffer() throws IOException {
        //TODO
        return loadString();
    }

    public double loadDouble() throws IOException {
        return parser.rdbLoadBinaryDoubleValue();
    }
}
