package com.moilioncircle.examples;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandListener;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueModule;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.Module;
import com.moilioncircle.redis.replicator.rdb.module.DefaultRdbModuleParser;
import com.moilioncircle.redis.replicator.rdb.module.ModuleParser;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by leon on 2/2/17.
 */
public class ModuleParserExample {
    public static void main(String[] args) throws IOException {
        RedisReplicator replicator = new RedisReplicator("127.0.0.1", 6381, Configuration.defaultSetting());
        replicator.addCommandParser(CommandName.name("hellotype.insert"), new HelloTypeParser());
        replicator.addModuleParser("hellotype", 0, new HelloTypeModuleParser());
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (kv instanceof KeyStringValueModule) {
                    System.out.println(kv);
                }
            }
        });

        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof HelloTypeCommand) {
                    System.out.println(command);
                }
            }
        });

        replicator.open();
    }

    public static class HelloTypeModuleParser implements ModuleParser<HelloTypeModule> {

        @Override
        public HelloTypeModule parse(RedisInputStream in) throws IOException {
            DefaultRdbModuleParser parser = new DefaultRdbModuleParser(in);
            int elements = (int) parser.loadUnSigned();
            long[] ary = new long[elements];
            int i = 0;
            while (elements-- > 0) {
                ary[i++] = parser.loadSigned();
            }
            return new HelloTypeModule(ary);
        }
    }

    public static class HelloTypeModule implements Module {
        private final long[] value;

        public HelloTypeModule(long[] value) {
            this.value = value;
        }

        public long[] getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "HelloTypeModule{" +
                    "value=" + Arrays.toString(value) +
                    '}';
        }
    }

    public static class HelloTypeParser implements CommandParser<HelloTypeCommand> {
        @Override
        public HelloTypeCommand parse(CommandName cmdName, Object[] params) {
            String key = (String) params[0];
            long value = Long.parseLong((String) params[1]);
            return new HelloTypeCommand(key, value);
        }
    }

    public static class HelloTypeCommand implements Command {
        private final String key;
        private final long value;

        public long getValue() {
            return value;
        }

        public String getKey() {
            return key;
        }

        public HelloTypeCommand(String key, long value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "HelloTypeCommand{" +
                    "key='" + key + '\'' +
                    ", value=" + value +
                    '}';
        }

    }
}
