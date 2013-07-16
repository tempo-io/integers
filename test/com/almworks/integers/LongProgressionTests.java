/*
 * Copyright 2013 ALM Works Ltd
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

package com.almworks.integers;

public class LongProgressionTests extends IntegersFixture {
  public void testCreator() {
    IntProgression.Arithmetic progression = new IntProgression.Arithmetic(0, 5, 2);

    IntProgression progression2 = IntProgression.arithmetic(0, 5, 2);
//    progression2.a

    IntList expected = IntArray.create(0, 2, 4, 6, 8);
    CHECK.order(expected.iterator(), progression.iterator());
  }

  public void testFillArray() {
    int[] res = IntProgression.Arithmetic.fillArray(0, 2, 5);
    long[] expected = {0, 2, 4, 6, 8};
//    CHECK.order(res, expected);
  }

  public void testIndexOf() {
    IntProgression.Arithmetic progression = new IntProgression.Arithmetic(0, 5, 2);
    for( int i = 0; i < 5; i++) {
      assertEquals(i, progression.indexOf(i * 2));
    }
    assertEquals(-1, progression.indexOf(-2));
    assertEquals(-1, progression.indexOf(3));
    assertEquals(-1, progression.indexOf(10));
  }

}
