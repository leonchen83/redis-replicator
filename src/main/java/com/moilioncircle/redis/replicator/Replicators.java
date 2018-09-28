package com.moilioncircle.redis.replicator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Leon Chen
 * @since 3.0.0
 */
public class Replicators {

    /*
     * SYNC
     */
    public static void open(Replicator replicator) {
        try {
            replicator.open();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void close(Replicator replicator) {
        try {
            replicator.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void openQuietly(Replicator replicator) {
        try {
            open(replicator);
        } catch (Throwable e) {
        }
    }

    public static void closeQuietly(Replicator replicator) {
        try {
            open(replicator);
        } catch (Throwable e) {
        }
    }

    /*
     * ASYNC
     */
    public static CompletableFuture<Void> open(AsyncReplicator replicator) {
        return open(replicator, null);
    }

    public static CompletableFuture<Void> close(AsyncReplicator replicator) {
        return close(replicator, null);
    }

    public static CompletableFuture<Void> open(AsyncReplicator replicator, Executor executor) {
        return replicator.open(executor);
    }

    public static CompletableFuture<Void> close(AsyncReplicator replicator, Executor executor) {
        return replicator.close(executor);
    }
}
