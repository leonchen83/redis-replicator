package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.CommandName;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.UnLinkCommand;

/**
 * Created by leon on 2/2/17.
 */
public class UnLinkParser implements CommandParser<UnLinkCommand> {
    @Override
    public UnLinkCommand parse(CommandName cmdName, Object[] params) {
        String[] keys = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            keys[i] = (String) params[i];
        }
        return new UnLinkCommand(keys);
    }
}
