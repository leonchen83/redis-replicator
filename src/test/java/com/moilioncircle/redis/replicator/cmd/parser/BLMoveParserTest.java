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

package com.moilioncircle.redis.replicator.cmd.parser;

import org.junit.Test;

import com.moilioncircle.redis.replicator.cmd.impl.BLMoveCommand;
import com.moilioncircle.redis.replicator.cmd.impl.DirectionType;

import junit.framework.TestCase;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class BLMoveParserTest extends AbstractParserTest {
	@Test
	public void parse() {
		{
			BLMoveParser parser = new BLMoveParser();
			BLMoveCommand cmd = parser.parse(toObjectArray("BLMOVE aaa bbb LEFT RIGHT".split(" ")));
			assertEquals("aaa", cmd.getSource());
			assertEquals("bbb", cmd.getDestination());
			TestCase.assertEquals(DirectionType.LEFT, cmd.getFrom());
			TestCase.assertEquals(DirectionType.RIGHT, cmd.getTo());
		}
		
		{
			BLMoveParser parser = new BLMoveParser();
			BLMoveCommand cmd = parser.parse(toObjectArray("BLMOVE aaa bbb RIGHT LEFT".split(" ")));
			assertEquals("aaa", cmd.getSource());
			assertEquals("bbb", cmd.getDestination());
			TestCase.assertEquals(DirectionType.RIGHT, cmd.getFrom());
			TestCase.assertEquals(DirectionType.LEFT, cmd.getTo());
		}
	}
}