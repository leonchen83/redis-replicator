package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.DecrByCommand;

import java.math.BigDecimal;

/**
 * Created by leon on 2/5/17.
 */
public class DecrByParser implements CommandParser<DecrByCommand> {
    @Override
    public DecrByCommand parse(Object[] command) {
        int idx = 1;
        String key = (String) command[idx++];
        long value = new BigDecimal((String) command[idx++]).longValueExact();
        return new DecrByCommand(key, value);
    }
}
