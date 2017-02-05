package com.moilioncircle.redis.replicator;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
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
                RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly1.aof"), FileType.AOF,
                Configuration.defaultSetting());
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
                System.out.println("close open");
                assertEquals(4, acc.get());
            }
        });
        replicator.open();
    }

    @Test
    public void open2() throws Exception {
        Replicator replicator = new RedisReplicator(
                RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly2.aof"), FileType.AOF,
                Configuration.defaultSetting());
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof SetCommand && ((SetCommand) command).getKey().startsWith("test_")) {
                    acc.incrementAndGet();
                }
            }
        });
        replicator.addCloseListener(new CloseListener() {
            @Override
            public void handle(Replicator replicator) {
                System.out.println("close open2");
                assertEquals(48000, acc.get());
            }
        });
        replicator.open();
    }

    @Test
    public void open3() throws Exception {
        Replicator replicator = new RedisReplicator(
                RedisSocketReplicatorTest.class.getClassLoader().getResourceAsStream("appendonly3.aof"), FileType.AOF,
                Configuration.defaultSetting());
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
                System.out.println("close open3");
                assertEquals(92539, acc.get());
            }
        });
        replicator.open();
    }

}