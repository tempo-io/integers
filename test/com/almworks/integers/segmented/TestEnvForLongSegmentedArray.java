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

package com.almworks.integers.segmented;

public class TestEnvForLongSegmentedArray implements LongSegmentedArrayEnvironment {
  private final LongSegment myCache[] = new LongSegment[1024];
  private int myLast = -1;

  public int allocateCount;
  public int allocateSize;
  public int freeCount;
  public int freeSize;
  public int copied;

  public LongSegment allocate(int size) {
    allocateCount++;
    allocateSize += size;
    if (size != 1024 || myLast < 0)
      return new LongSegment(size);
    LongSegment r = myCache[myLast];
    myCache[myLast--] = null;
    return r;
  }

  public void free(LongSegment object) {
    freeCount++;
    freeSize += object.getSize();
    if (myLast < myCache.length - 1 && object.getSize() == 1024)
      myCache[++myLast] = object;
  }

  public LongSegments allocateSegments(int size) {
    return new LongSegments(size);
  }

  public void free(LongSegments segments) {
  }

  public void copy(long[] source, int sourceOffset, long[] destination, int destinationOffset, int length) {
    System.arraycopy(source, sourceOffset, destination, destinationOffset, length);
    copied += length;
  }

  public void clear() {
    allocateCount = 0;
    allocateSize = 0;
    freeCount = 0;
    freeSize = 0;
    copied = 0;
  }
}
