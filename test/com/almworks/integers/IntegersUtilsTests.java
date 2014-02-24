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

package com.almworks.integers;

import com.almworks.integers.func.IntProcedure;

import java.util.HashSet;

public class IntegersUtilsTests extends IntegersFixture {
  public void testPermutationsSimple() {
    IntegersUtils.allPermutations(1, new IntProcedure() {
      @Override
      public void invoke(int a) {
        fail();
      }
    });
    IntegersUtils.allPermutations(2, new IntProcedure() {
      boolean beenThere = false;

      @Override
      public void invoke(int a) {
        assertFalse(beenThere);
        assertEquals(0, a);
        beenThere = true;
      }
    });
    final IntArray is = IntArray.create(1, 2, 3, 4);
    IntegersDebug.println("0 " + is);
    IntegersUtils.allPermutations(4, new IntProcedure() {
      int count = 1;

      @Override
      public void invoke(int i) {
        assertTrue("" + i, i >= 0);
        assertTrue("" + i, i < 3);
        is.swap(i, i + 1);
        IntegersDebug.println(count++ + " " + is);
      }
    });
  }

  public void testAllPermutationsOf8() {
    final char c[] = "12345678".toCharArray();
    final HashSet<String> perms = new HashSet<String>();
    perms.add(new String(c));
    final int[] permCount = new int[] {1};
    IntegersUtils.allPermutations(c.length, new IntProcedure() {
      @Override
      public void invoke(int swapIdx) {
        permCount[0] += 1;
        assertTrue(swapIdx >= 0 && swapIdx < c.length - 1);
        char t = c[swapIdx];
        c[swapIdx] = c[swapIdx + 1];
        c[swapIdx + 1] = t;
        String perm = new String(c);
        assertTrue(permCount[0] + "", perms.add(perm));
      }
    });
    // Check that permCount is 8!
    for (int i = 2; i <= c.length; ++i) {
      assertEquals(permCount[0] + " " + i, 0, permCount[0] % i);
    }
  }

  public void testReversePerm() {
    final IntArray perms = IntArray.copy(new IntProgression.ArithmeticIterator(0, 9, 1));
    IntegersUtils.allPermutations(perms.size(), new IntProcedure() {
      {
        testRevPerm(perms);
      }

      @Override
      public void invoke(int i) {
        perms.swap(i, i + 1);
        testRevPerm(perms);
      }
    });
  }

  private static void testRevPerm(IntArray perms) {
    IntArray rev = new IntArray(perms);
    IntegersUtils.reversePerm(rev);
    int n = perms.size();
    IntArray e = new IntArray(n);
    for (int i = 0; i < n; ++i) {
      e.add(perms.get(rev.get(i)));
    }
    CHECK.order(IntProgression.arithmetic(0, n).iterator(), e.iterator());
  }


  public void testArrayCopy() {
    int arrayLength = 1000;
    int maxValue = Integer.MAX_VALUE;

    int[] res = new int[arrayLength];
    int[] expected = new int[arrayLength];
    for (int i = 0; i < arrayLength; i++) {
      res[i] = RAND.nextInt(maxValue);
      expected[i] = res[i];
    }
    int[] copy = IntegersUtils.arrayCopy(res);
    CHECK.order(expected, copy);
  }

  public void testIndexOf() {
    int arrayLength = 1000;
    int maxValue = Integer.MAX_VALUE;

    IntArray res = new IntArray(arrayLength);
    int[] intArr = new int[arrayLength];

    for (int i = 0; i < arrayLength; i++) {
      int val = RAND.nextInt(maxValue);
      res.add(val);
      intArr[i] = val;
    }

    for (int i = 0; i < arrayLength; i++) {
      int val = res.get(i);
      assertEquals(res.indexOf(val), IntegersUtils.indexOf(intArr, val));
    }
  }

  public void testSubstringAfterLast() {
    assertEquals("b", IntegersUtils.substringAfterLast("a b", " "));
    assertEquals("", IntegersUtils.substringAfterLast("a ", " "));
  }
}
