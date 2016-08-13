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

import com.moilioncircle.redis.replicator.cmd.*;
import com.moilioncircle.redis.replicator.rdb.RdbFilter;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by leon on 8/13/16.
 */
public interface Replicator extends Closeable {

    void addRdbFilter(RdbFilter filter);

    void removeRdbFilter(RdbFilter filter);

    void addRdbListener(RdbListener listener);

    void removeRdbListener(RdbListener listener);

    void buildInCommandParserRegister();

    <T extends Command> void addCommandParser(CommandName command, CommandParser<T> parser);

    <T extends Command> void removeCommandParser(CommandName command, CommandParser<T> parser);

    void addCommandFilter(CommandFilter filter);

    void removeCommandFilter(CommandFilter filter);

    void addCommandListener(CommandListener listener);

    void removeCommandListener(CommandListener listener);

    void doCommandHandler(Command command);

    boolean doCommandFilter(Command command);

    void doRdbHandler(KeyValuePair<?> kv);

    boolean doRdbFilter(KeyValuePair<?> kv);

    void open() throws IOException;
}
