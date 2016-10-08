package com.moilioncircle.examples;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;

/**
 * Created by leon on 10/8/16.
 */
public class CommandParserExample {
    public static void main(String[] args) throws Exception {
        final RedisReplicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());

        replicator.addCommandParser(CommandName.name("APPEND"), new YourAppendParser());

        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof YourAppendParser.YourAppendCommand) {
                    YourAppendParser.YourAppendCommand yourAppendCommand = (YourAppendParser.YourAppendCommand) command;
                    System.out.println(yourAppendCommand.key);
                    System.out.println(yourAppendCommand.value);
                }
            }
        });
        replicator.open();

        System.in.read();
    }

    public static class YourAppendParser implements CommandParser<YourAppendParser.YourAppendCommand> {

        @Override
        public YourAppendCommand parse(CommandName cmdName, Object[] params) {
            return new YourAppendCommand((String) params[0], (String) params[1]);
        }

        public static class YourAppendCommand implements Command {
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

            @Override
            public CommandName name() {
                return CommandName.name("APPEND");
            }
        }
    }
}
