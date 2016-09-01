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

/**
 * Created by leon on 8/20/16.
 */
public class Db {
    public int dbNumber;
    /* rdb version 7 */
    public Integer dbsize = null;
    /* rdb version 7 */
    public Integer expires = null;

    public Db(int dbNumber) {
        this.dbNumber = dbNumber;
    }

    public Db(int dbNumber, int dbsize, int expires) {
        this.dbNumber = dbNumber;
        this.dbsize = dbsize;
        this.expires = expires;
    }

    public int getDbNumber() {
        return dbNumber;
    }

    public void setDbNumber(int dbNumber) {
        this.dbNumber = dbNumber;
    }

    public Integer getDbsize() {
        return dbsize;
    }

    public void setDbsize(Integer dbsize) {
        this.dbsize = dbsize;
    }

    public Integer getExpires() {
        return expires;
    }

    public void setExpires(Integer expires) {
        this.expires = expires;
    }

    @Override
    public String toString() {
        return "Db{" +
                "dbNumber=" + dbNumber +
                ", dbsize=" + dbsize +
                ", expires=" + expires +
                '}';
    }
}
