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

public class #E#Segments {

  /**
   * Holds segments, possibly nulls.
   */
  final #E#Segment[] segments;

  /**
   * See {@link #E#Segment#refCount}.
   */
  int refCount;

  public #E#Segments(int size) {
    segments = new #E#Segment[size];
  }

  public int getSize() {
    return segments.length;
  }

  public String toString() {
    return "SEGS[" + (segments == null ? -1 : segments.length) + "]@" + refCount;
  }
}
