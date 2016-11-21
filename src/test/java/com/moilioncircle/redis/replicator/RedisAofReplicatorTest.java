package com.moilioncircle.redis.replicator;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandFilter;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.impl.SetParser;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 11/21/16.
 */
public class RedisAofReplicatorTest {
    @Test
    public void open() throws Exception {
        Replicator replicator = new RedisReplicator(
                RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"),
                Configuration.defaultSetting(), false);
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                acc.incrementAndGet();
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                assertEquals(4, acc.get());
            }
        });
        replicator.open();
    }

    @Test
    public void open2() throws Exception {
        Replicator replicator = new RedisReplicator(
                RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly2.aof"),
                Configuration.defaultSetting(), false);
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addCommandFilter(new CommandFilter() {
            @Override
            public boolean accept(Command command) {
                return command instanceof SetParser.SetCommand && ((SetParser.SetCommand) command).getKey().startsWith("test_");
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                acc.incrementAndGet();
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                assertEquals(8000, acc.get());
            }
        });
        replicator.open();
    }

}