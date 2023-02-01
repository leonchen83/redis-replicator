/*
 * Copyright 2016-2017 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.online;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.moilioncircle.redis.replicator.Configuration;
import com.moilioncircle.redis.replicator.client.RESP2;
import com.moilioncircle.redis.replicator.client.RESP2Client;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
public class RESP2ClientTest {
    
    @Test
    public void test() throws IOException {
        try (RESP2Client client = new RESP2Client("127.0.0.1", 6379, Configuration.defaultSetting())) {
            RESP2Client.Command command = client.newCommand();
            RESP2.Node pong = command.invoke("ping".getBytes());
            assertEquals(RESP2.Type.STRING, pong.type);
            assertEquals("PONG", pong.getString().toUpperCase());
            
            command.post(n -> {
                assertEquals(RESP2.Type.STRING, n.type);
                assertEquals("PONG", n.getString().toUpperCase());
            }, "ping".getBytes());
            
            command.post(n -> {
                assertEquals(RESP2.Type.STRING, n.type);
                assertEquals("PONG", n.getString().toUpperCase());
            }, "ping".getBytes());
            command.get();
        }
    }
    
    @Test
    public void test1() throws IOException {
        try (RESP2Client client = new RESP2Client("127.0.0.1", 6380, Configuration.defaultSetting().setAuthPassword("test"))) {
            RESP2Client.Command command = client.newCommand();
            RESP2.Node pong = command.invoke("ping");
            assertEquals(RESP2.Type.STRING, pong.type);
            assertEquals("PONG", pong.getString().toUpperCase());
            
            command.post(n -> {
                assertEquals(RESP2.Type.STRING, n.type);
                assertEquals("PONG", n.getString().toUpperCase());
            }, "ping");
            
            command.post(n -> {
                assertEquals(RESP2.Type.STRING, n.type);
                assertEquals("PONG", n.getString().toUpperCase());
            }, "ping");
            command.get();
        }
    }
}
