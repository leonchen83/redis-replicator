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

package com.moilioncircle.examples.scan;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisRdbReplicator;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.event.PreRdbSyncEvent;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.skip.SkipRdbVisitor;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
@SuppressWarnings("resource")
public class ScanExample {
    
    /**
     * @throws Exception
     * scan all redis data
     */
    public void scan() throws Exception {
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379?enableScan=yes&scanStep=128");
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                System.out.println(event);
            }
        });
        
        replicator.open();
    }
    
    /**
     * @throws IOException
     * scan redis and save to rdb file
     */
    public void scanToRdb() throws Exception {
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
    
        //save rdb from remote server
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379?enableScan=yes&scanStep=512");
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
    
        //check rdb file
        replicator = new RedisRdbReplicator(new File("./dump.rdb"), Configuration.defaultSetting());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                System.out.println(event);
            }
        });
        replicator.open();
    }
}
