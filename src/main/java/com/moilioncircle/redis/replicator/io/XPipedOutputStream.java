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

package com.moilioncircle.redis.replicator.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @author Leon Chen
 * @since 3.7.0
 */
public class XPipedOutputStream extends OutputStream {
    private XPipedInputStream is;
    
    public XPipedOutputStream() {
    }
    
    public XPipedOutputStream(XPipedInputStream is) {
        connect(is);
    }
    
    void connect(XPipedInputStream is) {
        this.is = Objects.requireNonNull(is);
    }
    
    @Override
    public void write(int b) throws IOException {
        Objects.requireNonNull(is).write(b);
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        Objects.requireNonNull(is);
        while (len > 0) {
            int w = is.write(b, off, len);
            len -= w;
            off += w;
        }
    }
    
    @Override
    public void close() throws IOException {
        if (is != null) {
            is.close();
        }
    }
}
