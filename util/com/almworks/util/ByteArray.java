/*
 * Copyright 2014 ALM Works Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almworks.util;

import java.io.IOException;
import java.io.InputStream;

public class ByteArray {
  private byte[] myBytes;
  private int mySize = 0;

  public ByteArray() {
    this(10);
  }

  public ByteArray(int size) {
    this(new byte[size], 0);
  }

  public ByteArray(byte[] bytes, int size) {
    myBytes = bytes;
    mySize = size;
  }

  /**
   *
   * @param stream an {@link InputStream} to read from
   * @param count maximum number of bytes to read
   * @return See {@link InputStream#read(byte[], int, int)}
   * @throws IOException
   */
  public int readFrom(InputStream stream, int count) throws IOException {
    ensureCapacity(mySize + count);
    int readBytes = stream.read(myBytes, mySize, count);
    if (readBytes > 0) mySize += readBytes;
    return readBytes;
  }

  public void readAllFromStream(InputStream stream) throws IOException {
    //noinspection StatementWithEmptyBody
    while (readFrom(stream, Math.max(1, stream.available())) >= 0);
  }

  public byte[] toNativeArray() {
    byte[] bytes = new byte[mySize];
    getBytes(0, bytes, mySize);
    return bytes;
  }

  public void getBytes(int offset, byte[] bytes, int length) {
    if (offset  + length > mySize)
      throw new IndexOutOfBoundsException(offset + " " + mySize);
    System.arraycopy(myBytes, offset, bytes, 0, length);
  }

  private void ensureCapacity(int expectedSize) {
    if (expectedSize <= myBytes.length) return;
    byte[] newBytes = new byte[Math.max(expectedSize, myBytes.length*2)];
    System.arraycopy(myBytes, 0, newBytes, 0, mySize);
    myBytes = newBytes;
  }

  public String toString() {
    int length = mySize;
    byte[] bytes = myBytes;
    return toString(bytes, length);
  }

  public static String toString(byte[] bytes, int length) {
    StringBuilder builder = new StringBuilder("ByteArray [");
    for (int i = 0; i < length; i++) {
      int aByte = bytes[i];
      if (aByte < 0)
        aByte = aByte - Byte.MIN_VALUE + Byte.MAX_VALUE;
      if (i > 0)
        builder.append(",");
      builder.append(Integer.toHexString(aByte));
    }
    builder.append("]");
    return builder.toString();
  }

  public static String toString(byte[] bytes) {
    return bytes != null ? toString(bytes, bytes.length) : "<null>";
  }
}
