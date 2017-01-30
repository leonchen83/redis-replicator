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

package com.moilioncircle.redis.replicator.rdb.datatype;

import com.moilioncircle.redis.replicator.event.Event;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by leon on 8/13/16.
 */
@SuppressWarnings("unchecked")
public class KeyValuePair<T> implements Event {
    protected DB db;
    protected int valueRdbType;
    protected ExpiredType expiredType = ExpiredType.NONE;
    protected Long expiredValue;
    protected String key;
    protected T value;

    public int getValueRdbType() {
        return valueRdbType;
    }

    public void setValueRdbType(int valueRdbType) {
        this.valueRdbType = valueRdbType;
    }

    public ExpiredType getExpiredType() {
        return expiredType;
    }

    public void setExpiredType(ExpiredType expiredType) {
        this.expiredType = expiredType;
    }

    public Long getExpiredValue() {
        return expiredValue;
    }

    public void setExpiredValue(Long expiredValue) {
        this.expiredValue = expiredValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public DB getDb() {
        return db;
    }

    public void setDb(DB db) {
        this.db = db;
    }

    /**
     * @return expiredValue as Integer
     */
    public Integer getExpiredSeconds() {
        return expiredValue == null ? null : expiredValue.intValue();
    }

    /**
     * @return expiredValue as Long
     */
    public Long getExpiredMs() {
        return expiredValue;
    }

    /**
     * @return REDIS_RDB_TYPE_STRING
     */
    public String getValueAsString() {
        return (String) value;
    }

    /**
     * @return REDIS_RDB_TYPE_HASH, REDIS_RDB_TYPE_HASH_ZIPMAP, REDIS_RDB_TYPE_HASH_ZIPLIST
     */
    public Map<String, String> getValueAsHash() {
        return (Map<String, String>) value;
    }

    /**
     * @return REDIS_RDB_TYPE_SET, REDIS_RDB_TYPE_SET_INTSET
     */
    public Set<String> getValueAsSet() {
        return (Set<String>) value;
    }

    /**
     * @return REDIS_RDB_TYPE_ZSET, REDIS_RDB_TYPE_ZSET_ZIPLIST
     */
    public Set<ZSetEntry> getValueAsZSet() {
        return (Set<ZSetEntry>) value;
    }

    /**
     * @return REDIS_RDB_TYPE_LIST, REDIS_RDB_TYPE_LIST_ZIPLIST
     */
    public List<String> getValueAsStringList() {
        return (List<String>) value;
    }

    /**
     * @return REDIS_RDB_TYPE_LIST_QUICKLIST
     */
    public List<byte[]> getValueAsByteArrayList() {
        return (List<byte[]>) value;
    }

    @Override
    public String toString() {
        return "KeyValuePair{" +
                "db=" + db +
                ", valueRdbType=" + valueRdbType +
                ", expiredType=" + expiredType +
                ", expiredValue=" + expiredValue +
                ", key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
