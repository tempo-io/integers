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

import com.almworks.integers.util.AmortizedSortedLongSet;
import com.almworks.integers.util.AmortizedSortedLongSetTemp;
import com.almworks.integers.util.FindingLongIterator;
import com.almworks.integers.util.TreeSetWrapper;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

public class WritableLongSetPerformanceComparison extends IntegersFixture {

  public static interface SetCreator {
    WritableLongSet get(LongList values);
    String toString();
  }

  public static class DynamicLongSetCreator implements SetCreator {
    public WritableLongSet get(LongList values) {
      return DynamicLongSet.fromSortedList(values);
    }
    public String toString() { return "DynamicLongSet";}
  }

  public static class AmortizedSortedLongSetCreator implements SetCreator {
    public WritableLongSet get(LongList values) {
      AmortizedSortedLongSet set = new AmortizedSortedLongSet();
      for (LongIterator it: values.iterator()) {
        set.add(it.value());
      }
      return set;
    }
    public String toString() { return "AmortizedSortedLongSet";}
  }

  public class AmortizedSortedLongSetTempCreator implements SetCreator {
    public WritableLongSet get(LongList values) {
      return AmortizedSortedLongSetTemp.fromSortedList(values);
    }
    public String toString() { return "AmortizedSortedLongSetTemp";}
  }

  public class StandartTreeSetCreator implements SetCreator {
    public WritableLongSet get(LongList values) {
      return new TreeSetWrapper(values);
    }
    public String toString() { return "TreeSet";}
  }

  public static class TestConfiguration {
    public static final int ACTIONS_COUNT = 4;
    private SetCreator[] setCreators;
    private int testLen, warmUp;
    private int testNumber;
    private int addElementsInBegin;
    private int maxIssueBits, maxStructureBits;
    // add, remove, contains, tailIterator
    public int[] relationOfQueries;
    public int[] sumRelationOfQueries;
    private int queriesSum;
    private long mask;

    public TestConfiguration(SetCreator[] setCreators, int testLen, int testNumber, int warmUp, int addElementsInBegin, int maxIssueBits, int maxStructureBits) {
      this.setCreators = setCreators;
      this.testLen = testLen;
      this.warmUp = warmUp;
      this.testNumber = testNumber;
      this.addElementsInBegin = addElementsInBegin;
      this.maxIssueBits = maxIssueBits;
      this.maxStructureBits = maxStructureBits;
      mask = (1<<maxStructureBits) - 1;
    }

//    public String toString() {
//      StringBuilder res = new StringBuilder("TestConfiguration:\n");
//      res.append("  setCreator: [").append(Arrays.toString(setCreators)).append("]\n");
//      res.append("  addElementsInBegin: ").append(addElementsInBegin).append("]\n");
//      res.append("  relationOfQueries: [").append(Arrays.toString(relationOfQueries)).append("]\n");
//      res.append("  testLen, testNumber: ").append(testLen).append("; ").append(testNumber).append("; ").append(warmUp);
//      return res.toString();
//    }

    private long nextTestParam() {
      long res = (1L * RAND.nextInt(1<<maxIssueBits))<<(maxStructureBits);
      res += RAND.nextInt(1<<maxStructureBits);
//      System.out.println(res);
      return res;
    }

    private int getType() {
      int testType = 0;
      int cur = RAND.nextInt(sumRelationOfQueries[ACTIONS_COUNT - 1]);
      while (sumRelationOfQueries[testType] <= cur) {
        testType++;
      }
      assert testType < ACTIONS_COUNT;
      return testType;
    }

    private long action(WritableLongSet set, int testType, long testParam) {
      long start;
      assert 0 <= testType && testType < ACTIONS_COUNT;
      if (testType == 3) {
        testParam = testParam & mask;
      }
      switch (testType) {
        // add
        case 0:
          start = System.currentTimeMillis();
          set.add(testParam);
          return System.currentTimeMillis() - start;
        // remove
        case 1:
          start = System.currentTimeMillis();
          set.remove(testParam);
          return System.currentTimeMillis() - start;
        // contains
        case 2:
          start = System.currentTimeMillis();
          boolean res = set.contains(testParam);
          return System.currentTimeMillis() - start;
        // tailIterator
        case 3:
          start = System.currentTimeMillis();
          long maxParam = testParam + (1 << maxStructureBits);
          testParam = 0;
          LongIterator it = set.tailIterator(testParam);
          for (long val = maxParam - 1; val < maxParam && it.hasNext(); ) {
            val = it.nextValue();
          }
          return System.currentTimeMillis() - start;
        default:
          assert false : "testType = " + testType;
      }
      return -1;
    }

    public void inicRelationsOfQueries() {
      sumRelationOfQueries = new int[ACTIONS_COUNT];
      sumRelationOfQueries[0] = relationOfQueries[0];
      for (int i = 1; i < ACTIONS_COUNT; i++) {
        sumRelationOfQueries[i] = sumRelationOfQueries[i - 1] + relationOfQueries[i];
      }
    }


    public void run(int[] paramRelationOfQueries) {
      relationOfQueries = paramRelationOfQueries;
      assert relationOfQueries.length == ACTIONS_COUNT;

      inicRelationsOfQueries();
      WritableLongSet sets[] = new WritableLongSet[setCreators.length];
      long[] times = new long[sets.length];
      int totalLen = testLen + warmUp;
      int[] testTypes = new int[totalLen];
      long[] testParams = new long[totalLen];

      for (int tN = 0; tN < testNumber; tN++) {
        for (int i = 0; i < totalLen; i++) {
          testTypes[i] = getType();
          testParams[i] = nextTestParam();
        }

        LongArray elementsForAdd = new LongArray(addElementsInBegin);
        for (int i = 0; i < addElementsInBegin; i++) {
          elementsForAdd.add(nextTestParam());
        }
        elementsForAdd.sortUnique();

        for (int i = 0; i < setCreators.length; i++) {
          sets[i] = setCreators[i].get(elementsForAdd);
        }

        for (int i = 0; i < sets.length; i++) {
          for (int j = 0; j < warmUp; j++) {
            action(sets[i], testTypes[j], testParams[j]);
          }
          for (int j = warmUp; j < totalLen; j++) {
            times[i] += action(sets[i], testTypes[j], testParams[j]);
          }
        }
      }
      for (int i = 0; i < setCreators.length; i++) {
        System.out.printf("| %29s | %6d |\n", setCreators[i].toString(), times[i]);
      }
    }
  }

  public void testSets() {
    SetCreator[] all = new SetCreator[]{
//        new AmortizedSortedLongSetCreator(),
        new AmortizedSortedLongSetTempCreator(),
//        new DynamicLongSetCreator(),
        new StandartTreeSetCreator(),
    };
//    int[] standartRelationsOfQueries = {1, 1, 50, 25};
    int[] standartRelationsOfQueries = {1, 0, 0, 0};

    TestConfiguration config = new TestConfiguration(all, 100, 5000, 3000, 100000, 14, 14);
    config.run(standartRelationsOfQueries);

//    config = new TestConfiguration(all, 10000, 50, 3000, 1500000, 14, 14);
//    config.run(standartRelationsOfQueries);

//    config = new TestConfiguration(all, 10000, 50, 3000, 2000000, 14, 14);
//    config.run(standartRelationsOfQueries);
  }
}