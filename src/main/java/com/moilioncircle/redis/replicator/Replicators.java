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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
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
            Objects.requireNonNull(replicator).open();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void close(Replicator replicator) {
        try {
            Objects.requireNonNull(replicator).close();
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
            close(replicator);
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
        return Objects.requireNonNull(replicator).open(executor);
    }

    public static CompletableFuture<Void> close(AsyncReplicator replicator, Executor executor) {
        return Objects.requireNonNull(replicator).close(executor);
    }
}
