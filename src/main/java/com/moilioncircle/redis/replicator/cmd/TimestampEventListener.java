/*
 * Copyright 2016-2017 Leon Chen
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

package com.moilioncircle.redis.replicator.cmd;

import java.util.function.Predicate;

import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;

/**
 * @author Leon Chen
 * @since 3.6.0
 */
public class TimestampEventListener implements EventListener {
	
	private boolean guard;
	private final EventListener listener;
	private final Predicate<Long> condition;
	
	public TimestampEventListener(Predicate<Long> condition, EventListener listener) {
		this.condition = condition;
		this.listener = listener;
	}
	
	@Override
	public void onEvent(Replicator replicator, Event event) {
		if (event instanceof TimestampEvent) {
			TimestampEvent te = (TimestampEvent) event;
			guard = condition.test(te.getTimestamp());
		} else {
			if (guard) listener.onEvent(replicator, event);
		}
	}
}
