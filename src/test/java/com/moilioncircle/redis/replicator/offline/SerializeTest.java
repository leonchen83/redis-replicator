/*
 * Copyright 2016-2019 Leon Chen
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

package com.moilioncircle.redis.replicator.offline;

import static com.moilioncircle.redis.replicator.util.Tuples.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;

/**
 * @author Leon Chen
 * @since 3.3.0
 */
public class SerializeTest {
    
    @Test
    public void test() throws Exception {
        SetCommand command = new SetCommand();
        command.setKey("key".getBytes());
        command.setValue("value".getBytes());
        command.getContext().setOffsets(of(1L, 10L));
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(o);
        os.writeObject(command);
        os.close();
        byte[] serialized = o.toByteArray();
        ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(serialized));
        SetCommand command1 = (SetCommand)is.readObject();
        is.close();
        assertEquals("key", new String(command1.getKey()));
        assertEquals("value", new String(command1.getValue()));
        assertEquals(1L, command1.getContext().getOffsets().getV1().longValue());
        assertEquals(10L, command1.getContext().getOffsets().getV2().longValue());
    }
}
