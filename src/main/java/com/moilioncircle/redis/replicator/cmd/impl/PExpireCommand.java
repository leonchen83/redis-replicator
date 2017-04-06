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

package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * Created by leon on 2/2/17.
 */
public class PExpireCommand implements Command {
    private String key;
    private long ex;

    public PExpireCommand() {
    }

    public PExpireCommand(String key, long ex) {
        this.key = key;
        this.ex = ex;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getEx() {
        return ex;
    }

    public void setEx(long ex) {
        this.ex = ex;
    }

    @Override
    public String toString() {
        return "PExpireCommand{" +
                "key='" + key + '\'' +
                ", ex=" + ex +
                '}';
    }
}
