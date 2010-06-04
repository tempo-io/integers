package com.almworks.integers.optimized;

public class TestEnvForSegmentedIntArray implements SegmentedIntArrayEnvironment {
  private final IntSegment myCache[] = new IntSegment[1024];
  private int myLast = -1;

  public int allocateCount;
  public int allocateSize;
  public int freeCount;
  public int freeSize;
  public int copied;

  public IntSegment allocate(int size) {
    allocateCount++;
    allocateSize += size;
    if (size != 1024 || myLast < 0)
      return new IntSegment(size);
    IntSegment r = myCache[myLast];
    myCache[myLast--] = null;
    return r;
  }

  public void free(IntSegment object) {
    freeCount++;
    freeSize += object.getSize();
    if (myLast < myCache.length - 1 && object.getSize() == 1024)
      myCache[++myLast] = object;
  }

  public IntSegments allocateSegments(int size) {
    return new IntSegments(size);
  }

  public void free(IntSegments segments) {
  }

  public void copy(int[] source, int sourceOffset, int[] destination, int destinationOffset, int length) {
    System.arraycopy(source, sourceOffset, destination, destinationOffset, length);
    copied += length;
  }

  public void clear() {
    allocateCount = 0;
    allocateSize = 0;
    freeCount = 0;
    freeSize = 0;
    copied = 0;
  }
}
