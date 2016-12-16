package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;

import java.util.Arrays;

/**
 * Created by Adrian Yao on 2016/12/9.
 */
public class SremParser implements CommandParser<SremParser.SremCommand> {

    @Override
    public SremCommand parse(CommandName cmdName, Object[] params) {
        String key = (String) params[0];
        int length = params.length - 1;
        String[] members = new String[length];
        for (int i = 0; i < length; i++) {
            members[i] = (String) params[i+1];
        }
        return new SremCommand(key, members);
    }

    public static class SremCommand implements Command {

        public String key;
        public String[] members;

        public SremCommand(String key, String[] members) {
            this.key = key;
            this.members = members;
        }

        @Override
        public CommandName name() {
            return CommandName.name("SREM");
        }

        @Override
        public String toString() {
            return "SremCommand{" +
                    "key='" + key + '\'' +
                    ", members=" + Arrays.toString(members) +
                    '}';
        }
    }
}
