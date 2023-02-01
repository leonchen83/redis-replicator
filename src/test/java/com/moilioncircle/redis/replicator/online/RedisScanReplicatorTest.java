/*
 * Copyright 2016-2017 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.online;

import static org.junit.Assert.assertEquals;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisRdbReplicator;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.event.PreRdbSyncEvent;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.skip.SkipRdbVisitor;
import com.moilioncircle.redis.replicator.util.Strings;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
@SuppressWarnings("resource")
public class RedisScanReplicatorTest {
    
    @Test
    public void testScanToRdb() throws Exception {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("./dump.rdb")));
        RawByteListener listener = new RawByteListener() {
            @Override
            public void handle(byte... rawBytes) {
                try {
                    out.write(rawBytes);
                } catch (IOException ignore) {
                }
            }
        };
    
        try (Jedis jedis = new Jedis("127.0.0.1", 6380)) {
            jedis.auth("test");
            Pipeline pipeline = jedis.pipelined();
            for (int i = 0; i < 500; i++) {
                pipeline.set("scan " + i, "scan" + i);
            }
            pipeline.sync();
        }
        
        //save rdb from remote server
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6380?authPassword=test&enableScan=yes&scanStep=512");
        replicator.setRdbVisitor(new SkipRdbVisitor(replicator));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof PreRdbSyncEvent) {
                    replicator.addRawByteListener(listener);
                }
                if (event instanceof PostRdbSyncEvent) {
                    replicator.removeRawByteListener(listener);
                    try {
                        out.close();
                        replicator.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        });
        
        replicator.open();
        
        AtomicInteger acc = new AtomicInteger();
        //check rdb file
        replicator = new RedisRdbReplicator(new File("./dump.rdb"), Configuration.defaultSetting());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueString) {
                    KeyStringValueString kv = (KeyStringValueString) event;
                    String key = Strings.toString(kv.getKey());
                    if (key.startsWith("scan")) {
                        acc.incrementAndGet();
                    }
                }
            }
        });
        replicator.open();
        assertEquals(500, acc.get());
    }
}
