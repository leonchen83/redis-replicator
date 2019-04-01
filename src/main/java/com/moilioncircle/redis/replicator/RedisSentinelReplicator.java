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

package com.moilioncircle.redis.replicator;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.moilioncircle.redis.replicator.sentienl.DefaultSentinel;
import com.moilioncircle.redis.replicator.sentienl.Sentinel;
import com.moilioncircle.redis.replicator.sentienl.SentinelListener;
import com.moilioncircle.redis.replicator.util.HostAndPort;

/**
 * @author Leon Chen
 * @since 3.2.0
 */
public class RedisSentinelReplicator extends AbstractReplicator {

	private final Sentinel sentinel;
	private volatile Replicator replicator;

	public RedisSentinelReplicator(List<HostAndPort> hosts, String name, Configuration configuration) {
		Objects.requireNonNull(hosts);
		Objects.requireNonNull(configuration);
		this.configuration = configuration;
		this.sentinel = new DefaultSentinel(hosts, name);
		this.sentinel.addSentinelListener(new DefaultSentinelListener());
	}

	@Override
	public void open() throws IOException {
		this.sentinel.open();
	}

	@Override
	public void close() throws IOException {
		this.sentinel.close();
	}

	private class DefaultSentinelListener implements SentinelListener {

		@Override
		public void onClose(Sentinel sentinel) {
			if (replicator != null) Replicators.close(replicator);
		}

		@Override
		public void onSwitch(Sentinel sentinel, HostAndPort host) {
			if (replicator != null) Replicators.close(replicator);
			replicator = new RedisReplicator(host.getHost(), host.getPort(), getConfiguration());
			
			Replicators.open(replicator);
		}
	}
}
