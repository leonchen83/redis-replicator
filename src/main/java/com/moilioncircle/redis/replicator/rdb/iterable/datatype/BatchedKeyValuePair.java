package com.moilioncircle.redis.replicator.rdb.iterable.datatype;

import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

/**
 * @author Baoyi Chen
 */
public class BatchedKeyValuePair<K, V> extends KeyValuePair<K, V> {

    private static final long serialVersionUID = 1L;

    private int batch;
    private boolean last;

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
