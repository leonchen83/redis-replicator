package com.moilioncircle.redis.replicator.rdb.datatype;

import com.moilioncircle.redis.replicator.event.Event;

/**
 * Created by leon on 2017/1/30.
 */
public class AuxField implements Event {

    private String auxKey;
    private String auxValue;

    public AuxField() {
    }

    public AuxField(String auxKey, String auxValue) {
        this.auxKey = auxKey;
        this.auxValue = auxValue;
    }

    public String getAuxKey() {
        return auxKey;
    }

    public String getAuxValue() {
        return auxValue;
    }

    public void setAuxKey(String auxKey) {
        this.auxKey = auxKey;
    }

    public void setAuxValue(String auxValue) {
        this.auxValue = auxValue;
    }

    @Override
    public String toString() {
        return "AuxField{" +
                "auxKey='" + auxKey + '\'' +
                ", auxValue='" + auxValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuxField auxField = (AuxField) o;

        return auxKey.equals(auxField.auxKey);
    }

    @Override
    public int hashCode() {
        return auxKey.hashCode();
    }
}
