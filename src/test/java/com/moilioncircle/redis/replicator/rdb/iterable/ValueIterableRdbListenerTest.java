package com.moilioncircle.redis.replicator.rdb.iterable;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class ValueIterableRdbListenerTest {

    @Test
    public void test() {
        final AtomicInteger acc = new AtomicInteger(0);
        Replicator r = new RedisReplicator(ValueIterableRdbListenerTest.class.getClassLoader().getResourceAsStream("dump-huge-kv.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addRdbListener(new ValueIterableRdbListener(3) {
            @Override
            public void handleString(KeyValuePair<byte[]> kv, int batch, boolean last) {
            }

            @Override
            public void handleList(KeyValuePair<List<byte[]>> kv, int batch, boolean last) {
            }

            @Override
            public void handleSet(KeyValuePair<Set<byte[]>> kv, int batch, boolean last) {
            }

            @Override
            public void handleMap(KeyValuePair<Map<byte[], byte[]>> kv, int batch, boolean last) {
                acc.incrementAndGet();
            }

            @Override
            public void handleZSetEntry(KeyValuePair<Set<ZSetEntry>> kv, int batch, boolean last) {
            }

            @Override
            public void handleModule(KeyValuePair<Module> kv, int batch, boolean last) {
            }
        });
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }

        assertEquals(4, acc.get());

    }

    @Test
    public void test1() {
        final AtomicInteger acc = new AtomicInteger(0);
        Replicator r = new RedisReplicator(ValueIterableRdbListenerTest.class.getClassLoader().getResourceAsStream("dump-huge-kv.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addRdbListener(new ValueIterableRdbListener(2) {
            @Override
            public void handleString(KeyValuePair<byte[]> kv, int batch, boolean last) {
            }

            @Override
            public void handleList(KeyValuePair<List<byte[]>> kv, int batch, boolean last) {
            }

            @Override
            public void handleSet(KeyValuePair<Set<byte[]>> kv, int batch, boolean last) {
            }

            @Override
            public void handleMap(KeyValuePair<Map<byte[], byte[]>> kv, int batch, boolean last) {
                acc.incrementAndGet();
            }

            @Override
            public void handleZSetEntry(KeyValuePair<Set<ZSetEntry>> kv, int batch, boolean last) {
            }

            @Override
            public void handleModule(KeyValuePair<Module> kv, int batch, boolean last) {
            }
        });
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }

        assertEquals(5, acc.get());

    }
}