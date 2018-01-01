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
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.datatype.ZSetEntry;
import com.moilioncircle.redis.replicator.rdb.iterable.ValueIterableRdbVisitor;

import java.util.List;
import java.util.Map;

/**
 * @author Leon Chen
 * @since 2.4.4
 */
public class HugeKVSocketExample {

    public static void main(String[] args) throws Exception {
        Replicator r = new RedisReplicator("redis://127.0.0.1:6379");
        r.setRdbVisitor(new ValueIterableRdbVisitor(r));
        r.addRdbListener(new HugeKVRdbListener(200) {
            @Override
            public void handleString(boolean last, byte[] key, byte[] value, int type) {
                // your business code goes here.
            }

            @Override
            public void handleModule(boolean last, byte[] key, Module value, int type) {
                // your business code goes here.
            }

            @Override
            public void handleList(boolean last, byte[] key, List<byte[]> list, int type) {
                // your business code goes here.
            }

            @Override
            public void handleZSetEntry(boolean last, byte[] key, List<ZSetEntry> list, int type) {
                // your business code goes here.
            }

            @Override
            public void handleMap(boolean last, byte[] key, List<Map.Entry<byte[], byte[]>> list, int type) {
                // your business code goes here.
            }
        });
        r.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                System.out.println(command);
            }
        });
        r.open();
    }
}
