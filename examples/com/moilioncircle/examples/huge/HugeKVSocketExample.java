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

package com.moilioncircle.examples.huge;

import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.rdb.iterable.ValueIterableEventListener;
import com.moilioncircle.redis.replicator.rdb.iterable.ValueIterableRdbVisitor;
import com.moilioncircle.redis.replicator.rdb.iterable.datatype.BatchedKeyValuePair;

/**
 * @author Leon Chen
 * @since 2.4.4
 */
public class HugeKVSocketExample {

    public static void main(String[] args) throws Exception {
        Replicator r = new RedisReplicator("redis://127.0.0.1:6379");
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addEventListener(new ValueIterableEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof BatchedKeyValuePair<?, ?>) {
                    // do something
                }
                if (event instanceof Command) {
                    System.out.println(event);
                }
            }
        }));
        r.open();
    }
}
