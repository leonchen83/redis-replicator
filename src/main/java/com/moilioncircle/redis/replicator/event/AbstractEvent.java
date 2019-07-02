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

package com.moilioncircle.redis.replicator.event;

import com.moilioncircle.redis.replicator.util.type.Tuple2;

/**
 * @author Leon Chen
 * @since 3.3.0
 */
public abstract class AbstractEvent implements Event {
    protected Context context = new ContextImpl();

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private static class ContextImpl implements Context {
        private static final long serialVersionUID = 1L;

        private Tuple2<Long, Long> offsets;

        /**
         * Fetch offset behavior:
         * 
         * 1. if event is self-define event like {@link com.moilioncircle.redis.replicator.event.PreRdbSyncEvent} 
         *    or {@link com.moilioncircle.redis.replicator.event.PreCommandSyncEvent}  and etc. 
         *    then offset.getV2() - offset.getV1() = 0. 
         *    Notice that {@link com.moilioncircle.redis.replicator.event.PostRdbSyncEvent} is not self-define event.
         *    it contains 9 bytes with 1 byte rdb type and 8 bytes checksum.
         *    
         * 2. if event from Rdb file or Aof file or Mixed file(set aof-use-rdb-preamble yes in redis.conf). 
         *    the offset is the start position and end position in corresponding file. 
         *    Notice that the we ignore following Rdb type's offset
         *    RDB_OPCODE_RESIZEDB, RDB_OPCODE_SELECTDB, RDB_OPCODE_MODULE_AUX. so the offset may be discontinuous.
         *    
         * 3. if event from redis replication protocol via socket.
         *    then the KeyValuePair event's offset is the position from Rdb file, and Command event's offset is 
         *    redis replication backlog's offset. so the Command event's offset may less then the KeyValuePair event's offset.
         *    
         * Calculate the offset:
         * 
         * RDB
         * | rdb type(1 byte)        | rdb key value content  |
         * | start offset(inclusion) | end offset(exclusion)  |
         * 
         * start offset contains rdb type. so offset.getV2() - offset.getV1() = (rdb type) + (rdb key value content) 
         * 
         * AOF
         * |*3\r\n$3\r\nset\r\n$1\r\na\r\n$1\r\nb\r\n(set a b) |
         * | start offset(inclusion) , end offset(exclusion)   |
         * then offset.getV2() - offset.getV1() = 27
         * 
         * @return a Tuple2 with the start offset and end offset
         */
        @Override
        public Tuple2<Long, Long> getOffsets() {
            return offsets;
        }

        @Override
        public void setOffsets(Tuple2<Long, Long> offset) {
            this.offsets = offset;
        }
    }
}
