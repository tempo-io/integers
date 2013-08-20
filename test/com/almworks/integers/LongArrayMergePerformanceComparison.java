package com.almworks.integers;

import java.io.*;
import java.util.Arrays;

public class LongArrayMergePerformanceComparison extends IntegersFixture {
  public enum MergeModification {
    REALLOC, REPLACE, REPLACE_COUNT_ENSURE_CAPACITY
  }

  MergeType[] mergers = {new MergeUsingSame(), new MergeUsingSmall(), new MergeUsingHeuristic()};
  MergeModification[] mergeModifications = {MergeModification.REALLOC, MergeModification.REPLACE, MergeModification.REPLACE_COUNT_ENSURE_CAPACITY};

  public static interface MergeType {
    void invoke(LongArray first, LongArray second);
    String toString();
  }

  public static class MergeUsingSame implements MergeType {
    public void invoke(LongArray first, LongArray second) {
      first.mergeWithSameLength(second);
    }
    public String toString() { return "Same";}
  }

  public static class MergeUsingSmall implements MergeType {
    public void invoke(LongArray first, LongArray second) {
      first.mergeWithSmall(second);
    }
    public String toString() { return "Small";}
  }

  public static class MergeUsingHeuristic implements MergeType {
    public void invoke(LongArray first, LongArray second) {
      first.merge(second);
    }
    public String toString() { return "Heuristic";}
  }

  public static class TestConfiguration {
    public MergeType[] mergers;
    public MergeModification[] modifications;
    public int[] firstSizes, coefs;
    public int testNumber, warmUp;
    // if outputFolder == "", than result will put to System.output
    public String outputFolder;

    public TestConfiguration(MergeType[] mergers, MergeModification[] modifications, int[] firstSizes, int[] coefs, int testNumber, int warmUp, String outputFolder) {
      this.mergers = mergers;
      this.modifications = modifications;
      this.firstSizes = firstSizes;
      this.coefs = coefs;
      this.testNumber = testNumber;
      this.warmUp = warmUp;
      this.outputFolder = outputFolder;
    }

    public String toString() {
      StringBuilder res = new StringBuilder("TestConfiguration:\n");
      res.append("  mergers: [").append(Arrays.toString(mergers)).append("]\n");
      res.append("  modifications: [").append(Arrays.toString(modifications)).append("]\n");
      res.append("  firstSizes: [").append(Arrays.toString(firstSizes)).append("]\n");
      res.append("  coefs: [").append(Arrays.toString(coefs)).append("]\n");
      res.append("  testNumber: ").append(testNumber).append("; ").append(warmUp);
      return res.toString();
    }
  }

  public long runMerge(MergeType merger, MergeModification mergeModification, LongArray[] arrays) {
    long start = 0;
    if (mergeModification == MergeModification.REPLACE_COUNT_ENSURE_CAPACITY) {
      start = System.currentTimeMillis();
    }
    if (mergeModification != MergeModification.REALLOC) {
      arrays[0].ensureCapacity(arrays[0].size() + arrays[1].size() + 10);
    }
    if (mergeModification != MergeModification.REPLACE_COUNT_ENSURE_CAPACITY) {
      start = System.currentTimeMillis();
    }
    merger.invoke(arrays[0], arrays[1]);
    return System.currentTimeMillis() - start;
  }

  protected int[] intAp(int start, int step, int count) {
    int[] r = new int[count];
    for (int i = 0; i < r.length; i++) {
      r[i] = start + step * i;
    }
    return r;
  }

  public LongArray[] getArrays(int ... sizes) {
    LongArray[] res = new LongArray[sizes.length];
    for (int i = 0; i < sizes.length; i++) {
      res[i] = new LongArray(sizes[i]);
      for (int j = 0; j < sizes[i]; j++) {
        res[i].add(RAND.nextInt());
      }
      res[i].sortUnique();
    }
    return res;
  }

