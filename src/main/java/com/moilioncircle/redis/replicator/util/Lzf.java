/*
 * Copyright 2009-2010 Ning, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.moilioncircle.redis.replicator.util;

/**
 * @author Ning
 * @author Leon Chen
 * @see <a href="https://github.com/ning/compress/blob/master/src/main/java/com/ning/compress/lzf/impl/VanillaChunkDecoder.java">VanillaChunkDecoder.java</a>
 * @see <a href="https://github.com/ning/compress/blob/master/src/main/java/com/ning/compress/lzf/impl/VanillaChunkEncoder.java">VanillaChunkEncoder.java</a>
 * @since 2.1.0
 */
public class Lzf {
    
    private static final int MAX_OFF = 1 << 13;
    private static final int HASH_SIZE = 1 << 14;
    private static final int MAX_LITERAL = 1 << 5;
    private static final int MAX_REF = (1 << 8) + (1 << 3);
    private static final ThreadLocal<long[]> BUFFER = ThreadLocal.withInitial(() -> new long[HASH_SIZE]);
    
    public static ByteArray decode(ByteArray bytes, long len) {
        ByteArray out = new ByteArray(len);
        decode(bytes, 0, out, 0, len);
        return out;
    }
    
    private static void decode(ByteArray in, long inPos, ByteArray out, long outPos, long outEnd) {
        do {
            int ctrl = in.get(inPos++) & 255;
            if (ctrl < 1 << 5) {
                switch (ctrl) {
                    case 31:
                        out.set(outPos++, in.get(inPos++));
                    case 30:
                        out.set(outPos++, in.get(inPos++));
                    case 29:
                        out.set(outPos++, in.get(inPos++));
                    case 28:
                        out.set(outPos++, in.get(inPos++));
                    case 27:
                        out.set(outPos++, in.get(inPos++));
                    case 26:
                        out.set(outPos++, in.get(inPos++));
                    case 25:
                        out.set(outPos++, in.get(inPos++));
                    case 24:
                        out.set(outPos++, in.get(inPos++));
                    case 23:
                        out.set(outPos++, in.get(inPos++));
                    case 22:
                        out.set(outPos++, in.get(inPos++));
                    case 21:
                        out.set(outPos++, in.get(inPos++));
                    case 20:
                        out.set(outPos++, in.get(inPos++));
                    case 19:
                        out.set(outPos++, in.get(inPos++));
                    case 18:
                        out.set(outPos++, in.get(inPos++));
                    case 17:
                        out.set(outPos++, in.get(inPos++));
                    case 16:
                        out.set(outPos++, in.get(inPos++));
                    case 15:
                        out.set(outPos++, in.get(inPos++));
                    case 14:
                        out.set(outPos++, in.get(inPos++));
                    case 13:
                        out.set(outPos++, in.get(inPos++));
                    case 12:
                        out.set(outPos++, in.get(inPos++));
                    case 11:
                        out.set(outPos++, in.get(inPos++));
                    case 10:
                        out.set(outPos++, in.get(inPos++));
                    case 9:
                        out.set(outPos++, in.get(inPos++));
                    case 8:
                        out.set(outPos++, in.get(inPos++));
                    case 7:
                        out.set(outPos++, in.get(inPos++));
                    case 6:
                        out.set(outPos++, in.get(inPos++));
                    case 5:
                        out.set(outPos++, in.get(inPos++));
                    case 4:
                        out.set(outPos++, in.get(inPos++));
                    case 3:
                        out.set(outPos++, in.get(inPos++));
                    case 2:
                        out.set(outPos++, in.get(inPos++));
                    case 1:
                        out.set(outPos++, in.get(inPos++));
                    case 0:
                        out.set(outPos++, in.get(inPos++));
                }
                continue;
            }
            
            long len = ctrl >> 5;
            ctrl = -((ctrl & 0x1F) << 8) - 1;
            if (len < 7) {
                ctrl -= in.get(inPos++) & 255;
                out.set(outPos, out.get(outPos++ + ctrl));
                out.set(outPos, out.get(outPos++ + ctrl));
                switch ((int) len) {
                    case 6:
                        out.set(outPos, out.get(outPos++ + ctrl));
                    case 5:
                        out.set(outPos, out.get(outPos++ + ctrl));
                    case 4:
                        out.set(outPos, out.get(outPos++ + ctrl));
                    case 3:
                        out.set(outPos, out.get(outPos++ + ctrl));
                    case 2:
                        out.set(outPos, out.get(outPos++ + ctrl));
                    case 1:
                        out.set(outPos, out.get(outPos++ + ctrl));
                }
                continue;
            }
            
            len = in.get(inPos++) & 255;
            ctrl -= in.get(inPos++) & 255;
            
            if ((ctrl + len) < -9) {
                len += 9;
                if (len <= 32) {
                    long inPos1 = outPos + ctrl;
                    long outPos1 = outPos;
                    switch ((int) len - 1) {
                        case 31:
                            out.set(outPos1++, out.get(inPos1++));
                        case 30:
                            out.set(outPos1++, out.get(inPos1++));
                        case 29:
                            out.set(outPos1++, out.get(inPos1++));
                        case 28:
                            out.set(outPos1++, out.get(inPos1++));
                        case 27:
                            out.set(outPos1++, out.get(inPos1++));
                        case 26:
                            out.set(outPos1++, out.get(inPos1++));
                        case 25:
                            out.set(outPos1++, out.get(inPos1++));
                        case 24:
                            out.set(outPos1++, out.get(inPos1++));
                        case 23:
                            out.set(outPos1++, out.get(inPos1++));
                        case 22:
                            out.set(outPos1++, out.get(inPos1++));
                        case 21:
                            out.set(outPos1++, out.get(inPos1++));
                        case 20:
                            out.set(outPos1++, out.get(inPos1++));
                        case 19:
                            out.set(outPos1++, out.get(inPos1++));
                        case 18:
                            out.set(outPos1++, out.get(inPos1++));
                        case 17:
                            out.set(outPos1++, out.get(inPos1++));
                        case 16:
                            out.set(outPos1++, out.get(inPos1++));
                        case 15:
                            out.set(outPos1++, out.get(inPos1++));
                        case 14:
                            out.set(outPos1++, out.get(inPos1++));
                        case 13:
                            out.set(outPos1++, out.get(inPos1++));
                        case 12:
                            out.set(outPos1++, out.get(inPos1++));
                        case 11:
                            out.set(outPos1++, out.get(inPos1++));
                        case 10:
                            out.set(outPos1++, out.get(inPos1++));
                        case 9:
                            out.set(outPos1++, out.get(inPos1++));
                        case 8:
                            out.set(outPos1++, out.get(inPos1++));
                        case 7:
                            out.set(outPos1++, out.get(inPos1++));
                        case 6:
                            out.set(outPos1++, out.get(inPos1++));
                        case 5:
                            out.set(outPos1++, out.get(inPos1++));
                        case 4:
                            out.set(outPos1++, out.get(inPos1++));
                        case 3:
                            out.set(outPos1++, out.get(inPos1++));
                        case 2:
                            out.set(outPos1++, out.get(inPos1++));
                        case 1:
                            out.set(outPos1++, out.get(inPos1++));
                        case 0:
                            out.set(outPos1++, out.get(inPos1++));
                    }
                } else {
                    ByteArray.arraycopy(out, outPos + ctrl, out, outPos, len);
                }
                outPos += len;
                continue;
            }
            out.set(outPos, out.get(outPos++ + ctrl));
            out.set(outPos, out.get(outPos++ + ctrl));
            out.set(outPos, out.get(outPos++ + ctrl));
            out.set(outPos, out.get(outPos++ + ctrl));
            out.set(outPos, out.get(outPos++ + ctrl));
            out.set(outPos, out.get(outPos++ + ctrl));
            out.set(outPos, out.get(outPos++ + ctrl));
            out.set(outPos, out.get(outPos++ + ctrl));
            out.set(outPos, out.get(outPos++ + ctrl));
            
            len += outPos;
            final long end = len - 3;
            while (outPos < end) {
                out.set(outPos, out.get(outPos++ + ctrl));
                out.set(outPos, out.get(outPos++ + ctrl));
                out.set(outPos, out.get(outPos++ + ctrl));
                out.set(outPos, out.get(outPos++ + ctrl));
            }
            if (len - outPos == 3) {
                out.set(outPos, out.get(outPos++ + ctrl));
                out.set(outPos, out.get(outPos++ + ctrl));
                out.set(outPos, out.get(outPos++ + ctrl));
            } else if (len - outPos == 2) {
                out.set(outPos, out.get(outPos++ + ctrl));
                out.set(outPos, out.get(outPos++ + ctrl));
            } else if (len - outPos == 1) {
                out.set(outPos, out.get(outPos++ + ctrl));
            }
        } while (outPos < outEnd);
        
        if (outPos != outEnd) {
            throw new AssertionError("corrupt data: overrun in decompress, input offset " + inPos + ", output offset " + outPos);
        }
    }
    
