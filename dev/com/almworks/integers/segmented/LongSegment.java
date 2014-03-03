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

// CODE GENERATED FROM com/almworks/integers/segmented/PSegment.tpl




package com.almworks.integers.segmented;

public class LongSegment {
  /**
   * Holds array with the data. Some cells may not hold meaningful data.
   * It is up to the using class to track data size.
   */
  final long[] data;

  /**
   * Holds the number of uses in aggregate structures. When >1, the modifying
   * code must make a private copy and apply modifications there.
   */
  int refCount;

  public LongSegment(int size) {
    data = new long[size];
  }

  public int getSize() {
    return data.length;
  }

  public String toString() {
    return "INTS[" + (data == null ? -1 : data.length) + "]@" + refCount;
  }
}
