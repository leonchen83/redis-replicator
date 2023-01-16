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

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jingqi Xu
 * @author andyqzb
 * @author Leon Chen
 * @since 3.7.0
 */
public abstract class AbstractAsyncInputStream extends InputStream {
    //
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    //
    protected static final int DEFAULT_CAPACITY = 2 * 1024 * 1024;
    
    //
    protected Closeable resource;
    protected volatile IOException exception;
    protected final ByteRingBuffer ringBuffer;
    protected final ReentrantLock lock = new ReentrantLock(false);
    protected final AtomicBoolean closed = new AtomicBoolean(false);
    protected final Condition bufferNotFull = this.lock.newCondition();
    protected final Condition bufferNotEmpty = this.lock.newCondition();
    
    
    /*
     *
     */
    public AbstractAsyncInputStream(Closeable resource) {
        this(resource, DEFAULT_CAPACITY);
    }
    
    public AbstractAsyncInputStream(Closeable resource, int size) {
        this.resource = resource;
        this.ringBuffer = new ByteRingBuffer(size);
    }
    
    /*
     *
     */
    @Override
    public int available() throws IOException {
        return this.ringBuffer.size();
    }
    
    @Override
    public void close() throws IOException {
        //
        if (!this.closed.compareAndSet(false, true)) return;
        
        //
        try {
            this.resource.close();
        } finally {
            this.lock.lock();
            try {
                this.bufferNotFull.signalAll();
                this.bufferNotEmpty.signalAll();
            } finally {
                this.lock.unlock();
            }
        }
    }
    
    @Override
    public int read() throws IOException {
        this.lock.lock();
        try {
            //
            while (this.ringBuffer.isEmpty()) {
                if (this.exception != null) throw this.exception;
                this.bufferNotEmpty.awaitUninterruptibly();
                if (this.closed.get()) throw new EOFException();
            }
            
            //
            final int r = this.ringBuffer.read();
            this.bufferNotFull.signal();
            return r;
        } finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.lock.lock();
        try {
            //
            while (this.ringBuffer.isEmpty()) {
                if (this.exception != null) throw this.exception;
                this.bufferNotEmpty.awaitUninterruptibly();
                if (this.closed.get()) throw new EOFException();
            }
            
            //
            final int r = this.ringBuffer.read(b, off, len);
            this.bufferNotFull.signal();
            return r;
        } finally {
            this.lock.unlock();
        }
    }
    
    public int write(int b) throws IOException {
        this.lock.lock();
        try {
            //
            while (this.ringBuffer.isFull()) {
                this.bufferNotFull.awaitUninterruptibly();
                if (this.closed.get()) throw new EOFException();
            }
            
            //
            final int w = this.ringBuffer.write(b);
            this.bufferNotEmpty.signal();
            return w;
        } finally {
            this.lock.unlock();
        }
    }
    
    public int write(byte[] b, int off, int len) throws IOException {
        this.lock.lock();
        try {
            //
            while (this.ringBuffer.isFull()) {
                this.bufferNotFull.awaitUninterruptibly();
                if (this.closed.get()) throw new EOFException();
            }
            
            //
            final int w = this.ringBuffer.write(b, off, len);
            this.bufferNotEmpty.signal();
            return w;
        } finally {
            this.lock.unlock();
        }
    }
    
    /*
     *
     */
    protected final class ByteRingBuffer {
        //
        private int size;
        private int head; // Write
        private int tail; // Read
        private final byte[] buffer;
        
        /*
         *
         */
        public ByteRingBuffer(int capacity) {
            this.buffer = new byte[capacity];
        }
        
        /*
         *
         */
        public int size() {
            return this.size;
        }
        
        public boolean isEmpty() {
            return this.size == 0;
        }
        
        public boolean isFull() {
            return this.size == this.buffer.length;
        }
        
        /*
         *
         */
        public int read() {
            //
            final int r = this.buffer[this.tail] & 0xFF;
            
            //
            this.tail = (this.tail + 1) % this.buffer.length;
            this.size -= 1;
            return r;
        }
        
        public int read(byte[] b, int off, int len) {
            //
            final int r = Math.min(this.size, len);
            if (this.head > this.tail) {
                System.arraycopy(this.buffer, this.tail, b, off, r);
            } else {
                final int r1 = Math.min(this.buffer.length - this.tail, r);
                System.arraycopy(this.buffer, this.tail, b, off, r1);
                if (r1 < r) System.arraycopy(this.buffer, 0, b, off + r1, r - r1);
            }
            
            //
            this.tail = (this.tail + r) % this.buffer.length;
            this.size -= r;
            return r;
        }
        
        public int write(int b) {
            //
            this.buffer[head] = (byte) b;
            
            //
            this.head = (this.head + 1) % this.buffer.length;
            this.size += 1;
            return 1;
        }
        
        public int write(byte[] b, int off, int len) {
            //
            final int w = Math.min(this.buffer.length - this.size, len);
            if (this.head < this.tail) {
                System.arraycopy(b, off, this.buffer, this.head, w);
            } else {
                final int w1 = Math.min(this.buffer.length - this.head, w);
                System.arraycopy(b, off, this.buffer, this.head, w1);
                if (w1 < w) System.arraycopy(b, off + w1, this.buffer, 0, w - w1);
            }
            
            //
            this.head = (this.head + w) % this.buffer.length;
            this.size += w;
            return w;
        }
    }
}
