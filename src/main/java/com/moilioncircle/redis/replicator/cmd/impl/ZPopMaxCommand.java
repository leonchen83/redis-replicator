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

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class ZPopMaxCommand  implements Command {
	
	private static final long serialVersionUID = 1L;
	
	private String key;
	private Integer count;
	private byte[] rawKey;
	
	public ZPopMaxCommand() {
	}
	
	public ZPopMaxCommand(String key, Integer count) {
		this(key, count, null);
	}
	
	public ZPopMaxCommand(String key, Integer count, byte[] rawKey) {
		this.key = key;
		this.count = count;
		this.rawKey = rawKey;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}
	
	public byte[] getRawKey() {
		return rawKey;
	}
	
	public void setRawKey(byte[] rawKey) {
		this.rawKey = rawKey;
	}
	
	@Override
	public String toString() {
		return "ZPopMaxCommand{" +
				"key='" + key + '\'' +
				", count=" + count +
				'}';
	}
}