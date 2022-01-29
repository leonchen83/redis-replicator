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

package com.moilioncircle.redis.replicator;

import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;

/**
 * @author Leon Chen
 * @since 3.6.0
 */
public enum DefaultReplFilter implements ReplFilter {
    
    AOF {
        @Override
        public String[] command() {
            return new String[]{"REPLCONF", "rdb-filter-only", ""};
        }
    
        @Override
        public EventListener listener(Replicator replicator) {
            replicator.getConfiguration().setDiscardRdbEvent(true);
            return null;
        }
    },
    
    FUNCTION {
        @Override
        public String[] command() {
            return new String[]{"REPLCONF", "rdb-filter-only", "functions"};
        }
    },
    
    RDB {
        @Override
        public String[] command() {
            return new String[]{"REPLCONF", "rdb-only", "1"};
        }
        
        @Override
        public EventListener listener(Replicator replicator) {
            return new EventListener() {
                @Override
                public void onEvent(Replicator replicator, Event event) {
                    if (event instanceof PostRdbSyncEvent) Replicators.closeQuietly(replicator);
                }
            };
        }
    };
}
