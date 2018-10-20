package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.XSetIdCommand;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;

/**
 * @author Leon Chen
 * @since 2.6.1
 */
public class XSetIdParser implements CommandParser<XSetIdCommand> {
    
    @Override
    public XSetIdCommand parse(Object[] command) {
        int idx = 1;
        byte[] key = toBytes(command[idx]);
        idx++;
        byte[] id = toBytes(command[idx]);
        idx++;
        return new XSetIdCommand(key, id);
    }
}
