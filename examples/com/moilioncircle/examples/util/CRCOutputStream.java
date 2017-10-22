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

package com.moilioncircle.examples.util;

import java.io.IOException;
import java.io.OutputStream;

import static com.moilioncircle.examples.util.CRC64.crc64;

/**
 * @author Leon Chen
 * @since 2.5.0
 */
public class CRCOutputStream extends OutputStream {

    private long checksum = 0L;
    private final OutputStream out;

    public CRCOutputStream(OutputStream out) {
        this.out = out;
    }

    public byte[] getCRC64() {
        return longToByteArray(checksum);
    }

    private static byte[] longToByteArray(long value) {
        return new byte[]{
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24),
                (byte) (value >> 32),
                (byte) (value >> 40),
                (byte) (value >> 48),
                (byte) (value >> 56),
        };
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        checksum = crc64(new byte[]{(byte) b}, checksum);
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        checksum = crc64(b, off, len, checksum);
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void close() throws IOException {
        out.close();
    }
}
