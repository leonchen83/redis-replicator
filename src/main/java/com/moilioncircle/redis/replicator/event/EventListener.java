package com.moilioncircle.redis.replicator.event;

import com.moilioncircle.redis.replicator.Replicator;

/**
 * @author Leon Chen
 */
public interface EventListener {
    void onEvent(Replicator replicator, Event event);
}
