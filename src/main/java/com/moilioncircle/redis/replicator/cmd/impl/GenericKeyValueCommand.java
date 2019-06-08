package com.moilioncircle.redis.replicator.cmd.impl;

/**
 * @author René Kerner (@rk3rn3r)
 * @since 3.3.0
 */
class GenericKeyValueCommand extends GenericKeyCommand {

    private byte[] value;

    GenericKeyValueCommand() {
    }

    GenericKeyValueCommand(byte[] key, byte[] value) {
        super(key);
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
