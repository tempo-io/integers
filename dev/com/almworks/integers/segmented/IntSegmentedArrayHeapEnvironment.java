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

// CODE GENERATED FROM com/almworks/integers/segmented/PSegmentedArrayHeapEnvironment.tpl




package com.almworks.integers.segmented;

public class IntSegmentedArrayHeapEnvironment implements IntSegmentedArrayEnvironment {
  public static final IntSegmentedArrayEnvironment INSTANCE = new IntSegmentedArrayHeapEnvironment();

  public IntSegment allocate(int size) {
    return new IntSegment(size);
  }

  public IntSegments allocateSegments(int size) {
    return new IntSegments(size);
  }

  public void free(IntSegment segment) {
  }

  public void free(IntSegments segments) {
  }

  public final void copy(int[] source, int sourceOffset, int[] destination, int destinationOffset, int length) {
    System.arraycopy(source, sourceOffset, destination, destinationOffset, length);
  }
}
