/*
 * Copyright 2016 leon chen
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

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.RdbParser;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by leon on 8/13/16.
 */
class RedisFileReplicator extends AbstractReplicator {

    public RedisFileReplicator(File file, Configuration configuration) throws FileNotFoundException {
        //bug fix http://git.oschina.net/leonchen83/redis-replicator/issues/2
        this(new FileInputStream(file), configuration);
    }

    public RedisFileReplicator(InputStream in, Configuration configuration) {
        this.configuration = configuration;
        this.inputStream = new RedisInputStream(in, this.configuration.getBufferSize());
        this.eventQueue = new ArrayBlockingQueue<>(this.configuration.getEventQueueSize());
    }

    @Override
    public void open() throws IOException {
        worker.start();
        RdbParser parser = new RdbParser(inputStream, this);
        parser.parse();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        if (worker != null && !worker.isClosed()) worker.close();
    }
}
