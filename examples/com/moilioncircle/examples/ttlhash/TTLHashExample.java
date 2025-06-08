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

package com.moilioncircle.examples.ttlhash;

import java.util.Map;

import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueTTLHash;
import com.moilioncircle.redis.replicator.rdb.datatype.TTLValue;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * @author Leon Chen
 * @since 3.9.0
 */
public class TTLHashExample {
    public static void main(String[] args) throws Exception {
        Replicator r = new RedisReplicator("redis://127.0.0.1:6379");
        r.addEventListener(new EventListener() {
            @SuppressWarnings("unused")
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueTTLHash) {
                    KeyStringValueTTLHash skv = (KeyStringValueTTLHash) event;
                    // key
                    byte[] key = skv.getKey();
                    
                    // ttl hash
                    Map<byte[], TTLValue> ttlHash = skv.getValue();
                    for (Map.Entry<byte[], TTLValue> entry : ttlHash.entrySet()) {
                        System.out.println("field:" + Strings.toString(entry.getKey()));
                        System.out.println("value:" + Strings.toString(entry.getValue().getValue()));
                        System.out.println("field ttl:" + entry.getValue().getExpires());
                    }
                }
            }
        });
        r.open();
    }
}
