/*
 * Copyright 2016-2018 Leon Chen
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

package com.moilioncircle.examples.stream;

import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueStream;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.Stream;

import java.util.NavigableMap;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
@SuppressWarnings("resource")
public class StreamExample {
    public static void main(String[] args) throws Exception {
        Replicator r = new RedisReplicator("redis://127.0.0.1:6379");
        r.addRdbListener(new RdbListener.Adaptor() {
            @SuppressWarnings("unused")
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (kv instanceof KeyStringValueStream) {
                    // key
                    String key = kv.getKey();
                    
                    // stream
                    Stream stream = kv.getValueAsStream();
                    // last stream id
                    stream.getLastId();
                    
                    // entries
                    NavigableMap<Stream.ID, Stream.Entry> entries = stream.getEntries();
                    
                    // optional : group
                    for (Stream.Group group : stream.getGroups()) {
                        // global PEL(pending entries list)
                        NavigableMap<Stream.ID, Stream.Nack> gpel = group.getGlobalPendingEntries();
                        
                        // consumer
                        for (Stream.Consumer consumer : group.getConsumers()) {
                            // PEL(pending entries list)
                            NavigableMap<Stream.ID, Stream.Nack> pel = consumer.getPendingEntries();
                        }
                    }
                }
            }
        });
        r.open();
    }
}