    public static ByteArray encode(ByteArray bytes) {
        long len = bytes.length();
        ByteArray out = new ByteArray(len - 3);
        len = encode(bytes, len, out, 0);
        if (len <= 0) {
            return bytes;
        } else {
            ByteArray r = new ByteArray(len);
            ByteArray.arraycopy(out, 0, r, 0, len);
            return r;
        }
    }
    
    public static long encode(ByteArray in, long inLen, ByteArray out, long outPos) {
        try {
            int inPos = 0;
            long outLen = outPos + out.length() - 1;
            long[] hashTab = BUFFER.get();
            java.util.Arrays.fill(hashTab, 0);
            int literals = 0;
            outPos++;
            int future = first(in, 0);
            while (inPos < inLen - 4) {
                byte p2 = in.get(inPos + 2);
                // next
                future = (future << 8) + (p2 & 255);
                int off = hash(future);
                long ref = hashTab[off];
                hashTab[off] = inPos;
                // if (ref < inPos
                //       && ref > 0
                //       && (off = inPos - ref - 1) < MAX_OFF
                //       && in[ref + 2] == p2
                //       && (((in[ref] & 255) << 8) | (in[ref + 1] & 255)) ==
                //           ((future >> 8) & 0xffff)) {
                if (ref < inPos
                        && ref > 0
                        && (off = (int)(inPos - ref - 1)) < MAX_OFF
                        && in.get(ref + 2) == p2
                        && in.get(ref + 1) == (byte) (future >> 8)
                        && in.get(ref) == (byte) (future >> 16)) {
                    // match
                    long maxLen = inLen - inPos - 2;
                    if (maxLen > MAX_REF) {
                        maxLen = MAX_REF;
                    }
                    if (outPos + 3 + 1 >= outLen) {
                        int c = literals == 0 ? 1 : 0;
                        if (outPos - c + 3 + 1 >= outLen)
                            return 0;
                    }
                    if (literals == 0) {
                        // multiple back-references,
                        // so there is no literal run control byte
                        outPos--;
                    } else {
                        // set the control byte at the start of the literal run
                        // to store the number of literals
                        out.set(outPos - literals - 1, (byte) (literals - 1));
                        literals = 0;
                    }
                    int len = 3;
                    while (len < maxLen && in.get(ref + len) == in.get(inPos + len)) {
                        len++;
                    }
                    len -= 2;
                    if (len < 7) {
                        out.set(outPos++, (byte) ((off >> 8) + (len << 5)));
                    } else {
                        out.set(outPos++, (byte) ((off >> 8) + (7 << 5)));
                        out.set(outPos++, (byte) (len - 7));
                    }
                    out.set(outPos++, (byte) off);
                    // move one byte forward to allow for a literal run control byte
                    outPos++;
                    inPos += len;
                    // rebuild the future, and store the last bytes to the hashtable.
                    // Storing hashes of the last bytes in back-reference improves
                    // the compression ratio and only reduces speed slightly.
                    future = first(in, inPos);
                    future = next(future, in, inPos);
                    hashTab[hash(future)] = inPos++;
                    future = next(future, in, inPos);
                    hashTab[hash(future)] = inPos++;
                } else {
                    // copy one byte from input to output as part of literal
                    if (outPos >= outLen) {
                        return 0;
                    }
                    out.set(outPos++, in.get(inPos++));
                    literals++;
                    // at the end of this literal chunk, write the length
                    // to the control byte and start a new chunk
                    if (literals == MAX_LITERAL) {
                        out.set(outPos - literals - 1, (byte) (literals - 1));
                        literals = 0;
                        // move ahead one byte to allow for the
                        // literal run control byte
                        outPos++;
                    }
                }
            }
    
            if (outPos + 3 > outLen) {
                return 0;
            }
    
            // write the remaining few bytes as literals
            while (inPos < inLen) {
                out.set(outPos++, in.get(inPos++));
                literals++;
                if (literals == MAX_LITERAL) {
                    out.set(outPos - literals - 1, (byte) (literals - 1));
                    literals = 0;
                    outPos++;
                }
            }
            // writes the final literal run length to the control byte
            out.set(outPos - literals - 1, (byte) (literals - 1));
            if (literals == 0) {
                outPos--;
            }
            return outPos;
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
    
    private static int first(ByteArray in, long inPos) {
        return (in.get(inPos) << 8) | (in.get(inPos + 1) & 255);
    }
    
    private static int next(int v, ByteArray in, long inPos) {
        return (v << 8) | (in.get(inPos + 2) & 255);
    }
    
    private static int hash(int h) {
        return ((h * 2777) >> 9) & (HASH_SIZE - 1);
    }
}
