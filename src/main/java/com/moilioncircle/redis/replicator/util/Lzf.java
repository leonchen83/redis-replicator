/*
 * Copyright 2016 leon chen
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

package com.moilioncircle.redis.replicator.util;

/**
 * @author leon.chen
 * @since 2016/8/11
 */
public class Lzf {

    public static byte[] decode(byte[] bytes, int len) {
        byte[] out = new byte[len];
        decode(bytes, 0, out, 0, len);
        return out;
    }

    public static void decode(byte[] in, int inPos, byte[] out, int outPos, int outEnd) {
        do {
            int ctrl = in[inPos++] & 255;
            if (ctrl < 1 << 5) {
                switch (ctrl) {
                    case 31:
                        out[outPos++] = in[inPos++];
                    case 30:
                        out[outPos++] = in[inPos++];
                    case 29:
                        out[outPos++] = in[inPos++];
                    case 28:
                        out[outPos++] = in[inPos++];
                    case 27:
                        out[outPos++] = in[inPos++];
                    case 26:
                        out[outPos++] = in[inPos++];
                    case 25:
                        out[outPos++] = in[inPos++];
                    case 24:
                        out[outPos++] = in[inPos++];
                    case 23:
                        out[outPos++] = in[inPos++];
                    case 22:
                        out[outPos++] = in[inPos++];
                    case 21:
                        out[outPos++] = in[inPos++];
                    case 20:
                        out[outPos++] = in[inPos++];
                    case 19:
                        out[outPos++] = in[inPos++];
                    case 18:
                        out[outPos++] = in[inPos++];
                    case 17:
                        out[outPos++] = in[inPos++];
                    case 16:
                        out[outPos++] = in[inPos++];
                    case 15:
                        out[outPos++] = in[inPos++];
                    case 14:
                        out[outPos++] = in[inPos++];
                    case 13:
                        out[outPos++] = in[inPos++];
                    case 12:
                        out[outPos++] = in[inPos++];
                    case 11:
                        out[outPos++] = in[inPos++];
                    case 10:
                        out[outPos++] = in[inPos++];
                    case 9:
                        out[outPos++] = in[inPos++];
                    case 8:
                        out[outPos++] = in[inPos++];
                    case 7:
                        out[outPos++] = in[inPos++];
                    case 6:
                        out[outPos++] = in[inPos++];
                    case 5:
                        out[outPos++] = in[inPos++];
                    case 4:
                        out[outPos++] = in[inPos++];
                    case 3:
                        out[outPos++] = in[inPos++];
                    case 2:
                        out[outPos++] = in[inPos++];
                    case 1:
                        out[outPos++] = in[inPos++];
                    case 0:
                        out[outPos++] = in[inPos++];
                }
                continue;
            }

            int len = ctrl >> 5;
            ctrl = -((ctrl & 0x1f) << 8) - 1;
            if (len < 7) {
                ctrl -= in[inPos++] & 255;
                out[outPos] = out[outPos++ + ctrl];
                out[outPos] = out[outPos++ + ctrl];
                switch (len) {
                    case 6:
                        out[outPos] = out[outPos++ + ctrl];
                    case 5:
                        out[outPos] = out[outPos++ + ctrl];
                    case 4:
                        out[outPos] = out[outPos++ + ctrl];
                    case 3:
                        out[outPos] = out[outPos++ + ctrl];
                    case 2:
                        out[outPos] = out[outPos++ + ctrl];
                    case 1:
                        out[outPos] = out[outPos++ + ctrl];
                }
                continue;
            }

            len = in[inPos++] & 255;
            ctrl -= in[inPos++] & 255;

            if ((ctrl + len) < -9) {
                len += 9;
                if (len <= 32) {
                    int inPos1 = outPos + ctrl;
                    int outPos1 = outPos;
                    switch (len - 1) {
                        case 31:
                            out[outPos1++] = out[inPos1++];
                        case 30:
                            out[outPos1++] = out[inPos1++];
                        case 29:
                            out[outPos1++] = out[inPos1++];
                        case 28:
                            out[outPos1++] = out[inPos1++];
                        case 27:
                            out[outPos1++] = out[inPos1++];
                        case 26:
                            out[outPos1++] = out[inPos1++];
                        case 25:
                            out[outPos1++] = out[inPos1++];
                        case 24:
                            out[outPos1++] = out[inPos1++];
                        case 23:
                            out[outPos1++] = out[inPos1++];
                        case 22:
                            out[outPos1++] = out[inPos1++];
                        case 21:
                            out[outPos1++] = out[inPos1++];
                        case 20:
                            out[outPos1++] = out[inPos1++];
                        case 19:
                            out[outPos1++] = out[inPos1++];
                        case 18:
                            out[outPos1++] = out[inPos1++];
                        case 17:
                            out[outPos1++] = out[inPos1++];
                        case 16:
                            out[outPos1++] = out[inPos1++];
                        case 15:
                            out[outPos1++] = out[inPos1++];
                        case 14:
                            out[outPos1++] = out[inPos1++];
                        case 13:
                            out[outPos1++] = out[inPos1++];
                        case 12:
                            out[outPos1++] = out[inPos1++];
                        case 11:
                            out[outPos1++] = out[inPos1++];
                        case 10:
                            out[outPos1++] = out[inPos1++];
                        case 9:
                            out[outPos1++] = out[inPos1++];
                        case 8:
                            out[outPos1++] = out[inPos1++];
                        case 7:
                            out[outPos1++] = out[inPos1++];
                        case 6:
                            out[outPos1++] = out[inPos1++];
                        case 5:
                            out[outPos1++] = out[inPos1++];
                        case 4:
                            out[outPos1++] = out[inPos1++];
                        case 3:
                            out[outPos1++] = out[inPos1++];
                        case 2:
                            out[outPos1++] = out[inPos1++];
                        case 1:
                            out[outPos1++] = out[inPos1++];
                        case 0:
                            out[outPos1++] = out[inPos1++];
                    }
                } else {
                    System.arraycopy(out, outPos + ctrl, out, outPos, len);
                }
                outPos += len;
                continue;
            }

            out[outPos] = out[outPos++ + ctrl];
            out[outPos] = out[outPos++ + ctrl];
            out[outPos] = out[outPos++ + ctrl];
            out[outPos] = out[outPos++ + ctrl];
            out[outPos] = out[outPos++ + ctrl];
            out[outPos] = out[outPos++ + ctrl];
            out[outPos] = out[outPos++ + ctrl];
            out[outPos] = out[outPos++ + ctrl];
            out[outPos] = out[outPos++ + ctrl];

            len += outPos;
            final int end = len - 3;
            while (outPos < end) {
                out[outPos] = out[outPos++ + ctrl];
                out[outPos] = out[outPos++ + ctrl];
                out[outPos] = out[outPos++ + ctrl];
                out[outPos] = out[outPos++ + ctrl];
            }
            switch (len - outPos) {
                case 3:
                    out[outPos] = out[outPos++ + ctrl];
                case 2:
                    out[outPos] = out[outPos++ + ctrl];
                case 1:
                    out[outPos] = out[outPos++ + ctrl];
            }
        } while (outPos < outEnd);

        if (outPos != outEnd) {
            throw new AssertionError("Corrupt data: overrun in decompress, input offset " + inPos + ", output offset " + outPos);
        }
    }

}
