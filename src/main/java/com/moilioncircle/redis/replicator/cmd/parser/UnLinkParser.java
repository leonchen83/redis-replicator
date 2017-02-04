package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.UnLinkCommand;

/**
 * Created by leon on 2/2/17.
 */
public class UnLinkParser implements CommandParser<UnLinkCommand> {
    @Override
    public UnLinkCommand parse(Object[] command) {
        int idx = 1;
        String[] keys = new String[command.length - 1];
        for (int i = idx, j = 0; i < command.length; i++, j++) {
            keys[j] = (String) command[i];
        }
        return new UnLinkCommand(keys);
    }
}
