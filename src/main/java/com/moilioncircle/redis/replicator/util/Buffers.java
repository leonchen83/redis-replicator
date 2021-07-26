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

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @author Leon Chen
 * @since 3.5.6
 */
public class Buffers {
    
    public static ByteBuffer flip(ByteBuffer buffer) {
        Buffer temp = buffer;
        return (ByteBuffer) temp.flip();
    }
    
    public static ByteBuffer mark(ByteBuffer buffer) {
        Buffer temp = buffer;
        return (ByteBuffer) temp.mark();
    }
    
    public static ByteBuffer clear(ByteBuffer buffer) {
        Buffer temp = buffer;
        return (ByteBuffer) temp.clear();
    }
    
    public static ByteBuffer reset(ByteBuffer buffer) {
        Buffer temp = buffer;
        return (ByteBuffer) temp.reset();
    }
    
    public static ByteBuffer rewind(ByteBuffer buffer) {
        Buffer temp = buffer;
        return (ByteBuffer) temp.rewind();
    }
    
    public static ByteBuffer limit(ByteBuffer buffer, int limit) {
        Buffer temp = buffer;
        return (ByteBuffer) temp.limit(limit);
    }
    
    public static ByteBuffer position(ByteBuffer buffer, int position) {
        Buffer temp = buffer;
        return (ByteBuffer) temp.position(position);
    }
}
