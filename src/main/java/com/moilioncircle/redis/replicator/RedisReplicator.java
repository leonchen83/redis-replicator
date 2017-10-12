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

package com.moilioncircle.redis.replicator;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.io.PeekableInputStream;
import com.moilioncircle.redis.replicator.io.RawByteListener;
import com.moilioncircle.redis.replicator.rdb.AuxFieldListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.RdbVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
public class RedisReplicator implements Replicator {
	protected Replicator replicator;

	public RedisReplicator(File file, FileType fileType, Configuration configuration) throws FileNotFoundException {
		switch (fileType) {
		case AOF:
			this.replicator = new RedisAofReplicator(file, configuration);
			break;
		case RDB:
			this.replicator = new RedisRdbReplicator(file, configuration);
			break;
		case MIXED:
			this.replicator = new RedisMixReplicator(file, configuration);
			break;
		default:
			throw new UnsupportedOperationException(fileType.toString());
		}
	}

	public RedisReplicator(InputStream in, FileType fileType, Configuration configuration) {
		switch (fileType) {
		case AOF:
			this.replicator = new RedisAofReplicator(in, configuration);
			break;
		case RDB:
			this.replicator = new RedisRdbReplicator(in, configuration);
			break;
		case MIXED:
			this.replicator = new RedisMixReplicator(in, configuration);
			break;
		default:
			throw new UnsupportedOperationException(fileType.toString());
		}
	}

	public RedisReplicator(String host, int port, Configuration configuration) {
		this.replicator = new RedisSocketReplicator(host, port, configuration);
	}

	/**
	 * @param uri redis uri.
	 * @throws URISyntaxException uri syntax error.
	 * @throws IOException        read timeout or read EOF.
	 * @see RedisURI
	 * @since 2.4.0
	 */
	public RedisReplicator(String uri) throws URISyntaxException, IOException {
		Objects.requireNonNull(uri);
		initialize(new RedisURI(uri));
	}

	/**
	 * @param uri redis uri.
	 * @throws IOException read timeout or read EOF.
	 * @since 2.4.2
	 */
	public RedisReplicator(RedisURI uri) throws IOException {
		initialize(uri);
	}

	private void initialize(RedisURI uri) throws IOException {
		Objects.requireNonNull(uri);
		Configuration configuration = Configuration.valueOf(uri);
		if (uri.getFileType() != null) {
			PeekableInputStream in = new PeekableInputStream(uri.toURL()
				.openStream());
			switch (uri.getFileType()) {
			case AOF:
				if (in.peek() == 'R') {
					this.replicator = new RedisMixReplicator(in, configuration);
				} else {
					this.replicator = new RedisAofReplicator(in, configuration);
				}
				break;
			case RDB:
				this.replicator = new RedisRdbReplicator(in, configuration);
				break;
			case MIXED:
				this.replicator = new RedisMixReplicator(in, configuration);
				break;
			default:
				throw new UnsupportedOperationException(uri.getFileType()
					.toString());
			}
		} else {
			this.replicator = new RedisSocketReplicator(uri.getHost(), uri.getPort(), configuration);
		}
	}

	@Override
	public boolean addRdbListener(RdbListener listener) {
		return replicator.addRdbListener(listener);
	}

	@Override
	public boolean removeRdbListener(RdbListener listener) {
		return replicator.removeRdbListener(listener);
	}

	@Override
	public boolean addAuxFieldListener(AuxFieldListener listener) {
		return replicator.addAuxFieldListener(listener);
	}

	@Override
	public boolean removeAuxFieldListener(AuxFieldListener listener) {
		return replicator.removeAuxFieldListener(listener);
	}

	@Override
	public boolean addRawByteListener(RawByteListener listener) {
		return replicator.addRawByteListener(listener);
	}

	@Override
	public boolean removeRawByteListener(RawByteListener listener) {
		return replicator.removeRawByteListener(listener);
	}

	@Override
	public void builtInCommandParserRegister() {
		replicator.builtInCommandParserRegister();
	}

	@Override
	public CommandParser<? extends Command> getCommandParser(CommandName command) {
		return replicator.getCommandParser(command);
	}

	@Override
	public <T extends Command> void addCommandParser(CommandName command, CommandParser<T> parser) {
		replicator.addCommandParser(command, parser);
	}

	@Override
	public CommandParser<? extends Command> removeCommandParser(CommandName command) {
		return replicator.removeCommandParser(command);
	}

	@Override
	public ModuleParser<? extends Module> getModuleParser(String moduleName, int moduleVersion) {
		return replicator.getModuleParser(moduleName, moduleVersion);
	}

	@Override
	public <T extends Module> void addModuleParser(String moduleName, int moduleVersion, ModuleParser<T> parser) {
		replicator.addModuleParser(moduleName, moduleVersion, parser);
	}

	@Override
	public ModuleParser<? extends Module> removeModuleParser(String moduleName, int moduleVersion) {
		return replicator.removeModuleParser(moduleName, moduleVersion);
	}

	@Override
	public void setRdbVisitor(RdbVisitor rdbVisitor) {
		replicator.setRdbVisitor(rdbVisitor);
	}

	@Override
	public RdbVisitor getRdbVisitor() {
		return replicator.getRdbVisitor();
	}

	@Override
	public boolean addCommandListener(CommandListener listener) {
		return replicator.addCommandListener(listener);
	}

	@Override
	public boolean removeCommandListener(CommandListener listener) {
		return replicator.removeCommandListener(listener);
	}

	@Override
	public boolean addCloseListener(CloseListener listener) {
		return replicator.addCloseListener(listener);
	}

	@Override
	public boolean removeCloseListener(CloseListener listener) {
		return replicator.removeCloseListener(listener);
	}

	@Override
	public boolean addExceptionListener(ExceptionListener listener) {
		return replicator.addExceptionListener(listener);
	}

	@Override
	public boolean removeExceptionListener(ExceptionListener listener) {
		return replicator.removeExceptionListener(listener);
	}

	@Override
	public boolean verbose() {
		return replicator.verbose();
	}

	@Override
	public Status getStatus() {
		return replicator.getStatus();
	}

	@Override
	public Configuration getConfiguration() {
		return replicator.getConfiguration();
	}

	@Override
	public void open() throws IOException {
		replicator.open();
	}

	@Override
	public void close() throws IOException {
		replicator.close();
	}

	/**
	 * @param rawBytes input stream raw bytes
	 * @since 2.2.0
	 * @deprecated notice that this method will remove in version 3.0.0
	 */
	@Deprecated
	public void handle(byte... rawBytes) {
		if (!(replicator instanceof AbstractReplicatorListener))
			return;
		((AbstractReplicatorListener) replicator).doRawByteListener(rawBytes);
	}
}
