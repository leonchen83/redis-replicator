package com.moilioncircle.redis.replicator.cmd.parser;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toLong;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.XSetIdCommand;

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
        Long entriesAdded = null;
        byte[] maxDeletedEntryId = null;
        while (idx < command.length) {
            String next = toRune(command[idx++]);
            if (isEquals(next, "ENTRIESADDED")) {
                entriesAdded = toLong(command[idx++]);
            } else if (isEquals(next, "MAXDELETEDID")) {
                maxDeletedEntryId = toBytes(command[idx++]);
            } else {
                throw new UnsupportedOperationException(next);
            }
        }
        return new XSetIdCommand(key, id, entriesAdded, maxDeletedEntryId);
    }
}
