package com.moilioncircle.redis.replicator.cmd.impl;

import java.io.Serializable;

/**
 * Created by leon on 2/2/17.
 */
public enum Op implements Serializable {
    AND, OR, XOR, NOT
}
