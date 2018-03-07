package com.moilioncircle.redis.replicator.rdb.dump;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.dump.datatype.DumpKeyValuePair;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class DumpRdbVisitorTest {
    @Test
    public void test1() {
        final byte[] string = ByteParser.toBytes("\\x00\\x1e\\xe4\\xb8\\xad\\xe5\\x9b\\xbd\\xe9\\x93\\xb6\\xe8\\xa1\\x8c\\xe5\\x85\\xa8\\xe7\\x90\\x83\\xe9\\x97\\xa8\\xe6\\x88\\xb7\\xe7\\xbd\\x91\\xe7\\xab\\x99\\b\\x00k\\xe3\\x19'\\x94\\xe6\\x8a9");
        byte[] set = ByteParser.toBytes("\\x0b\\x1c\\x02\\x00\\x00\\x00\\n\\x00\\x00\\x00\\x01\\x00\\x02\\x00\\x03\\x00\\x04\\x00\\x05\\x00\\x06\\x00\\a\\x00\\b\\x00\\t\\x00\\n\\x00\\b\\x00w\\xa2\\x0fZ~\\xf5\\xb8\\x80");
        byte[] list = ByteParser.toBytes("\\x0e\\x01\\x1f\\x1f\\x00\\x00\\x00\\x1c\\x00\\x00\\x00\\n\\x00\\x00\\xfb\\x02\\xfa\\x02\\xf9\\x02\\xf8\\x02\\xf7\\x02\\xf6\\x02\\xf5\\x02\\xf4\\x02\\xf3\\x02\\xf2\\xff\\b\\x00\\xd5T\\xbeK\\xcdf\\x0f\\x1b");
        byte[] map = ByteParser.toBytes("\\r33\\x00\\x00\\x000\\x00\\x00\\x00\\x14\\x00\\x00\\xf2\\x02\\xf2\\x02\\xf3\\x02\\xf3\\x02\\xf4\\x02\\xf4\\x02\\xf5\\x02\\xf5\\x02\\xf6\\x02\\xf6\\x02\\xf7\\x02\\xf7\\x02\\xf8\\x02\\xf8\\x02\\xf9\\x02\\xf9\\x02\\xfa\\x02\\xfa\\x02\\xfb\\x02\\xfb\\xff\\b\\x00\\xee2\\x87;\\xceN\\x93P");
        byte[] zset = ByteParser.toBytes("\\x0c33\\x00\\x00\\x000\\x00\\x00\\x00\\x14\\x00\\x00\\xf2\\x02\\xf2\\x02\\xf3\\x02\\xf3\\x02\\xf4\\x02\\xf4\\x02\\xf5\\x02\\xf5\\x02\\xf6\\x02\\xf6\\x02\\xf7\\x02\\xf7\\x02\\xf8\\x02\\xf8\\x02\\xf9\\x02\\xf9\\x02\\xfa\\x02\\xfa\\x02\\xfb\\x02\\xfb\\xff\\b\\x00\\x87\\xb7\\x16\\xf8\\xc9^\\xaf\\\\");
        final AtomicReference<byte[]> astring = new AtomicReference<>();
        final AtomicReference<byte[]> amap = new AtomicReference<>();
        final AtomicReference<byte[]> alist = new AtomicReference<>();
        final AtomicReference<byte[]> azset = new AtomicReference<>();
        final AtomicReference<byte[]> aset = new AtomicReference<>();
        Replicator r = new RedisReplicator(DumpRdbVisitorTest.class.getClassLoader().getResourceAsStream("dump-huge-kv.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new DumpRdbVisitor(r));
        r.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (!(kv instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair dkv = (DumpKeyValuePair) kv;
                if (dkv.getKey().equals("k10")) {
                    amap.set(dkv.getValue());
                } else if (dkv.getKey().equals("list10")) {
                    alist.set(dkv.getValue());
                } else if (dkv.getKey().equals("zset")) {
                    azset.set(dkv.getValue());
                } else if (dkv.getKey().equals("set")) {
                    aset.set(dkv.getValue());
                } else if (dkv.getKey().equals("s")) {
                    astring.set(dkv.getValue());
                }
            }
        });
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }
        assertArrayEquals(string, astring.get());
        assertArrayEquals(set, aset.get());
        assertArrayEquals(zset, azset.get());
        assertArrayEquals(list, alist.get());
        assertArrayEquals(map, amap.get());
    }
}