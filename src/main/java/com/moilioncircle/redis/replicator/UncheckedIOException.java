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

import java.io.IOException;
import java.util.Objects;

/**
 * @author Leon Chen
 * @since 2.3.0
 */
public class UncheckedIOException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UncheckedIOException(String message, IOException cause) {
		super(message, Objects.requireNonNull(cause));
	}

	public UncheckedIOException(IOException cause) {
		super(Objects.requireNonNull(cause));
	}

	@Override
	public IOException getCause() {
		return (IOException) super.getCause();
	}
}
