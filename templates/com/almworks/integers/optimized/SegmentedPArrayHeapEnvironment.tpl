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

package com.almworks.integers.optimized;

public class Segmented#E#ArrayHeapEnvironment implements Segmented#E#ArrayEnvironment {
  public static final Segmented#E#ArrayEnvironment INSTANCE = new Segmented#E#ArrayHeapEnvironment();

  public #E#Segment allocate(int size) {
    return new #E#Segment(size);
  }

  public #E#Segments allocateSegments(int size) {
    return new #E#Segments(size);
  }

  public void free(#E#Segment segment) {
  }

  public void free(#E#Segments segments) {
  }

  public final void copy(#e#[] source, int sourceOffset, #e#[] destination, int destinationOffset, int length) {
    System.arraycopy(source, sourceOffset, destination, destinationOffset, length);
  }
}
