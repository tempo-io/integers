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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class WritableLongSetPerformanceComparison extends IntegersFixture {

  public static interface SetCreator {
    WritableLongSet invoke(LongList values);
    String toString();
  }

  public static class DynamicLongSetCreator implements SetCreator {
    public WritableLongSet invoke(LongList values) {
      return DynamicLongSet.fromSortedList(values);
    }
    public String toString() { return "DynamicLongSet";}
  }

  public static class AmortizedSortedLongSetCreator implements SetCreator {
    public WritableLongSet invoke(LongList values) {
      AmortizedSortedLongSet set = new AmortizedSortedLongSet();
      for (LongIterator it: values.iterator()) {
        set.add(it.value());
      }
      return set;
    }
    public String toString() { return "AmortizedSortedLongSet";}
  }

  public static class AmortizedSortedLongSetTempCreator implements SetCreator {
    public WritableLongSet invoke(LongList values) {
      return new AmortizedSortedLongSetTemp(values);
    }
    public String toString() { return "AmortizedSortedLongSetTemp";}
  }

  public static class TestConfiguration {
    public SetCreator[] setCreators;
    public int testLen;
    public int testNumber, warmUp;
    public int addElementsInBegin;
    // add, remove, contains, tailIterator
    public int[] relationOfQueries;

    // if outputFolder == "", than result will put to System.output
    public String outputFolder;

    public TestConfiguration(SetCreator[] setCreators, int testLen, int testNumber, int warmUp, int addElementsInBegin, int[] relationOfQueries, String outputFolder) {
      this.setCreators = setCreators;
      this.testLen = testLen;
      this.testNumber = testNumber;
      this.warmUp = warmUp;
      this.addElementsInBegin = addElementsInBegin;
      this.relationOfQueries = relationOfQueries;
      this.outputFolder = outputFolder;
    }

    public String toString() {
      StringBuilder res = new StringBuilder("TestConfiguration:\n");
      res.append("  setCreator: [").append(Arrays.toString(setCreators)).append("]\n");
      res.append("  addElementsInBegin: ").append(addElementsInBegin).append("]\n");
      res.append("  relationOfQueries: [").append(Arrays.toString(relationOfQueries)).append("]\n");
      res.append("  testLen, testNumber: ").append(testLen).append("; ").append(testNumber).append("; ").append(warmUp);
      return res.toString();
    }

    public void run() throws IOException {
      PrintStream out = System.out;
      boolean writeToFile = !outputFolder.equals("");
      if (writeToFile) {
        new File(outputFolder).mkdir();
        out = new PrintStream(new File (outputFolder + "/Testtxt"));
        out.println(toString());
      }
      System.out.println(toString());



//      int mergersLen = mergers.length;
//
//      for (int firstSize: firstSizes) {
//        if (writeToFile) {
//          out = new PrintStream(new File (outputFolder + "/" + firstSize + ".txt"));
//        }
//
//        out.print("| firstSize | coef ");
//        for (int type = 0; type < mergersLen; type++) {
//          for (int mod = 0; mod < modificationsLen; mod++) {
//            out.printf("| %s, %s ", mergers[type].toString(), modifications[mod]);
//          }
//        }
//        out.println();
//
//        for (int coef: coefs) {
//          long[] allTimes = new long[mergersLen * modificationsLen];
//          for (int i = 0; i < testNumber + warmUp; i++) {
//            LongArray[] arrays = LongArrayMergePerformanceComparison.getArrays(firstSize, firstSize / coef);
//            for (int type = 0; type < mergersLen; type++) {
//              for (int mod = 0; mod < modificationsLen; mod++) {
//                LongArray[] copy = {LongArray.copy(arrays[0]), LongArray.copy(arrays[1])};
//                long time = runMerge(mergers[type], modifications[mod], copy);
//                if (i > warmUp) {
//                  allTimes[type * modificationsLen + mod] += time;
//                }
//              }
//            }
//          }
//          out.printf("| %d | %d", firstSize, coef);
//          for (long time: allTimes) {
//            out.print("|" + time);
//          }
//          out.println();
//        }
//        if (writeToFile) out.close();
//      }
//    }
//
//    public long runMerge(SetCreator merger, MergeModification mergeModification, LongArray[] arrays) {
//      long start = 0;
//      if (mergeModification == MergeModification.REPLACE_COUNT_ENSURE_CAPACITY) {
//        start = System.currentTimeMillis();
//      }
//      if (mergeModification != MergeModification.REALLOC) {
//        arrays[0].ensureCapacity(arrays[0].size() + arrays[1].size() + 10);
//      }
//      if (mergeModification != MergeModification.REPLACE_COUNT_ENSURE_CAPACITY) {
//        start = System.currentTimeMillis();
//      }
//      merger.invoke(arrays[0], arrays[1]);
//      return System.currentTimeMillis() - start;
//    }
    }
  }
}