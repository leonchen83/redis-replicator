package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * @author Leon Chen
 * @since 2.6.1
 */
public class XSetIdCommand implements Command {
    
    private static final long serialVersionUID = 1L;
    
    private byte[] key;
    
    private byte[] id;
    
    public XSetIdCommand() {
    
    }
    
    public XSetIdCommand(byte[] key, byte[] id) {
        this.key = key;
        this.id = id;
    }
    
    public byte[] getKey() {
        return key;
    }
    
    public void setKey(byte[] key) {
        this.key = key;
    }
    
    public byte[] getId() {
        return id;
    }
    
    public void setId(byte[] id) {
        this.id = id;
    }
}
