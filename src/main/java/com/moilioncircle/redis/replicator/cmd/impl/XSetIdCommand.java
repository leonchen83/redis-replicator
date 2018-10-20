package com.moilioncircle.redis.replicator.cmd.impl;

import com.moilioncircle.redis.replicator.cmd.Command;

/**
 * @author Leon Chen
 * @since 2.6.1
 */
public class XSetIdCommand implements Command {
    
    private static final long serialVersionUID = 1L;
    
    private String key;
    
    private String id;
    
    private byte[] rawKey;
    
    private byte[] rawId;
    
    public XSetIdCommand() {
    
    }
    
    public XSetIdCommand(String key, String id) {
        this(key, id, null, null);
    }
    
    public XSetIdCommand(String key, String id, byte[] rawKey, byte[] rawId) {
        this.key = key;
        this.id = id;
        this.rawKey = rawKey;
        this.rawId = rawId;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public byte[] getRawKey() {
        return rawKey;
    }
    
    public void setRawKey(byte[] rawKey) {
        this.rawKey = rawKey;
    }
    
    public byte[] getRawId() {
        return rawId;
    }
    
    public void setRawId(byte[] rawId) {
        this.rawId = rawId;
    }
    
    @Override
    public String toString() {
        return "XSetIdCommand{" +
                "key='" + key + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
