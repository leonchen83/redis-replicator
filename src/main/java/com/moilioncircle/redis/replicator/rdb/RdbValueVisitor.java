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

package com.moilioncircle.redis.replicator.rdb;

import java.io.IOException;

import com.moilioncircle.redis.replicator.io.RedisInputStream;

/**
 * @author Leon Chen
 * @since 3.1.0
 */
public abstract class RdbValueVisitor {
    
    public <T> T applyFunction(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
    
    public <T> T applyFunction2(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyString(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyList(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applySet(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
    
    public <T> T applySetListPack(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyZSet(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyZSet2(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyHash(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyHashZipMap(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyListZipList(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applySetIntSet(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyZSetZipList(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
    
    public <T> T applyZSetListPack(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyHashZipList(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
    
    public <T> T applyHashListPack(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyListQuickList(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
    
    public <T> T applyListQuickList2(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyModule(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyModule2(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    public <T> T applyStreamListPacks(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
    
    public <T> T applyStreamListPacks2(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
    
    public <T> T applyStreamListPacks3(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
    
    public <T> T applyHashMetadata(RedisInputStream in, int version) throws IOException{
        throw new UnsupportedOperationException("must implement this method.");
    }
    
    public <T> T applyHashListPackEx(RedisInputStream in, int version) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
}
