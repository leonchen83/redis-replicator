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

package com.moilioncircle.examples.extension;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;

import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.AbstractCommand;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@SuppressWarnings("resource")
public class CommandExtensionExample {

    public static void main(String[] args) throws Exception {
        final Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");

        replicator.addCommandParser(CommandName.name("APPEND"), new YourAppendParser());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof YourAppendParser.YourAppendCommand) {
                    YourAppendParser.YourAppendCommand yourAppendCommand = (YourAppendParser.YourAppendCommand) event;
                    System.out.println(yourAppendCommand.key);
                    System.out.println(yourAppendCommand.value);
                }
            }
        });
        replicator.open();
    }

    public static class YourAppendParser implements CommandParser<YourAppendParser.YourAppendCommand> {

        @Override
        public YourAppendCommand parse(Object[] command) {
            return new YourAppendCommand(toRune(command[1]), toRune(command[2]));
        }

        public static class YourAppendCommand extends AbstractCommand {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            public final String key;
            public final String value;

            public YourAppendCommand(String key, String value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public String toString() {
                return "YourAppendCommand{" +
                        "key='" + key + '\'' +
                        ", value='" + value + '\'' +
                        '}';
            }
        }
    }
}
