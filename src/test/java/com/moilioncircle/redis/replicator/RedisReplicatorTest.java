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
import com.moilioncircle.redis.replicator.cmd.CommandFilter;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.impl.SetParser;
import junit.framework.TestCase;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;

/**
 * Created by leon on 8/13/16.
 */
public class RedisReplicatorTest extends TestCase {


    @Test
    public void testSync() throws Exception {
        //socket
        final RedisReplicator replicator = new RedisReplicator("127.0.0.1",
                6379,
                Configuration.defaultSetting()
                        .setRetries(0)
                        .setVerbose(true));
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(5000);
                    Jedis jedis = new Jedis("127.0.0.1", 6379);
                    jedis.set("abc", "bcd");
                    jedis.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    replicator.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        replicator.addCommandFilter(new CommandFilter() {
            @Override
            public boolean accept(Command command) {
                return command.name().equals(CommandName.name("SET"));
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                SetParser.SetCommand setCommand = (SetParser.SetCommand) command;
                assertEquals("abc", setCommand.key);
                assertEquals("bcd", setCommand.value);
                System.out.println("done");
            }
        });
        replicator.open();

    }
}
