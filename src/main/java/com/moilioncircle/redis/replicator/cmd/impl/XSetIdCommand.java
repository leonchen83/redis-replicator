package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.CommandSpec;

/**
 * @author Leon Chen
 * @since 2.6.1
 */
@CommandSpec(command = "XSETID")
public class XSetIdCommand extends GenericKeyCommand {
    
    private static final long serialVersionUID = 1L;
    
    private byte[] id;
    
    public XSetIdCommand() {
    }
    
    public XSetIdCommand(byte[] key, byte[] id) {
        super(key);
        this.id = id;
    }

    public byte[] getId() {
        return id;
    }
    
    public void setId(byte[] id) {
        this.id = id;
    }
}
