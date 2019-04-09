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

package com.moilioncircle.redis.replicator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.io.RawByteListener;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class AbstractReplicatorListener implements ReplicatorListener {
    protected final List<CloseListener> closeListeners = new CopyOnWriteArrayList<>();
    protected final List<EventListener> eventListeners = new CopyOnWriteArrayList<>();
    protected final List<StatusListener> statusListeners = new CopyOnWriteArrayList<>();
    protected final List<RawByteListener> rawByteListeners = new CopyOnWriteArrayList<>();
    protected final List<ExceptionListener> exceptionListeners = new CopyOnWriteArrayList<>();

    @Override
    public boolean addEventListener(EventListener listener) {
        return eventListeners.add(listener);
    }

    @Override
    public boolean removeEventListener(EventListener listener) {
        return eventListeners.remove(listener);
    }

    @Override
    public boolean addRawByteListener(RawByteListener listener) {
        return this.rawByteListeners.add(listener);
    }

    @Override
    public boolean removeRawByteListener(RawByteListener listener) {
        return this.rawByteListeners.remove(listener);
    }

    @Override
    public boolean addCloseListener(CloseListener listener) {
        return closeListeners.add(listener);
    }

    @Override
    public boolean removeCloseListener(CloseListener listener) {
        return closeListeners.remove(listener);
    }

    @Override
    public boolean addExceptionListener(ExceptionListener listener) {
        return exceptionListeners.add(listener);
    }

    @Override
    public boolean removeExceptionListener(ExceptionListener listener) {
        return exceptionListeners.remove(listener);
    }

    @Override
    public boolean addStatusListener(StatusListener listener) {
        return statusListeners.add(listener);
    }

    @Override
    public boolean removeStatusListener(StatusListener listener) {
        return statusListeners.remove(listener);
    }

    protected void doEventListener(Replicator replicator, Event event) {
        if (eventListeners.isEmpty()) return;
        for (EventListener listener : eventListeners) {
            listener.onEvent(replicator, event);
        }
    }

    protected void doCloseListener(Replicator replicator) {
        if (closeListeners.isEmpty()) return;
        for (CloseListener listener : closeListeners) {
            listener.handle(replicator);
        }
    }

    protected void doExceptionListener(Replicator replicator, Throwable throwable, Event event) {
        if (exceptionListeners.isEmpty()) return;
        for (ExceptionListener listener : exceptionListeners) {
            listener.handle(replicator, throwable, event);
        }
    }

    protected void doStatusListener(Replicator replicator, Status status) {
        if (statusListeners.isEmpty()) return;
        for (StatusListener listener : statusListeners) {
            listener.handle(replicator, status);
        }
    }
}
