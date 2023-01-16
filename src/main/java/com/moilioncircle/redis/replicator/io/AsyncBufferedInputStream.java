/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Jingqi Xu
 * @author andyqzb
 * @since 2.1.0
 */
public final class AsyncBufferedInputStream extends AbstractAsyncInputStream implements Runnable {
    
    protected final Thread worker;
    protected final ThreadFactory threadFactory;
    
    /*
     *
     */
    public AsyncBufferedInputStream(InputStream is) {
        this(is, DEFAULT_CAPACITY);
    }
    
    public AsyncBufferedInputStream(InputStream is, int size) {
        this(is, size, Executors.defaultThreadFactory());
    }
    
    public AsyncBufferedInputStream(InputStream is, int size, ThreadFactory tf) {
        super(is, size);
        this.threadFactory = tf;
        
        //
        this.worker = this.threadFactory.newThread(this);
        this.worker.start();
    }
    
    /*
     *
     */
    @Override
    public void run() {
        try {
            final byte[] buffer = new byte[512 * 1024];
            while (!this.closed.get()) {
                //
                int r = ((InputStream) this.resource).read(buffer, 0, buffer.length);
                if (r < 0) throw new EOFException();
                
                //
                int offset = 0;
                while (r > 0) {
                    final int w = write(buffer, offset, r);
                    r -= w;
                    offset += w;
                }
            }
        } catch (IOException e) {
            this.exception = e;
        } catch (Exception e) {
            logger.error("failed to transfer data", e);
        } finally {
            if (!this.closed.get()) {
                try {
                    close();
                } catch (IOException e) {
                    logger.error("failed to close is", e);
                }
            }
        }
    }
}
