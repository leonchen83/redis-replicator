package com.moilioncircle.redis.replicator.rdb.datatype;

/**
 * Created by leon on 8/20/16.
 */
public class Db {
    public int dbNumber;
    /* rdb version 7 */
    public int dbsize = -1;
    /* rdb version 7 */
    public int expires = -1;

    public Db(int dbNumber) {
        this.dbNumber = dbNumber;
    }

    public Db(int dbNumber, int dbsize, int expires) {
        this.dbNumber = dbNumber;
        this.dbsize = dbsize;
        this.expires = expires;
    }

    public int getDbNumber() {
        return dbNumber;
    }

    public void setDbNumber(int dbNumber) {
        this.dbNumber = dbNumber;
    }

    public int getDbsize() {
        return dbsize;
    }

    public void setDbsize(int dbsize) {
        this.dbsize = dbsize;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    @Override
    public String toString() {
        return "Db{" +
                "dbNumber=" + dbNumber +
                ", dbsize=" + dbsize +
                ", expires=" + expires +
                '}';
    }
}