  public void testBenchMerge(TestConfiguration configuration) throws IOException{
    PrintStream out = System.out;
    boolean writeToFile = !configuration.outputFolder.equals("");
    if (writeToFile) {
      new File(configuration.outputFolder).mkdir();
      out = new PrintStream(new File (configuration.outputFolder + "/TestConfiguration.txt"));
      out.println(configuration);
    }
    System.out.println(configuration);

    int mergersLen = configuration.mergers.length;
    int modificationsLen = configuration.modifications.length;

    for (int firstSize: configuration.firstSizes) {
      if (writeToFile) {
        out = new PrintStream(new File (configuration.outputFolder + "/" + firstSize + ".txt"));
      }

      out.print("| firstSize | coef ");
      for (int type = 0; type < mergersLen; type++) {
        for (int mod = 0; mod < modificationsLen; mod++) {
          out.printf("| %s, %s ", configuration.mergers[type].toString(), configuration.modifications[mod]);
        }
      }
      out.println();

      for (int coef: configuration.coefs) {

        long[] allTimes = new long[mergersLen * modificationsLen];
        for (int i = 0; i < configuration.testNumber + configuration.warmUp; i++) {
          LongArray[] arrays = getArrays(firstSize, firstSize / coef);
          for (int type = 0; type < mergersLen; type++) {
            for (int mod = 0; mod < modificationsLen; mod++) {
              LongArray[] copy = {LongArray.copy(arrays[0]), LongArray.copy(arrays[1])};
              long time = runMerge(configuration.mergers[type], configuration.modifications[mod], copy);
              if (i > configuration.warmUp) {
                allTimes[type * modificationsLen + mod] += time;
              }
            }
          }
        }
        out.printf("| %d | %d", firstSize, coef);
        for (long time: allTimes) {
          out.print("|" + time);
        }
        out.println();
      }
      if (writeToFile) out.close();
    }
  }

  public void testBenchMerge() throws IOException {
    final MergeType[] SameAndSmall = {mergers[0], mergers[1]};
    final MergeModification[] reallocModification = {MergeModification.REALLOC};
    final MergeModification[] replaceModification = {MergeModification.REPLACE};

    TestConfiguration config = new TestConfiguration(
        SameAndSmall,
        reallocModification,
        intAp(10000, 5000, 18), intAp(10, 1, 15), 5000, 100, "benchmark realloc, firstSize max 100000");
    testBenchMerge(config);

    config = new TestConfiguration(
        SameAndSmall,
        reallocModification,
        intAp(100000, 100000, 3), intAp(12, 1, 13), 100, 5, "benchmark realloc, firstSize max 400000");
    testBenchMerge(config);

    config = new TestConfiguration(
        SameAndSmall,
        reallocModification,
        intAp(500000, 100000, 6), intAp(12, 1, 13), 50, 5, "benchmark realloc, firstSize max 1000000");
    testBenchMerge(config);
    config = new TestConfiguration(
        SameAndSmall,
        replaceModification,
        intAp(10000, 5000, 18), intAp(3, 1, 7), 500, 20, "benchmark replace, firstSize max 100000");
    testBenchMerge(config);

    config = new TestConfiguration(
        SameAndSmall,
        replaceModification,
        intAp(100000, 100000, 3), intAp(3, 1, 7), 100, 5, "benchmark replace, firstSize max 400000");
    testBenchMerge(config);

    config = new TestConfiguration(
        SameAndSmall,
        replaceModification,
        intAp(500000, 100000, 6), intAp(3, 1, 7), 50, 5, "benchmark replace, firstSize max 1000000");
    testBenchMerge(config);
  }

  public void test() throws IOException {
    new File("op").mkdirs();

    PrintStream out = new PrintStream(new File ("Engineer.txt"));
    out.println("hw!");
    out.close();

    PrintStream sout = System.out;
    sout.print("hello!");
  }
}
