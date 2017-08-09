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
    private final ReentrantLock lock = new ReentrantLock(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final Condition limiterLock = this.lock.newCondition();

    public RateLimitInputStream(InputStream in) {
        this(in, DEFAULT_PERMITS);
    }

    public RateLimitInputStream(InputStream in, int permits) {
        this(in, permits, Executors.defaultThreadFactory());
    }

    public RateLimitInputStream(InputStream in, int permits, ThreadFactory factory) {
        this.in = in;
        this.factory = factory; this.permits = permits;
        this.limiter = new TokenBucketRateLimiter(permits);
        this.worker = this.factory.newThread(this); this.worker.start();
    }

    @Override
    public int read() throws IOException {
        lock.lock();
        try {
            while (!limiter.acquire(1)) {
                this.limiterLock.awaitUninterruptibly();
                if (this.closed.get()) throw new EOFException();
            }
            return in.read();
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
                    this.limiterLock.awaitUninterruptibly();
                    if (this.closed.get()) throw new EOFException();
                }
                if (in.read(b, index, len) == -1) return -1;
                index += len; total -= len;
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
                    this.limiterLock.awaitUninterruptibly();
                    if (this.closed.get()) throw new EOFException();
                }
                in.skip(skip);
                total -= skip;
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
                this.limiterLock.signalAll();
            } finally {
                this.lock.unlock();
            }
        }
    }

    @Override
    public void run() {
        try {
            int yield = 70, idx = 0;
            while (!this.closed.get()) {
                this.lock.lock();
                try {
                    this.limiter.update();
                } finally {
                    this.lock.unlock();
                }
                if (idx++ == yield) {
                    Thread.yield();
                    idx = 0;
                }
            }
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
        void update();

        boolean acquire(int permits);
    }

    private class TokenBucketRateLimiter implements RateLimiter {

        private long access;
        private int permits;
        private final int size;

        private TokenBucketRateLimiter(int permits) {
            this.access = currentTimeMillis();
            this.size = this.permits = permits;
        }

        /**
         * @see {@link RateLimitInputStream#run()}
         */
        @Override
        public void update() {
            long access = currentTimeMillis();
            if (access - this.access == 0) return;
            long p = (access - this.access) * size / 1000;
            if (p == 0) return; this.permits = Math.min(permits + (int) p, size);
            this.access = access; RateLimitInputStream.this.limiterLock.signalAll();
        }

        /**
         * @param permits the permits that acquire
         * @return if has enough permits return true else return false
         * @see {@link RateLimitInputStream#read()}
         * @see {@link RateLimitInputStream#read(byte[])}
         * @see {@link RateLimitInputStream#read(byte[], int, int)}
         * @see {@link RateLimitInputStream#skip(long)}
         */
        @Override
        public boolean acquire(int permits) {
            if (this.permits < permits) return false;
            else { this.permits -= permits; return true; }
        }
    }
}
