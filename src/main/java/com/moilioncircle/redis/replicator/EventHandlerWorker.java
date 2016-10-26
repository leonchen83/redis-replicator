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

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.PostFullSyncEvent;
import com.moilioncircle.redis.replicator.event.PreFullSyncEvent;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by leon on 8/25/16.
 */
/*package*/ class EventHandlerWorker extends Thread implements Closeable {
    private static final Log logger = LogFactory.getLog(EventHandlerWorker.class);

    private final AbstractReplicator replicator;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    public EventHandlerWorker(AbstractReplicator replicator) {
        this.replicator = replicator;
        setDaemon(true);
        setName("event-handler-worker");
    }

    @Override
    public void run() {
        while (!isClosed.get() || replicator.eventQueue.size() > 0) {
            try {
                Object object = replicator.eventQueue.take();
                if (object instanceof KeyValuePair<?>) {
                    KeyValuePair<?> kv = (KeyValuePair<?>) object;
                    if (!replicator.doRdbFilter(kv)) continue;
                    replicator.doRdbHandler(kv);
                } else if (object instanceof Command) {
                    Command command = (Command) object;
                    if (!replicator.doCommandFilter(command)) continue;
                    replicator.doCommandHandler(command);
                } else if (object instanceof PreFullSyncEvent) {
                    replicator.doPreFullSync();
                } else if (object instanceof PostFullSyncEvent) {
                    replicator.doPostFullSync(((PostFullSyncEvent) object).getChecksum());
                } else {
                    throw new AssertionError(object);
                }
            } catch (InterruptedException e) {
                close();
            } catch (Throwable e) {
                exceptionHandler(e);
            }
        }
    }

    @Override
    public void close() {
        isClosed.compareAndSet(false, true);
    }

    public boolean isClosed() {
        return isClosed.get();
    }

    protected void exceptionHandler(Throwable e) {
        logger.error("error", e);
    }
}
