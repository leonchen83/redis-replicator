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

package com.moilioncircle.redis.replicator.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.currentTimeMillis;

/**
 * @author Leon Chen
 * @since 2.3.2
 */
public class RateLimitInputStream extends InputStream implements Runnable {

    private static final Log logger = LogFactory.getLog(RateLimitInputStream.class);

    private static final int DEFAULT_PERMITS = 2 * 1024 * 1024;

    private final int permits;
    private final Thread worker;
    private final InputStream in;
    private final RateLimiter limiter;
    private final ThreadFactory factory;
    private volatile IOException exception;
    private final ReentrantLock lock = new ReentrantLock(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final Condition reader = this.lock.newCondition();
    private final Condition writer = this.lock.newCondition();

    public RateLimitInputStream(InputStream in) {
        this(in, DEFAULT_PERMITS);
    }

    public RateLimitInputStream(InputStream in, int permits) {
        this(in, permits, Executors.defaultThreadFactory());
    }

    public RateLimitInputStream(InputStream in, int permits, ThreadFactory factory) {
        this.in = in;
        this.factory = factory;
        this.permits = permits;
        this.limiter = new TokenBucketRateLimiter(permits);
        this.worker = this.factory.newThread(this); this.worker.start();
    }

    @Override
    public int read() throws IOException {
        lock.lock();
        try {
            while (!limiter.acquire(1)) {
                if (this.exception != null) throw this.exception;
                this.reader.awaitUninterruptibly();
                if (this.closed.get()) throw new EOFException();
            }
            int r = in.read();
            this.writer.signalAll();
            return r;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        lock.lock();
        try {
            int total = length, index = offset;
            while (total > 0) {
                int len = Math.min(permits, total);
                while (!limiter.acquire(len)) {
                    if (this.exception != null) throw this.exception;
                    this.reader.awaitUninterruptibly();
                    if (this.closed.get()) throw new EOFException();
                }
                if (in.read(b, index, len) == -1) return -1;
                index += len;
                total -= len;
                this.writer.signalAll();
            }
            assert total == 0;
            return total;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long skip(long len) throws IOException {
        lock.lock();
        try {
            long total = len;
            while (total > 0) {
                int skip = (int) Math.min(permits, total);
                while (!limiter.acquire(skip)) {
                    if (this.exception != null) throw this.exception;
                    this.reader.awaitUninterruptibly();
                    if (this.closed.get()) throw new EOFException();
                }
                in.skip(skip);
                total -= skip;
                this.writer.signalAll();
            }
            assert total == 0;
            return len;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        if (!this.closed.compareAndSet(false, true)) return;
        try {
            this.in.close();
        } finally {
            this.lock.lock();
            try {
                this.reader.signalAll();
                this.writer.signalAll();
            } finally {
                this.lock.unlock();
            }
        }
    }

    @Override
    public void run() {
        try {
            int yield = 40, idx = 0;
            while (!this.closed.get()) {
                this.lock.lock();
                try {
                    while (this.limiter.full()) {
                        this.writer.awaitUninterruptibly();
                        if (this.closed.get()) throw new EOFException();
                    }
                    boolean signal = false;
                    while (!this.limiter.update()) {
                        if (!signal) signal = true;
                        if (idx++ == yield) {
                            idx = 0;
                            Thread.yield();
                        }
                    }
                    if (signal) this.reader.signalAll();
                } finally {
                    this.lock.unlock();
                }
            }
        } catch (IOException e) {
            this.exception = e;
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

    private interface RateLimiter {

        /**
         * @return return whether bucket is full
         * @see {@link RateLimitInputStream#run()}
         */
        boolean full();

        /**
         * @return release() >= gap
         * @see {@link RateLimitInputStream#run()}
         */
        boolean update();

        /**
         * @param permits the permits that acquire
         * @return if has enough permits return true else return false
         * @see {@link RateLimitInputStream#read()}
         * @see {@link RateLimitInputStream#read(byte[])}
         * @see {@link RateLimitInputStream#read(byte[], int, int)}
         * @see {@link RateLimitInputStream#skip(long)}
         */
        boolean acquire(int permits);
    }

    private class TokenBucketRateLimiter implements RateLimiter {

        private double gap;
        private long access;
        private double permits;
        private final int size;

        private TokenBucketRateLimiter(int permits) {
            this.size = permits;
            this.permits = permits;
            this.access = currentTimeMillis();
        }

        @Override
        public boolean full() {
            return (int)permits == size;
        }

        @Override
        public boolean update() {
            double p = release();
            if (p < this.gap) {
                this.gap -= p;
                return false;
            } else {
                this.gap = 0;
                return true;
            }
        }

        @Override
        public boolean acquire(int permits) {
            release();
            if (this.permits < permits) {
                this.gap = permits - this.permits;
                return false;
            } else {
                this.permits -= permits;
                this.gap = 0;
                return true;
            }
        }

        private double release() {
            long access = currentTimeMillis();
            if (access <= this.access) return 0;
            double p = (access - this.access) * size / 1000d;
            this.permits += p; if (this.permits > size) this.permits = size;
            this.access = access; return p;
        }
    }
}
