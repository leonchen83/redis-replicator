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

package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.CommandSpec;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@CommandSpec(command = "FLUSHDB")
public class FlushDBCommand extends AbstractCommand {

    private static final long serialVersionUID = 1L;
    
    private boolean async;
    private boolean sync;

    public FlushDBCommand() {
    }
    
    public FlushDBCommand(boolean async) {
        this(async, false);
    }
    
    /**
     * @since 3.5.2
     * @param async async
     * @param sync sync
     */
    public FlushDBCommand(boolean async, boolean sync) {
        this.async = async;
        this.sync = sync;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }
    
    /**
     * @since 3.5.2
     * @return sync
     */
    public boolean isSync() {
        return sync;
    }
    
    /**
     * @since 3.5.2
     * @param sync sync
     */
    public void setSync(boolean sync) {
        this.sync = sync;
    }
}
