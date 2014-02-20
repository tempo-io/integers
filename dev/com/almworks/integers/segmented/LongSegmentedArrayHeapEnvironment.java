/*
 * Copyright 2010 ALM Works Ltd
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

public class LongSegmentedArrayHeapEnvironment implements LongSegmentedArrayEnvironment {
  public static final LongSegmentedArrayEnvironment INSTANCE = new LongSegmentedArrayHeapEnvironment();

  public LongSegment allocate(int size) {
    return new LongSegment(size);
  }

  public LongSegments allocateSegments(int size) {
    return new LongSegments(size);
  }

  public void free(LongSegment segment) {
  }

  public void free(LongSegments segments) {
  }

  public final void copy(long[] source, int sourceOffset, long[] destination, int destinationOffset, int length) {
    System.arraycopy(source, sourceOffset, destination, destinationOffset, length);
  }
}
