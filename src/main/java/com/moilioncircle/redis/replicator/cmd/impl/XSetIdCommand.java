package com.moilioncircle.redis.replicator.cmd.impl;

/**
 * @author Leon Chen
 * @since 2.6.1
 */
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
