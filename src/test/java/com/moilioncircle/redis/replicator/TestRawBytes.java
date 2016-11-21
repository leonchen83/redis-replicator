package com.moilioncircle.redis.replicator;

import com.moilioncircle.redis.replicator.rdb.RdbFilter;
import com.moilioncircle.redis.replicator.rdb.RdbListener;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyStringValueString;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by leon on 9/25/16.
 */
public class TestRawBytes {
    public static void main(String[] args) throws IOException, InterruptedException {
        Replicator replicator = new RedisRdbReplicator(
                TestRawBytes.class.getClassLoader().getResourceAsStream("dumpV7.rdb"),
                Configuration.defaultSetting());
        replicator.addRdbFilter(new RdbFilter() {
            @Override
            public boolean accept(KeyValuePair<?> kv) {
                return kv.getValueRdbType() == 0;
            }
        });
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (kv.getValueRdbType() == 0) {
                    KeyStringValueString ksvs = (KeyStringValueString) kv;
                    System.out.println("key:" + ksvs.getKey() + ",value:" + ksvs.getValue() + ",len:" + ksvs.getRawBytes().length + "," + Arrays.toString(ksvs.getRawBytes()));
                }
            }
        });
        replicator.open();
    }
}
