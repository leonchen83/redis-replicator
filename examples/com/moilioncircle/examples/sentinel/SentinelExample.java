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

package com.moilioncircle.examples.sentinel;

import com.moilioncircle.examples.sentinel.impl.RedisSentinelReplicator;
import com.moilioncircle.examples.sentinel.impl.RedisSentinelURI;
import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;

/**
 * @author Leon Chen
 * @since 3.1.1
 */
public class SentinelExample {
    
    public static void main(String[] args) throws Exception {
        // sentinel hosts
        String uri = "redis-sentinel://sntiusr:sntipwd@127.0.0.1:26379,127.0.0.1:26380?master=mymaster";
        Replicator replicator = new RedisSentinelReplicator(new RedisSentinelURI(uri), Configuration.defaultSetting().setAuthPassword("masterpwd"));
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                System.out.println(event);
            }
        });
        replicator.open();
    }
}
