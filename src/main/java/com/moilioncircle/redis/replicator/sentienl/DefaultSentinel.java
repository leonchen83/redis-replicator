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

package com.moilioncircle.redis.replicator.sentienl;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.moilioncircle.redis.replicator.util.HostAndPort;

/**
 * @author Leon Chen
 * @since 3.2.0
 */
public class DefaultSentinel implements Sentinel {

	private List<SentinelListener> listeners = new CopyOnWriteArrayList<>();

	public DefaultSentinel(List<HostAndPort> hosts, String name) {

	}

	@Override
	public void open() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public boolean addSentinelListener(SentinelListener listener) {
		return this.listeners.add(listener);
	}

	@Override
	public boolean removeSentinelListener(SentinelListener listener) {
		return this.listeners.remove(listener);
	}

	protected void doCloseListener() {
		if (listeners.isEmpty()) return;
		for (SentinelListener listener : listeners) {
			listener.onClose(this);
		}
	}

	protected void doSwitchListener(HostAndPort host) {
		if (listeners.isEmpty()) return;
		for (SentinelListener listener : listeners) {
			listener.onSwitch(this, host);
		}
	}
}
