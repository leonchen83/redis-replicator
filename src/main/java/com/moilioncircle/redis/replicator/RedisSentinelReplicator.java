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
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.ModuleKey;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;
import com.moilioncircle.redis.replicator.sentienl.DefaultSentinel;
import com.moilioncircle.redis.replicator.sentienl.Sentinel;
import com.moilioncircle.redis.replicator.sentienl.SentinelListener;
import com.moilioncircle.redis.replicator.util.HostAndPort;

/**
 * @author Leon Chen
 * @since 3.2.0
 */
public class RedisSentinelReplicator extends AbstractReplicator {

	protected static final Logger logger = LoggerFactory.getLogger(RedisSentinelReplicator.class);

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

	@Override
	public boolean addEventListener(EventListener listener) {
		if (replicator == null) {
			return super.addEventListener(listener);
		} else {
			return replicator.addEventListener(listener);
		}
	}

	@Override
	public boolean removeEventListener(EventListener listener) {
		if (replicator == null) {
			return super.removeEventListener(listener);
		} else {
			return replicator.removeEventListener(listener);
		}
	}

	@Override
	public boolean addRawByteListener(RawByteListener listener) {
		if (replicator == null) {
			return super.addRawByteListener(listener);
		} else {
			return replicator.addRawByteListener(listener);
		}
	}

	@Override
	public boolean removeRawByteListener(RawByteListener listener) {
		if (replicator == null) {
			return super.removeRawByteListener(listener);
		} else {
			return replicator.removeRawByteListener(listener);
		}
	}

	@Override
	public boolean addCloseListener(CloseListener listener) {
		if (replicator == null) {
			return super.addCloseListener(listener);
		} else {
			return replicator.addCloseListener(listener);
		}
	}

	@Override
	public boolean removeCloseListener(CloseListener listener) {
		if (replicator == null) {
			return super.removeCloseListener(listener);
		} else {
			return replicator.removeCloseListener(listener);
		}
	}

	@Override
	public boolean addExceptionListener(ExceptionListener listener) {
		if (replicator == null) {
			return super.addExceptionListener(listener);
		} else {
			return replicator.addExceptionListener(listener);
		}
	}

	@Override
	public boolean removeExceptionListener(ExceptionListener listener) {
		if (replicator == null) {
			return super.removeExceptionListener(listener);
		} else {
			return replicator.removeExceptionListener(listener);
		}
	}

	@Override
	public CommandParser<? extends Command> getCommandParser(CommandName command) {
		if (replicator == null) {
			return super.getCommandParser(command);
		} else {
			return replicator.getCommandParser(command);
		}
	}

	@Override
	public <T extends Command> void addCommandParser(CommandName command, CommandParser<T> parser) {
		if (replicator == null) {
			super.addCommandParser(command, parser);
		} else {
			replicator.addCommandParser(command, parser);
		}
	}

	@Override
	public CommandParser<? extends Command> removeCommandParser(CommandName command) {
		if (replicator == null) {
			return super.removeCommandParser(command);
		} else {
			return replicator.removeCommandParser(command);
		}
	}

	@Override
	public ModuleParser<? extends Module> getModuleParser(String moduleName, int moduleVersion) {
		if (replicator == null) {
			return super.getModuleParser(moduleName, moduleVersion);
		} else {
			return replicator.getModuleParser(moduleName, moduleVersion);
		}
	}

	@Override
	public <T extends Module> void addModuleParser(String moduleName, int moduleVersion, ModuleParser<T> parser) {
		if (replicator == null) {
			super.addModuleParser(moduleName, moduleVersion, parser);
		} else {
			replicator.addModuleParser(moduleName, moduleVersion, parser);
		}
	}

	@Override
	public ModuleParser<? extends Module> removeModuleParser(String moduleName, int moduleVersion) {
		if (replicator == null) {
			return super.removeModuleParser(moduleName, moduleVersion);
		} else {
			return replicator.removeModuleParser(moduleName, moduleVersion);
		}
	}

	@Override
	public Status getStatus() {
		if (replicator == null) {
			return Status.DISCONNECTED;
		} else {
			return replicator.getStatus();
		}
	}

	private class DefaultSentinelListener implements SentinelListener {

		@Override
		public void onClose(Sentinel sentinel) {
			if (replicator != null) Replicators.close(replicator);
		}

		@Override
		public void onSwitch(Sentinel sentinel, HostAndPort host) {
			logger.info("Sentinel switch master to [{}]", host);
			if (replicator != null) Replicators.close(replicator);
			Replicator next = new RedisSocketReplicator(host.getHost(), host.getPort(), getConfiguration());
			if (replicator == null) {
				// restore listeners
				for (CloseListener listener : closeListeners) next.addCloseListener(listener);
				for (EventListener listener : eventListeners) next.addEventListener(listener);
				for (RawByteListener listener : rawByteListeners) next.addRawByteListener(listener);
				for (ExceptionListener listener : exceptionListeners) next.addExceptionListener(listener);
				// restore module
				for (Map.Entry<ModuleKey, ModuleParser<? extends Module>> entry : modules.entrySet()) {
					final ModuleKey key = entry.getKey();
					next.addModuleParser(key.getModuleName(), key.getModuleVersion(), entry.getValue());
				}
				// restore command
				for (Map.Entry<CommandName, CommandParser<? extends Command>> entry : commands.entrySet()) {
					next.addCommandParser(entry.getKey(), entry.getValue());
				}
				// restore rdb visitor
				// no need to replace `replicator` in RdbVisitor
				next.setRdbVisitor(rdbVisitor);
			}
			replicator = next;
			Replicators.open(replicator);
		}
	}
}
