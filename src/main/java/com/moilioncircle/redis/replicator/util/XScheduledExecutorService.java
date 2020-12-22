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

package com.moilioncircle.redis.replicator.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.moilioncircle.redis.replicator.Configuration;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class XScheduledExecutorService extends AbstractExecutorService implements ScheduledExecutorService {
    
    private final Configuration configuration;
    private final ScheduledExecutorService executor;
    
    public XScheduledExecutorService(Configuration configuration) {
        this.configuration = configuration;
        ScheduledExecutorService executor = configuration.getScheduledExecutor();
        if (executor != null) {
            this.executor = executor;
        } else {
            this.executor = Executors.newSingleThreadScheduledExecutor();
        }
    }
    
    public long terminateQuietly(long timeout, TimeUnit unit) {
        ScheduledExecutorService executor = configuration.getScheduledExecutor();
        if (executor != null) {
            return 0L;
        } else {
            return Concurrents.terminateQuietly(this.executor, timeout, unit);
        }
    }
    
    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return executor.schedule(command, delay, unit);
    }
    
    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return executor.schedule(callable, delay, unit);
    }
    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return executor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }
    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
    
    @Override
    public void shutdown() {
        executor.shutdown();
    }
    
    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }
    
    @Override
    public boolean isTerminated() {
        return executor.isTerminated();
    }
    
    @Override
    public List<Runnable> shutdownNow() {
        return executor.shutdownNow();
    }
    
    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }
    
    @Override
    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }
    
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }
    
    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return executor.submit(task, result);
    }
    
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return executor.invokeAll(tasks);
    }
    
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return executor.invokeAny(tasks);
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return executor.invokeAll(tasks, timeout, unit);
    }
    
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return executor.invokeAny(tasks, timeout, unit);
    }
}
