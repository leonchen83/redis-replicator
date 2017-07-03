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
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.AuxFieldListener;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.AuxField;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * @author Leon Chen
 * @since 2.1.0
 */
@SuppressWarnings("resource")
public class UnsupportOperationTest {

    @Test
    public void testRdb() throws IOException {
        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), FileType.RDB,
                    Configuration.defaultSetting());
            replicator.addCommandListener(new CommandListener() {
                @Override
                public void handle(Replicator replicator, Command command) {
                }
            });
            replicator.open();
            fail();
        } catch (Exception e) {
        }

        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), FileType.RDB,
                    Configuration.defaultSetting());
            replicator.removeCommandListener(new CommandListener() {
                @Override
                public void handle(Replicator replicator, Command command) {
                }
            });
            replicator.open();
            fail();
        } catch (Exception e) {
        }

        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), FileType.RDB,
                    Configuration.defaultSetting());
            replicator.getCommandParser(CommandName.name("PING"));
            replicator.open();
            fail();
        } catch (Exception e) {
        }

        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), FileType.RDB,
                    Configuration.defaultSetting());
            replicator.addCommandParser(CommandName.name("PING"), new CommandParser<Command>() {
                @Override
                public Command parse(Object[] command) {
                    return null;
                }
            });
            replicator.open();
            fail();
        } catch (Exception e) {
        }

        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), FileType.RDB,
                    Configuration.defaultSetting());
            replicator.removeCommandParser(CommandName.name("PING"));
            replicator.open();
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testAof() throws IOException {
        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("dumpV7.rdb"), FileType.AOF,
                    Configuration.defaultSetting());
            replicator.addRdbListener(new RdbListener.Adaptor() {
                @Override
                public void handle(Replicator replicator, KeyValuePair<?> kv) {

                }
            });
            replicator.open();
            fail();
        } catch (Exception e) {
        }

        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"), FileType.AOF,
                    Configuration.defaultSetting());
            replicator.removeRdbListener(new RdbListener.Adaptor() {
                @Override
                public void handle(Replicator replicator, KeyValuePair<?> kv) {

                }
            });
            replicator.open();
            fail();
        } catch (Exception e) {
        }

        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"), FileType.AOF,
                    Configuration.defaultSetting());
            replicator.getModuleParser("hellotype", 0);
            replicator.open();
            fail();
        } catch (Exception e) {
        }

        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"), FileType.AOF,
                    Configuration.defaultSetting());
            replicator.addModuleParser("hellotype", 0, new ModuleParser<Module>() {
                @Override
                public Module parse(RedisInputStream in, int version) throws IOException {
                    return null;
                }
            });
            replicator.open();
            fail();
        } catch (Exception e) {
        }

        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"), FileType.AOF,
                    Configuration.defaultSetting());
            replicator.removeModuleParser("hellotype", 0);
            replicator.open();
            fail();
        } catch (Exception e) {
        }

        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"), FileType.AOF,
                    Configuration.defaultSetting());
            replicator.addAuxFieldListener(new AuxFieldListener() {
                @Override
                public void handle(Replicator replicator, AuxField auxField) {

                }
            });
            replicator.open();
            fail();
        } catch (Exception e) {
        }

        try {
            Replicator replicator = new RedisReplicator(
                    RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"), FileType.AOF,
                    Configuration.defaultSetting());
            replicator.removeAuxFieldListener(new AuxFieldListener() {
                @Override
                public void handle(Replicator replicator, AuxField auxField) {

                }
            });
            replicator.open();
            fail();
        } catch (Exception e) {
        }
    }
}
