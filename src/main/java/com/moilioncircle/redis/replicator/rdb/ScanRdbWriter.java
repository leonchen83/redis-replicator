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

package com.moilioncircle.redis.replicator.rdb;

import java.io.IOException;
import java.util.Queue;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RESP2;
import com.moilioncircle.redis.replicator.util.type.Tuple2;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
public class ScanRdbWriter {
    
    private RESP2.Client client;
    private Configuration configuration;
    
    public ScanRdbWriter(String host, int port, Configuration configuration) throws IOException {
        this.configuration = configuration;
        this.client = new RESP2.Client(host, port, configuration);
    }
    
    public <T> T retry(RESP2.Function<RESP2.Client, T> function) throws IOException {
        IOException exception = null;
        for (int i = 0; i < configuration.getRetries() || configuration.getRetries() <= 0; i++) {
            try {
                return function.apply(client);
            } catch (IOException e) {
                exception = e;
                try {
                    this.client = RESP2.Client.valueOf(this.client);
                } catch (IOException ex) {
                }
            }
        }
        throw exception;
    }
    
    public void retry(RESP2.Response prev) throws IOException {
        IOException exception = null;
        for (int i = 0; i < configuration.getRetries() || configuration.getRetries() <= 0; i++) {
            try {
                prev.get();
                return;
            } catch (IOException e) {
                exception = e;
                try {
                    this.client = RESP2.Client.valueOf(this.client);
                    Queue<Tuple2<RESP2.Context, byte[][]>> responses = prev.responses();
                    RESP2.Response next = this.client.newCommand();
                    while (!responses.isEmpty()) {
                        Tuple2<RESP2.Context, byte[][]> tuple2 = responses.poll();
                        RESP2.Context context = tuple2.getV1();
                        if (context instanceof RESP2.NodeConsumer) {
                            next.send((RESP2.NodeConsumer) context , tuple2.getV2());
                        } else if (context instanceof RESP2.BulkConsumer) {
                            next.send((RESP2.BulkConsumer) context , tuple2.getV2());
                        }
                    }
                    retry(next);
                } catch (IOException ex) {
                }
            }
        }
        throw exception;
    }
}
