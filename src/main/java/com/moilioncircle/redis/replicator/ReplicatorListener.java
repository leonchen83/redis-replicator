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

import com.moilioncircle.redis.replicator.cmd.CommandFilter;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.AuxFieldListener;
import com.moilioncircle.redis.replicator.rdb.RdbFilter;
import com.moilioncircle.redis.replicator.rdb.RdbListener;

/**
 * Created by leon on 2017/1/31.
 */
public interface ReplicatorListener {
    /*
     * Rdb
     */
    boolean addRdbFilter(RdbFilter filter);

    boolean removeRdbFilter(RdbFilter filter);

    boolean addRdbListener(RdbListener listener);

    boolean removeRdbListener(RdbListener listener);

    boolean addAuxFieldListener(AuxFieldListener listener);

    boolean removeAuxFieldListener(AuxFieldListener listener);

    boolean addRdbRawByteListener(RawByteListener listener);

    boolean removeRdbRawByteListener(RawByteListener listener);

    /*
     * Command
     */
    boolean addCommandFilter(CommandFilter filter);

    boolean removeCommandFilter(CommandFilter filter);

    boolean addCommandListener(CommandListener listener);

    boolean removeCommandListener(CommandListener listener);

    /*
     * Close
     */
    boolean addCloseListener(CloseListener listener);

    boolean removeCloseListener(CloseListener listener);

    /*
     * Exception
     */
    boolean addExceptionListener(ExceptionListener listener);

    boolean removeExceptionListener(ExceptionListener listener);
}
