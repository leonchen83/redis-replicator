package com.moilioncircle.redis.replicator;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Baoyi Chen
 */
public class ConfigurationTest {
    
    @Test
    public void test0() {
        try {
            Configuration.defaultSetting().toString();
        } catch (Throwable e) {
            fail();
        }
    }
    
    @Test
    public void test1() {
        try {
            Configuration.defaultSetting().setAuthPassword("").toString();
        } catch (Throwable e) {
            fail();
        }
    }
}