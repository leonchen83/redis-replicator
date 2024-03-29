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

package com.moilioncircle.redis.replicator.event;

import java.io.Serializable;

import com.moilioncircle.redis.replicator.util.type.Tuple2;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public interface Event extends Serializable {

    interface Context extends Serializable {

        Tuple2<Long, Long> getOffsets();
        
        void setOffsets(Tuple2<Long, Long> offset);

        /**
         * @since 3.6.5
         * @param key key
         * @return cookie value
         */
        default Object getCookie(Object key) {
            throw new UnsupportedOperationException();
        }

        /**
         * @since 3.6.5
         * @param key cookie key 
         * @param value cookie value
         * @return previous cookie value
         */
        default Object setCookie(Object key, Object value) {
            throw new UnsupportedOperationException();
        }
    }
}
