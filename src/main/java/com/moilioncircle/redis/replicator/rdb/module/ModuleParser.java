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

package com.moilioncircle.redis.replicator.rdb.module;

import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;

import java.io.IOException;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public interface ModuleParser<T extends Module> {

    /**
     * @param in      input stream
     * @param version module version : 1 or 2 <p>
     *                {@link com.moilioncircle.redis.replicator.Constants#RDB_TYPE_MODULE} : 1 <p>
     *                {@link com.moilioncircle.redis.replicator.Constants#RDB_TYPE_MODULE_2} : 2
     * @return module object
     * @throws IOException IOException
     * @since 2.3.0
     */
    T parse(RedisInputStream in, int version) throws IOException;
}
