package com.moilioncircle.redis.replicator.rdb.iterable;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.FileType;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueHash;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueList;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueSet;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueStream;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyStringValueZSet;
import org.junit.Test;

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
        r.addEventListener(new ValueIterableEventListener(3, new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof BatchedKeyStringValueZSet) {
                    zset.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueString) {
                    string.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueList) {
                    list.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueSet) {
                    set.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueHash) {
                    map.incrementAndGet();
                }
            }
        }));
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
        r.addEventListener(new ValueIterableEventListener(2, new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof BatchedKeyStringValueZSet) {
                    zset.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueString) {
                    string.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueList) {
                    list.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueSet) {
                    set.incrementAndGet();
                } else if (event instanceof BatchedKeyStringValueHash) {
                    map.incrementAndGet();
                }
            }
        }));
        
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
    
    @Test
    public void test2() {
        final AtomicInteger stream = new AtomicInteger(0);
        Replicator r = new RedisReplicator(ValueIterableRdbListenerTest.class.getClassLoader().getResourceAsStream("dump-stream.rdb"), FileType.RDB, Configuration.defaultSetting());
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addEventListener(new ValueIterableEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof BatchedKeyStringValueStream) {
                    stream.incrementAndGet();
                }
            }
        }));
        try {
            r.open();
        } catch (Exception e) {
            fail();
        }
        assertEquals(5, stream.get());
    }
}