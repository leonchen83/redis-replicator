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
    private Long entriesAdded;
    private byte[] maxDeletedEntryId;
    
    public XSetIdCommand() {
    }
    
    public XSetIdCommand(byte[] key, byte[] id) {
        super(key);
        this.id = id;
    }
    
    public XSetIdCommand(byte[] key, byte[] id, Long entriesAdded, byte[] maxDeletedEntryId) {
        this(key, id);
        this.entriesAdded = entriesAdded;
        this.maxDeletedEntryId = maxDeletedEntryId;
    }

    public byte[] getId() {
        return id;
    }
    
    public void setId(byte[] id) {
        this.id = id;
    }
    
    public Long getEntriesAdded() {
        return entriesAdded;
    }
    
    public void setEntriesAdded(Long entriesAdded) {
        this.entriesAdded = entriesAdded;
    }
    
    public byte[] getMaxDeletedEntryId() {
        return maxDeletedEntryId;
    }
    
    public void setMaxDeletedEntryId(byte[] maxDeletedEntryId) {
        this.maxDeletedEntryId = maxDeletedEntryId;
    }
}
