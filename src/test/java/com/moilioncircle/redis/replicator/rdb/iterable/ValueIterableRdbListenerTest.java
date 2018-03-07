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
        final AtomicInteger string = new AtomicInteger(0);
        final AtomicInteger map = new AtomicInteger(0);
        final AtomicInteger zset = new AtomicInteger(0);
        final AtomicInteger set = new AtomicInteger(0);
        final AtomicInteger list = new AtomicInteger(0);
        Replicator r = new RedisReplicator(ValueIterableRdbListenerTest.class.getClassLoader().getResourceAsStream("dump-huge-kv.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addRdbListener(new ValueIterableRdbListener(3) {
            @Override
            public void handleString(KeyValuePair<byte[]> kv, int batch, boolean last) {
                string.incrementAndGet();
            }

            @Override
            public void handleList(KeyValuePair<List<byte[]>> kv, int batch, boolean last) {
                list.incrementAndGet();
            }

            @Override
            public void handleSet(KeyValuePair<Set<byte[]>> kv, int batch, boolean last) {
                set.incrementAndGet();
            }

            @Override
            public void handleMap(KeyValuePair<Map<byte[], byte[]>> kv, int batch, boolean last) {
                map.incrementAndGet();
            }

            @Override
            public void handleZSetEntry(KeyValuePair<Set<ZSetEntry>> kv, int batch, boolean last) {
                zset.incrementAndGet();
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
        assertEquals(1, string.get());
        assertEquals(4, map.get());
        assertEquals(4, list.get());
        assertEquals(4, set.get());
        assertEquals(4, zset.get());
    }

    @Test
    public void test1() {
        final AtomicInteger string = new AtomicInteger(0);
        final AtomicInteger map = new AtomicInteger(0);
        final AtomicInteger zset = new AtomicInteger(0);
        final AtomicInteger set = new AtomicInteger(0);
        final AtomicInteger list = new AtomicInteger(0);
        Replicator r = new RedisReplicator(ValueIterableRdbListenerTest.class.getClassLoader().getResourceAsStream("dump-huge-kv.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addRdbListener(new ValueIterableRdbListener(2) {
            @Override
            public void handleString(KeyValuePair<byte[]> kv, int batch, boolean last) {
                string.incrementAndGet();
            }

            @Override
            public void handleList(KeyValuePair<List<byte[]>> kv, int batch, boolean last) {
                list.incrementAndGet();
            }

            @Override
            public void handleSet(KeyValuePair<Set<byte[]>> kv, int batch, boolean last) {
                set.incrementAndGet();
            }

            @Override
            public void handleMap(KeyValuePair<Map<byte[], byte[]>> kv, int batch, boolean last) {
                map.incrementAndGet();
            }

            @Override
            public void handleZSetEntry(KeyValuePair<Set<ZSetEntry>> kv, int batch, boolean last) {
                zset.incrementAndGet();
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
        assertEquals(1, string.get());
        assertEquals(5, map.get());
        assertEquals(5, list.get());
        assertEquals(5, set.get());
        assertEquals(5, zset.get());
    }
}