package com.moilioncircle.redis.replicator.offline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.RedisURI;

/**
 * @author Leon Chen
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
    
    @Test
    public void test2() throws URISyntaxException {
        RedisURI uri = new RedisURI("redis://127.0.0.1:6379?authUser=user&authPassword=password");
        Configuration configuration = Configuration.valueOf(uri);
        assertEquals("user", configuration.getAuthUser());
        assertEquals("password", configuration.getAuthPassword());
    
        uri = new RedisURI("redis://us:ps@127.0.0.1:6379?authUser=user&authPassword=password");
        configuration = Configuration.valueOf(uri);
        assertEquals("us", configuration.getAuthUser());
        assertEquals("ps", configuration.getAuthPassword());
    }
    
    @Test
    public void test3() {
        try {
            int timeout = Configuration.defaultSetting().getReadTimeout();
            assertEquals(60000, timeout);
        } catch (Throwable e) {
            fail();
        }
    }
}