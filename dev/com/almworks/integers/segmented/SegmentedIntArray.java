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

import com.almworks.integers.*;
import com.almworks.integers.func.IntIntToInt;
import com.almworks.integers.func.IntToInt;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class SegmentedIntArray extends AbstractWritableIntList implements Cloneable {
  private static final int SEGB_INITIAL = 4;
  private static final int SEGS_INITIAL = 1 << SEGB_INITIAL;
  private static final int SEGB_LARGE = 10;
  private static final int SEGS_LARGE = 1 << SEGB_LARGE;

  private final SegmentedIntArrayEnvironment myEnv;

  /**
   * List of segments holding the data. All segments are of equal size.
   */
  private IntSegments mySegments;

  /**
   * Number of allocated segments. The length of the array in mySegments may differ.
   */
  private int mySegmentCount;

  /**
   * Holds currently available capacity.
   * <p/>
   * myCapacity === mySegmentSize * number of allocated segments
   */
  private int myCapacity;


  /**
   * The number of free cells to the left of the array (The index of the first element in the first segment)
   */
  private int myLeftOffset;

  /**
   * The number of free cells to the right of the array
   */
  private int myRightOffset;


  /**
   * Segment size, in various forms.
   * <p/>
   * mySegmentSize === 1 << mySegmentBits
   * mySegmentMask === mySegmentSize - 1
   * <p/>
   * for usage, see {@link #writeInt(int, int)}
   */
  private int mySegmentBits = SEGB_INITIAL;
  private int mySegmentSize = SEGS_INITIAL;
  private int mySegmentMask = SEGS_INITIAL - 1;


  final boolean checkInvariants() {
    assert mySegmentBits >= SEGB_INITIAL && mySegmentBits <= SEGB_LARGE : mySegmentBits;
    assert mySegmentSize == 1 << mySegmentBits : mySegmentBits + " " + mySegmentSize;
    assert mySegmentMask == mySegmentSize - 1 : mySegmentSize + " " + mySegmentMask;
    assert myLeftOffset >= 0 && myLeftOffset < mySegmentSize : myLeftOffset + " " + mySegmentSize;
    assert myRightOffset >= 0 && myRightOffset < mySegmentSize : myRightOffset + " " + mySegmentSize;
    int size = size();
    assert mySegments != null || size == 0 : size + " " + mySegments;
    assert mySegments == null || mySegments.segments != null : mySegments;
    assert
      mySegmentCount >= 0 && (mySegmentCount == 0 || mySegmentCount <= mySegments.segments.length) :
      mySegmentCount + " " + mySegments;
    assert myCapacity == mySegmentCount << mySegmentBits : mySegmentBits + " " + mySegmentCount + " " + myCapacity;
    assert size <= myCapacity : size + " " + myCapacity;
    assert
      size + myLeftOffset + myRightOffset == myCapacity :
      size + " " + myLeftOffset + " " + myRightOffset + " " + myCapacity;
    if (mySegments != null) {
      assert mySegments.refCount > 0 : mySegments;
      if (mySegments.segments != null) {
        for (int i = 0; i < mySegments.segments.length; i++) {
          IntSegment seg = mySegments.segments[i];
          if (i >= mySegmentCount) {
            assert seg == null : mySegmentCount + " " + i + " " + seg;
          } else {
            assert seg != null : mySegmentCount + " " + i;
            assert seg.data != null : mySegmentCount + " " + i + " " + seg;
            assert seg.data.length == mySegmentSize : mySegmentCount + " " + i + " " + seg;
            assert seg.refCount > 0 : mySegmentCount + " " + i + " " + seg;
          }
        }
      }
    }
    return true;
  }


  public SegmentedIntArray() {
    this(SegmentedIntArrayHeapEnvironment.INSTANCE);
  }

  public SegmentedIntArray(SegmentedIntArrayEnvironment env) {
    myEnv = env;
    assert checkInvariants();
  }

  public int get(int index) {
    assert checkInvariants();
    try {
      index += myLeftOffset;
      IntSegment s = mySegments.segments[index >> mySegmentBits];
      return s.data[index & mySegmentMask];
    } catch (NullPointerException e) {
      if (index < 0 || index >= size())
        throw new IndexOutOfBoundsException(index + " " + this);
      else
        throw e;
    }
  }

  public void add(int value) {
    assert checkInvariants();
    int p = size();
    increaseSize(1, false);
    writeInt(myLeftOffset + p, value);
    assert checkInvariants();
  }

  public void insert(int index, int value) {
    assert checkInvariants();
    int sz = size();
    if (index < 0 || index > sz)
      throw new IndexOutOfBoundsException(index + " " + this);
    increaseSize(1, index < (sz >> 1));
    writeInt(myLeftOffset + index, value);
    assert checkInvariants();
  }

  @NotNull
  public WritableIntListIterator iterator(int from, int to) {
    if (from >= to) {
      assert from == to : from + " " + to;
      return IntIterator.EMPTY;
    }
    return new SegmentedIterator(from, to);
  }

  public void expand(int index, int count) {
    assert checkInvariants();
    if (index < 0 || index > size())
      throw new IndexOutOfBoundsException(index + " " + this);
    if (count <= 0)
      return;
    doExpand(index, count);
    assert checkInvariants();
  }

  public void insertMultiple(int index, int value, int count) {
    assert checkInvariants();
    if (count <= 0)
      return;
    doExpand(index, count);
    for (WritableIntListIterator ii = iterator(index, index + count); ii.hasNext();) {
      ii.next();
      ii.set(0, value);
    }
    assert checkInvariants();
  }

  public void removeRange(int from, int to) {
    assert checkInvariants();
    if (to <= from)
      return;
    checkRange(from, to);
    int sz = size();
    if (from == 0 && to == sz) {
      clear();
      return;
    }
    boolean leftward = from < (sz - to);
    int count = to - from;
    if (leftward) {
      shiftRight(myLeftOffset, myLeftOffset + from, count);
    } else {
      shiftLeft(myLeftOffset + to, myLeftOffset + sz, count);
    }
    decreaseSize(count, leftward);
    assert checkInvariants();
  }

  public int removeAt(int index) {
    assert checkInvariants();
    int value = get(index);
    int sz = size();
    boolean leftward = index < (sz >> 1);
    if (leftward) {
      shiftRight(myLeftOffset, myLeftOffset + index, 1);
    } else {
      shiftLeft(myLeftOffset + index + 1, myLeftOffset + sz, 1);
    }
    decreaseSize(1, leftward);
    assert checkInvariants();
    return value;
  }

  public void addAll(IntList values) {
    insertAll(size(), values);
  }

  public void insertAll(int index, IntList values, int sourceIndex, int count) {
    if (values == null || count <= 0)
      return;
    if (values instanceof SegmentedIntArray) {
      insertSegmented(index, (SegmentedIntArray) values, sourceIndex, count);
    } else if (values instanceof SubList) {
      SubList sublist = (SubList) values;
      if (sourceIndex + count > sublist.size())
        throw new IndexOutOfBoundsException(sourceIndex + " " + count + " " + values);
      insertAll(index, sublist.getParent(), sourceIndex + sublist.getFrom(), count);
    } else {
      insertList(index, values, sourceIndex, count);
    }
  }

  public void setAll(int index, IntList values, int sourceIndex, int count) {
    assert checkInvariants();
    if (values == null || count <= 0)
      return;
    if (values instanceof SegmentedIntArray) {
      copySegmented(index, (SegmentedIntArray) values, sourceIndex, count);
    } else if (values instanceof SubList) {
      SubList sublist = (SubList) values;
      if (sourceIndex + count > sublist.size())
        throw new IndexOutOfBoundsException(sourceIndex + " " + count + " " + values);
      setAll(index, sublist.getParent(), sourceIndex + sublist.getFrom(), count);
    } else {
      copyList(index, values, sourceIndex, count);
    }
    assert checkInvariants();
  }

  public int[] toArray(int startIndex, int[] dest, int destOffset, int length) {
    if (length <= 0)
      return dest;
    assert checkInvariants();
    if (startIndex < 0 || startIndex + length > size())
      throw new IndexOutOfBoundsException(startIndex + " " + length + " " + this);
    int sp = myLeftOffset + startIndex;
    int si = sp >> mySegmentBits;
    sp &= mySegmentMask;
    int copied = 0;
    while (true) {
      int len = Math.min(length - copied, mySegmentSize - sp);
      myEnv.copy(mySegments.segments[si].data, sp, dest, destOffset + copied, len);
      copied += len;
      if (copied >= length) {
        assert copied == length : copied + " " + length;
        break;
      }
      assert sp + len == mySegmentSize : sp + " " + len + " " + copied + " " + length;
      sp = 0;
      si++;
    }
    return dest;
  }

  public SegmentedIntArray clone() {
    return clone(0, size());
  }

  public SegmentedIntArray clone(int from, int to) {
    assert checkInvariants();
    checkRange(from, to);
    if (from >= to)
      return new SegmentedIntArray(myEnv);
    try {
      assert mySegmentCount > 0 : this;
      SegmentedIntArray r = (SegmentedIntArray) super.clone();
      r.updateSize(to - from);

      int left = myLeftOffset + from;
      int right = myLeftOffset + to;
      int startSegment = left >> mySegmentBits;
      int endSegment = (right - 1) >> mySegmentBits;
      r.mySegmentCount = endSegment - startSegment + 1;
      if (startSegment == 0 && r.mySegmentCount == mySegmentCount) {
        // segment array also copied
        r.mySegments.refCount++;
        r.myLeftOffset = left;
      } else {
        r.myCapacity = r.mySegmentCount << r.mySegmentBits;
        r.mySegments = r.newSegments(r.mySegmentCount);
        System.arraycopy(mySegments.segments, startSegment, r.mySegments.segments, 0, r.mySegmentCount);
        r.myLeftOffset = left & r.mySegmentMask;
      }
      for (int i = 0; i < r.mySegmentCount; i++) {
        r.use(r.mySegments.segments[i]);
      }
      r.myRightOffset = r.myCapacity - r.size() - r.myLeftOffset;
      assert checkInvariants();
      assert r.checkInvariants();
      return r;
    } catch (CloneNotSupportedException e) {
      throw new Error(e);
    }
  }

  private void insertList(int index, IntList list, int sourceIndex, int count) {
    assert checkInvariants();
    assert list != null && !list.isEmpty() && count > 0 && count <= list.size();
    doExpand(index, count);
    copyList(index, list, sourceIndex, count);
    assert checkInvariants();
  }

  private void copyList(int targetIndex, IntList list, int sourceIndex, int count) {
    int dp = myLeftOffset + targetIndex;
    int di = dp >> mySegmentBits;
    dp &= mySegmentMask;
    for (int i = 0; i < count;) {
      IntSegment seg = modify(di);
      int len = Math.min(count - i, mySegmentSize - dp);
      list.toArray(sourceIndex + i, seg.data, dp, len);
      i += len;
      dp = 0;
      di++;
    }
  }

  private void insertSegmented(int index, SegmentedIntArray array, int sourceIndex, int count) {
    assert checkInvariants();
    assert array != null && !array.isEmpty() && count > 0 && count <= array.size() : count + " " + array;
    // todo if segments are copied as whole, we don't need expand to allocate new segments
    doExpand(index, count);
    copySegmented(index, array, sourceIndex, count);
    assert checkInvariants();
  }

  private void copySegmented(int targetIndex, SegmentedIntArray array, int sourceIndex, int length) {
    int dp = myLeftOffset + targetIndex;
    int di = dp >> mySegmentBits;
    dp &= mySegmentMask;
    int sp = array.myLeftOffset + sourceIndex;
    int si = sp >> array.mySegmentBits;
    sp &= array.mySegmentMask;
    int copied = 0;
    while (true) {
      int len = Math.min(length - copied, Math.min(mySegmentSize - dp, array.mySegmentSize - sp));
      if (len == mySegmentSize && len == array.mySegmentSize) {
        // copy whole segment
        setSegment(di, array.mySegments.segments[si]);
      } else {
        IntSegment seg = modify(di);
        myEnv.copy(array.mySegments.segments[si].data, sp, seg.data, dp, len);
      }
      copied += len;
      if (copied >= length) {
        assert copied == length : copied + " " + length;
        break;
      }
      dp += len;
      if (dp >= mySegmentSize) {
        assert dp == mySegmentSize : dp + " " + mySegmentSize;
        di += dp >> mySegmentBits;
        dp &= mySegmentMask;
      }
      sp += len;
      if (sp >= array.mySegmentSize) {
        assert sp == array.mySegmentSize : sp + " " + array.mySegmentSize;
        si += sp >> array.mySegmentBits;
        sp &= array.mySegmentMask;
      }
    }
  }

  private void doExpand(int index, int count) {
    boolean leftward = isLeftwardExpand(index, count);
    if (leftward) {
      increaseSize(count, true);
      int shiftFrom = myLeftOffset + count;
      int shiftTo = shiftFrom + index;
      shiftLeft(shiftFrom, shiftTo, count);
    } else {
      // before size has changed
      int shiftFrom = myLeftOffset + index;
      int shiftTo = myLeftOffset + size();
      increaseSize(count, false);
      shiftRight(shiftFrom, shiftTo, count);
    }
  }


  private boolean isLeftwardExpand(int from, int count) {
    int sz = size();
    if (from == sz || mySegmentSize < SEGS_LARGE)
      return false;
    if (from == 0)
      return true;

    // number of blocks to be allocated when shifting left
    int leftAllocate = count <= myLeftOffset ? 0 : ((count - myLeftOffset - 1) >> mySegmentBits) + 1;

    // number of blocks to be allocated when shifting right
    // todo use another field for right offset?
    int si = (myLeftOffset + sz) & mySegmentMask;
    int rightOffset = si == 0 ? 0 : mySegmentSize - si;
    int rightAllocate = count <= rightOffset ? 0 : ((count - rightOffset - 1) >> mySegmentBits) + 1;

    // number of blocks to be affected (moving ints)
    int fromBlock = (from + myLeftOffset) >> mySegmentBits;
    int leftMove = fromBlock + 1;
    int rightMove = mySegmentCount - fromBlock;

    // heuristics for choosing left- or right- insert
    boolean leftward;
    if (leftMove < rightMove) {
      leftward = true;
    } else if (leftMove > rightMove) {
      leftward = false;
    } else {
      leftward = leftAllocate < rightAllocate;
    }
    return leftward;
  }

  private void shiftRight(int absfrom, int absto, int count) {
    if (count <= 0 || absfrom >= absto)
      return;
    int startSegment = absfrom >> mySegmentBits;
    int source = absto;
    int target = source + count;
    assert target <= myCapacity : target + " " + myCapacity;
    while (source > absfrom) {
      int sourceSegment = (source - 1) >> mySegmentBits;
      int sourceOffset = ((source - 1) & mySegmentMask) + 1;
      int targetSegment = (target - 1) >> mySegmentBits;
      int targetOffset = ((target - 1) & mySegmentMask) + 1;
      int len = Math.min(targetOffset, sourceOffset);
      if (sourceSegment == startSegment && len > source - absfrom)
        len = source - absfrom;
      if (len == mySegmentSize) {
        assert sourceSegment != targetSegment : sourceSegment + " " + targetSegment;
        modifySegments();
        // move the whole source segment onto target's segment place - no in-segment modification is needed
        IntegersUtils.swap(mySegments.segments, targetSegment, sourceSegment);
      } else {
        IntSegment tseg = modify(targetSegment);
        IntSegment sseg = mySegments.segments[sourceSegment];
        myEnv.copy(sseg.data, sourceOffset - len, tseg.data, targetOffset - len, len);
      }
      source -= len;
      target -= len;
    }
  }

  private void shiftLeft(int absfrom, int absto, int count) {
    if (count <= 0 || absfrom >= absto)
      return;
    int startSegment = (absto - 1) >> mySegmentBits;
    int source = absfrom;
    int target = source - count;
    assert target >= 0 : target;
    while (source < absto) {
      int sourceSegment = source >> mySegmentBits;
      int sourceOffset = source & mySegmentMask;
      int targetSegment = target >> mySegmentBits;
      int targetOffset = target & mySegmentMask;
      int len = mySegmentSize - Math.max(targetOffset, sourceOffset);
      if (sourceSegment == startSegment && len > absto - source)
        len = absto - source;
      if (len == mySegmentSize) {
        assert sourceSegment != targetSegment : sourceSegment + " " + targetSegment;
        modifySegments();
        // move the whole source segment onto target's segment place - no in-segment modification is needed
        IntegersUtils.swap(mySegments.segments, targetSegment, sourceSegment);
      } else {
        IntSegment tseg = modify(targetSegment);
        IntSegment sseg = mySegments.segments[sourceSegment];
        myEnv.copy(sseg.data, sourceOffset, tseg.data, targetOffset, len);
      }
      source += len;
      target += len;
    }
  }

  private void decreaseSize(int count, boolean leftward) {
    int sz = size();
    assert count <= sz : count + " " + sz;
    int newOffset = (leftward ? myLeftOffset : myRightOffset) + count;
    int segmentsFreed = newOffset >> mySegmentBits;
    if (segmentsFreed > 0) {
      for (int i = 0; i < segmentsFreed; i++) {
        setSegment(leftward ? i : mySegmentCount - 1 - i, null);
      }
      if (leftward && segmentsFreed < mySegmentCount) {
        int len = mySegmentCount - segmentsFreed;
        System.arraycopy(mySegments.segments, segmentsFreed, mySegments.segments, 0, len);
        Arrays.fill(mySegments.segments, len, mySegmentCount, null);
      }
      mySegmentCount -= segmentsFreed;
      myCapacity -= (segmentsFreed << mySegmentBits);
      newOffset &= mySegmentMask;
    }
    if (leftward)
      myLeftOffset = newOffset;
    else
      myRightOffset = newOffset;
    updateSize(sz - count);
  }

  private void increaseSize(int added, boolean leftward) {
    assert checkInvariants();
    int relatedOffset = leftward ? myLeftOffset : myRightOffset;
    int sz = size();
    if (added > relatedOffset) {
      int oppositeOffset = leftward ? myRightOffset : myLeftOffset;
      if (mySegmentCount <= 1 && mySegmentSize < SEGS_LARGE && oppositeOffset + sz + added <= SEGS_LARGE) {
        ensureFreeSpaceSmall(added, leftward);
      } else {
        if (mySegmentSize < SEGS_LARGE) {
          mySegmentBits = SEGB_LARGE;
          mySegmentSize = SEGS_LARGE;
          mySegmentMask = SEGS_LARGE - 1;
          expandSingleSegment(leftward);
          assert added > relatedOffset : leftward + " " + relatedOffset + " " + added;
        }
        int allocateCount = ((added - relatedOffset - 1) >> mySegmentBits) + 1;
        allocateSegments(allocateCount, leftward);
        myCapacity += allocateCount << mySegmentBits;
      }
    }
    sz += added;
    updateSize(sz);
    if (leftward) {
      myLeftOffset = myCapacity - sz - myRightOffset;
    } else {
      myRightOffset = myCapacity - sz - myLeftOffset;
    }
    assert checkInvariants();
  }

  private void ensureFreeSpaceSmall(int required, boolean leftward) {
    assert myCapacity < SEGS_LARGE : myCapacity;
    int capacity = (leftward ? myRightOffset : myLeftOffset) + size() + required;
    while (mySegmentSize < capacity) {
      mySegmentBits++;
      mySegmentSize <<= 1;
      mySegmentMask = mySegmentSize - 1;
    }
    if (mySegmentCount == 0) {
      assert size() == 0 : size();
      assert myLeftOffset == 0 : myLeftOffset;
      mySegments = myEnv.allocateSegments(1);
      mySegments.refCount++;
      setSegment(0, myEnv.allocate(mySegmentSize));
      myCapacity = mySegmentSize;
      myRightOffset = myCapacity;
      mySegmentCount = 1;
    } else {
      expandSingleSegment(leftward);
    }
  }

  private void expandSingleSegment(boolean leftward) {
    if (mySegmentCount == 1) {
      assert myCapacity < mySegmentSize : myCapacity + " " + mySegmentSize;
      IntSegment s = myEnv.allocate(mySegmentSize);
      IntSegment olds = mySegments.segments[0];
      myCapacity = mySegmentSize;
      int sz = size();
      if (!leftward) {
        myEnv.copy(olds.data, myLeftOffset, s.data, myLeftOffset, sz);
        myRightOffset = myCapacity - sz - myLeftOffset;
      } else {
        myEnv.copy(olds.data, myLeftOffset, s.data, s.data.length - myRightOffset - sz, sz);
        myLeftOffset = myCapacity - sz - myRightOffset;
      }
      setSegment(0, s);
    }
  }

  private void allocateSegments(int allocateCount, boolean leftward) {
    int totalCount = mySegmentCount + allocateCount;
    if (mySegments == null || mySegments.segments.length < totalCount) {
      IntSegments newsegm = newSegments(totalCount);
      if (mySegmentCount > 0) {
        System.arraycopy(mySegments.segments, 0, newsegm.segments, leftward ? allocateCount : 0, mySegmentCount);
      }
      IntSegments oldsegm = mySegments;
      mySegments = newsegm;
      if (oldsegm != null && --oldsegm.refCount == 0) {
        myEnv.free(oldsegm);
      }
    } else {
      if (leftward) {
        modifySegments();
        System.arraycopy(mySegments.segments, 0, mySegments.segments, allocateCount, mySegmentCount);
      }
    }
    for (int i = 0; i < allocateCount; i++) {
      int index = leftward ? i : i + mySegmentCount;
      modifySegments();
      IntSegment s = myEnv.allocate(mySegmentSize);
      use(s);
      mySegments.segments[index] = s;
      // cannot use setSegment, because old segment will be derefcounted
    }
    mySegmentCount = totalCount;
  }

  private IntSegments newSegments(int count) {
    int allocate = SEGS_INITIAL;
    while (allocate < count)
      allocate <<= 1;
    IntSegments r = myEnv.allocateSegments(allocate);
    r.refCount++;
    return r;
  }

  private void modifySegments() {
    if (mySegments == null) {
      assert false;
      return;
    }
    if (mySegments.refCount != 1) {
      assert mySegments.segments.length > 0 : mySegments.segments.length;
      IntSegments olds = mySegments;
      mySegments = myEnv.allocateSegments(mySegments.segments.length);
      mySegments.refCount++;
      if (olds != null) {
        if (mySegmentCount > 0) {
          System.arraycopy(olds.segments, 0, mySegments.segments, 0, mySegmentCount);
        }
        if (--olds.refCount == 0) {
          // was != 1
          assert false : olds;
          myEnv.free(olds);
        }
      }
    }
  }

  public void clear() {
    assert checkInvariants();
    if (mySegments != null) {
      for (int i = 0; i < mySegments.segments.length; i++) {
        unuse(mySegments.segments[i]);
      }
      if (--mySegments.refCount == 0) {
        myEnv.free(mySegments);
      }
      mySegments = null;
    }
    updateSize(0);
    mySegmentCount = 0;
    myCapacity = 0;
    myLeftOffset = 0;
    myRightOffset = 0;
    assert checkInvariants();
  }

  public void set(int index, int value) {
    assert checkInvariants();
    writeInt(myLeftOffset + index, value);
    assert checkInvariants();
  }

  public void setRange(int from, int to, int value) {
    assert checkInvariants();
    for (WritableIntListIterator ii = iterator(from, to); ii.hasNext();) {
      ii.next();
      ii.set(0, value);
    }
    assert checkInvariants();
  }

  private void writeInt(int absIndex, int value) {
    modify(absIndex >> mySegmentBits).data[absIndex & mySegmentMask] = value;
  }

  private IntSegment modify(int segmentIndex) {
    IntSegment s = mySegments.segments[segmentIndex];
    if (s.refCount == 1)
      return s;
    IntSegment newseg = myEnv.allocate(mySegmentSize);
    int L = segmentIndex == 0 ? myLeftOffset : 0;
    int R = segmentIndex == mySegmentCount - 1 ? myRightOffset : 0;
    myEnv.copy(s.data, L, newseg.data, L, s.data.length - L - R);
    return setSegment(segmentIndex, newseg);
  }

  private IntSegment setSegment(int index, IntSegment segment) {
    IntSegment old = mySegments.segments[index];
    if (old != segment) {
      modifySegments();
      mySegments.segments[index] = segment;
      use(segment);
      unuse(old);
    }
    return segment;
  }

  private void use(IntSegment segment) {
    if (segment != null) {
      segment.refCount++;
    }
  }

  private void unuse(IntSegment old) {
    if (old != null) {
      if (--old.refCount == 0) {
        myEnv.free(old);
      }
    }
  }

  public void apply(int from, int to, IntToInt function) {
    assert checkInvariants();
    if (from >= to)
      return;
    checkRange(from, to);
    for (WritableIntListIterator ii = iterator(from, to); ii.hasNext();) {
      ii.set(0, function.invoke(ii.nextValue()));
    }
    assert checkInvariants();
  }

  public void apply(int from, int to, IntIntToInt function, int secondArgument) {
    assert checkInvariants();
    if (from >= to)
      return;
    checkRange(from, to);
    for (WritableIntListIterator ii = iterator(from, to); ii.hasNext();) {
      ii.set(0, function.invoke(ii.nextValue(), secondArgument));
    }
    assert checkInvariants();
  }

  private void checkRange(int from, int to) {
    if (from < 0 || from > size()) {
      throw new ArrayIndexOutOfBoundsException(from);
    }
    if (to < 0 || to > size()) {
      throw new ArrayIndexOutOfBoundsException(to);
    }
  }

  // todo more effective binarySearch
  public int binarySearch(int value, int from, int to) {
    return super.binarySearch(value, from, to);
  }

  // todo more effective
  public void sort(WritableIntList... sortAlso) {
    super.sort(sortAlso);
  }

  // todo more effective
  public void swap(int index1, int index2) {
    super.swap(index1, index2);
  }



  private class SegmentedIterator extends AbstractIntIterator implements WritableIntListIterator {
    private final int myFrom;
    private int myTo;
    private int myNext;
    private int myCurrent;

    /**
     * The segment of the current value
     */
    private int mySegmentIndex;
    private IntSegment mySegment;

    /**
     * The offset of the next value
     */
    private int myOffset;

    private int myIterationModCount = modCount();

    public SegmentedIterator(int from, int to) {
      myFrom = from;
      myTo = to;
      myNext = from;
      int left = from + myLeftOffset;
      mySegmentIndex = left >> mySegmentBits;
      myOffset = (left & mySegmentMask);
      assert checkIterator();
    }

    public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
      assert checkIterator();
      checkMod();
      if (myNext < 0)
        throw new IllegalStateException();
      int p = myNext - 1 + offset;
      if (p < myFrom || p >= myTo)
        throw new NoSuchElementException(offset + " " + this);
      myOffset = myOffset - 1 + offset;
      adjustOffset();
      if (mySegment == null)
        mySegment = mySegments.segments[mySegmentIndex];
      myNext = p + 1;
      myOffset++;
      adjustOffset();
      assert checkIterator();
    }

    public WritableIntListIterator next() throws ConcurrentModificationException, NoSuchElementException {
      assert checkIterator();
      checkMod();
      if (myNext < 0)
        myNext = -myNext-1;
      if (myNext < myFrom || myNext >= myTo)
        throw new NoSuchElementException(String.valueOf(this));
      if (mySegment == null)
        mySegment = mySegments.segments[mySegmentIndex];
      myCurrent = mySegment.data[myOffset];
      myNext++;
      myOffset++;
      adjustOffset();
      assert checkIterator();
      return this;
    }

    public int value() throws NoSuchElementException {
      if (myNext < 0) throw new IllegalStateException();
      if (myNext <= myFrom) throw new NoSuchElementException();
      return myCurrent;
    }

    private void adjustOffset() {
      if (myOffset < 0 || myOffset >= mySegmentSize) {
        mySegment = null;
        if (myOffset < 0) {
          mySegmentIndex -= ((-myOffset - 1) >> mySegmentBits) + 1;
        } else {
          mySegmentIndex += myOffset >> mySegmentBits;
        }
        myOffset &= mySegmentMask;
      }
    }

    public boolean hasNext() {
      return (myNext < 0) ? -myNext-1 < myTo : myNext < myTo;
    }

    public int get(int relativeOffset) throws IndexOutOfBoundsException, NoSuchElementException {
      assert checkIterator();
      checkMod();
      if (myNext < 0)
        throw new IllegalStateException();
      int idx = myNext - 1 + relativeOffset;
      if (idx < myFrom || idx >= myTo)
        throw new NoSuchElementException(relativeOffset + " " + this);
      int off = myOffset - 1 + relativeOffset;
      int si = mySegmentIndex;
      IntSegment seg;
      if (off < 0 || off >= mySegmentSize) {
        if (off < 0) {
          si -= ((-off - 1) >> mySegmentBits) + 1;
        } else {
          si += off >> mySegmentBits;
        }
        off &= mySegmentMask;
        seg = mySegments.segments[si];
      } else {
        seg = mySegment == null ? mySegments.segments[si] : mySegment;
      }
      return seg.data[off];
    }

    public int index() throws NoSuchElementException {
      if (myNext < 0)
        throw new IllegalStateException();
      if (myNext <= myFrom)
        throw new NoSuchElementException();
      return myNext - 1;
    }

    public void set(int offset, int value) throws NoSuchElementException, IndexOutOfBoundsException {
      assert checkIterator();
      checkMod();
      if (myNext < 0)
        throw new IllegalStateException();
      int idx = myNext - 1 + offset;
      if (idx < myFrom || idx >= myTo)
        throw new NoSuchElementException(offset + " " + this);
      int off = myOffset - 1 + offset;
      int si = mySegmentIndex;
      IntSegment seg;
      if (off < 0 || off >= mySegmentSize) {
        if (off < 0) {
          si -= ((-off - 1) >> mySegmentBits) + 1;
        } else {
          si += off >> mySegmentBits;
        }
        off &= mySegmentMask;
        seg = mySegments.segments[si];
      } else {
        seg = mySegment == null ? mySegments.segments[si] : mySegment;
      }
      seg.data[off] = value;
      assert checkIterator();
    }

    public void removeRange(int offsetFrom, int offsetTo) throws NoSuchElementException {
      assert checkIterator();
      if (myNext < 0)
        throw new IllegalStateException();
      if (offsetFrom >= offsetTo) {
        assert offsetFrom == offsetTo : offsetFrom + " " + offsetTo;
        return;
      }
      checkMod();
      int f = myNext - 1 + offsetFrom;
      int t = myNext - 1 + offsetTo;
      if (f < myFrom || t > myTo)
        throw new NoSuchElementException(offsetFrom + " " + offsetTo + " " + this);
      SegmentedIntArray.this.removeRange(f, t);
      myNext = f;
      myTo -= (t - f);
      int p = myLeftOffset + myNext;
      mySegmentIndex = p >> mySegmentBits;
      mySegment = null;
      myOffset = p & mySegmentMask;
      myIterationModCount = modCount();
      myNext = -myNext-1;
      assert checkIterator();
    }

    @Override
    public void remove() throws NoSuchElementException, ConcurrentModificationException {
      assert checkIterator();
      checkMod();
      if (myNext < 0)
        throw new IllegalStateException();
      if (myNext <= myFrom)
        throw new NoSuchElementException();
      int p = myNext - 1;
      int lo = myLeftOffset;
      if (mySegment != null && (mySegment.refCount != 1 || myFrom + 1 == myTo)) {
        mySegment = null;
      }
      removeAt(p);
      myTo--;
      myNext = p;
      if (lo != myLeftOffset) {
        // left offset has been moved => there was left shift - we shouldn't do anything with offset
        if (myLeftOffset < lo) {
          // a segment has been removed
          assert myOffset == 0 : myOffset + " " + mySegmentSize;
          assert mySegmentIndex == 1 : mySegmentIndex + " " + this;
          mySegmentIndex--;
        }
      } else {
        // shift to left
        myOffset--;
        if (myOffset < 0) {
          mySegmentIndex--;
          mySegment = null;
          myOffset = mySegmentSize - 1;
        }
      }
      myIterationModCount = modCount();
      myNext = -myNext-1;
      assert checkIterator();
    }

    public String toString() {
      return myNext + "[" + myFrom + ";" + myTo + ") " + SegmentedIntArray.this;
    }

    protected void checkMod() {
      if (myIterationModCount != modCount())
        throw new ConcurrentModificationException();
    }

    final boolean checkIterator() {
      assert myFrom <= myTo : this;
      assert myNext >= myFrom && myNext <= myTo || myNext < 0 : this;
      int p = (myNext < 0) ? myLeftOffset - myNext-1 : myLeftOffset + myNext;
      assert p >> mySegmentBits == mySegmentIndex : mySegmentIndex + " " + myLeftOffset + " " + this;
      assert (p & mySegmentMask) == myOffset : myOffset + " " + myLeftOffset + " " + this;
      assert mySegment == null || size() > 0 : size() + " " + mySegment + " " + this;
      assert size() == 0 || (mySegment == null || mySegment == mySegments.segments[mySegmentIndex]) : this;
      assert myIterationModCount <= modCount() : myIterationModCount + " " + modCount() + " " + this;
      return true;
    }
  }
}
