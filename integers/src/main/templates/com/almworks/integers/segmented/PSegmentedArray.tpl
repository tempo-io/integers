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
import com.almworks.integers.func.#E#To#E#;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class #E#SegmentedArray extends AbstractWritable#E#List implements Cloneable {
  private static final int SEGB_INITIAL = 4;
  private static final int SEGS_INITIAL = 1 << SEGB_INITIAL;
  private static final int SEGB_LARGE = 10;
  private static final int SEGS_LARGE = 1 << SEGB_LARGE;

  private final #E#SegmentedArrayEnvironment myEnv;

  /**
   * List of segments holding the data. All segments are of equal size.
   */
  private #E#Segments mySegments;

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
   * for usage, see {@link #writeValue(int, #e#)}
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
        mySegmentCount >= 0 && (mySegments == null || mySegmentCount <= mySegments.segments.length) :
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
          #E#Segment seg = mySegments.segments[i];
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


  public #E#SegmentedArray() {
    this(#E#SegmentedArrayHeapEnvironment.INSTANCE);
  }

  public #E#SegmentedArray(#E#SegmentedArrayEnvironment env) {
    myEnv = env;
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  public static #E#SegmentedArray create(#E#List values) {
    #E#SegmentedArray array = new #E#SegmentedArray();
    array.addAll(values);
    return array;
  }

  public #e# get(int index) {
    assert !IntegersDebug.CHECK || checkInvariants();
    try {
      index += myLeftOffset;
      #E#Segment s = mySegments.segments[index >> mySegmentBits];
      return s.data[index & mySegmentMask];
    } catch (NullPointerException e) {
      if (index < 0 || index >= size())
        throw new IndexOutOfBoundsException(index + " " + this);
      else
        throw e;
    }
  }

  public void add(#e# value) {
    assert !IntegersDebug.CHECK || checkInvariants();
    int p = size();
    increaseSize(1, false);
    set(p, value);
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  @NotNull
  public Writable#E#ListIterator iterator(int from, int to) {
    if (from >= to) {
      assert from == to : from + " " + to;
      return #E#Iterator.EMPTY;
    }
    return new SegmentedIterator(from, to);
  }

  public void expand(int index, int count) {
    if (count < 0) {
      throw new IllegalArgumentException();
    }
    if (index < 0 || index > size())
      throw new IndexOutOfBoundsException(index + " " + this);
    assert !IntegersDebug.CHECK || checkInvariants();
    if (count == 0) {
      return;
    }
    doExpand(index, count);
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  public void insertMultiple(int index, #e# value, int count) {
    assert !IntegersDebug.CHECK || checkInvariants();
    if (count < 0) throw new IllegalArgumentException();
    int sz = size();
    if (index < 0 || index > sz)
      throw new IndexOutOfBoundsException(index + " " + this);
    if (count == 0) return;

    doExpand(index, count);
    if (count == 1) {
      set(index, value);
    } else {
      setRange(index, index + count, value);
    }
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  public void removeRange(int from, int to) {
    assert !IntegersDebug.CHECK || checkInvariants();
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
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  public #e# removeAt(int index) {
    assert !IntegersDebug.CHECK || checkInvariants();
    #e# value = get(index);
    int sz = size();
    boolean leftward = index < (sz >> 1);
    if (leftward) {
      shiftRight(myLeftOffset, myLeftOffset + index, 1);
    } else {
      shiftLeft(myLeftOffset + index + 1, myLeftOffset + sz, 1);
    }
    decreaseSize(1, leftward);
    assert !IntegersDebug.CHECK || checkInvariants();
    return value;
  }

  public void addAll(#E#List values) {
    insertAll(size(), values);
  }

  public void insertAll(int index, #E#List values, int sourceIndex, int count) {
    if (values == null || count <= 0)
      return;
    if (values instanceof #E#SegmentedArray) {
      insertSegmented(index, (#E#SegmentedArray) values, sourceIndex, count);
    } else if (values instanceof SubList) {
      SubList sublist = (SubList) values;
      if (sourceIndex + count > sublist.size())
        throw new IndexOutOfBoundsException(sourceIndex + " " + count + " " + values);
      insertAll(index, sublist.getParent(), sourceIndex + sublist.getFrom(), count);
    } else {
      insertList(index, values, sourceIndex, count);
    }
  }

  public void setAll(int index, #E#List values, int sourceIndex, int count) {
    assert !IntegersDebug.CHECK || checkInvariants();
    if (count < 0) {
      throw new IllegalArgumentException("count < 0");
    }
    if (values == null || count == 0) {
      return;
    }
    int sz = size();
    if (index < 0 || index >= sz)
      throw new IndexOutOfBoundsException(index + " " + sz);
    if (index + count > sz)
      throw new IndexOutOfBoundsException(index + " " + count + " " + sz);

    if (values instanceof #E#SegmentedArray) {
      copySegmented(index, (#E#SegmentedArray) values, sourceIndex, count);
    } else if (values instanceof SubList) {
      SubList sublist = (SubList) values;
      if (sourceIndex + count > sublist.size())
        throw new IndexOutOfBoundsException(sourceIndex + " " + count + " " + values);
      setAll(index, sublist.getParent(), sourceIndex + sublist.getFrom(), count);
    } else {
      copyList(index, values, sourceIndex, count);
    }
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  public #e#[] toNativeArray(int startIndex, #e#[] dest, int destOffset, int length) {
    if (length <= 0)
      return dest;
    assert !IntegersDebug.CHECK || checkInvariants();
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

  public #E#SegmentedArray clone() {
    return clone(0, size());
  }

  public #E#SegmentedArray clone(int from, int to) {
    assert !IntegersDebug.CHECK || checkInvariants();
    checkRange(from, to);
    if (from >= to)
      return new #E#SegmentedArray(myEnv);
    try {
      assert mySegmentCount > 0 : this;
      #E#SegmentedArray r = (#E#SegmentedArray) super.clone();
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
      assert !IntegersDebug.CHECK || checkInvariants();
      assert !IntegersDebug.CHECK || r.checkInvariants();
      return r;
    } catch (CloneNotSupportedException e) {
      throw new Error(e);
    }
  }

  private void insertList(int index, #E#List list, int sourceIndex, int count) {
    assert !IntegersDebug.CHECK || checkInvariants();
    assert list != null && !list.isEmpty() && count > 0 && count <= list.size();
    doExpand(index, count);
    copyList(index, list, sourceIndex, count);
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  private void copyList(int targetIndex, #E#List list, int sourceIndex, int count) {
    int dp = myLeftOffset + targetIndex;
    int di = dp >> mySegmentBits;
    dp &= mySegmentMask;
    for (int i = 0; i < count;) {
      #E#Segment seg = modify(di);
      int len = Math.min(count - i, mySegmentSize - dp);
      list.toNativeArray(sourceIndex + i, seg.data, dp, len);
      i += len;
      dp = 0;
      di++;
    }
  }

  private void insertSegmented(int index, #E#SegmentedArray array, int sourceIndex, int count) {
    assert !IntegersDebug.CHECK || checkInvariants();
    assert array != null && !array.isEmpty() && count > 0 && count <= array.size() : count + " " + array;
    // todo if segments are copied as whole, we don't need expand to allocate new segments
    doExpand(index, count);
    copySegmented(index, array, sourceIndex, count);
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  private void copySegmented(int targetIndex, #E#SegmentedArray array, int sourceIndex, int length) {
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
        #E#Segment seg = modify(di);
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
    int rightAllocate = count <= myRightOffset ? 0 : ((count - myRightOffset - 1) >> mySegmentBits) + 1;

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
        #E#Segment tseg = modify(targetSegment);
        #E#Segment sseg = mySegments.segments[sourceSegment];
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
        #E#Segment tseg = modify(targetSegment);
        #E#Segment sseg = mySegments.segments[sourceSegment];
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

  // todo refactor, simplify method increaseSize and add checkInvariants() in method expandSingleSegment().
  private void increaseSize(int added, boolean leftward) {
    assert !IntegersDebug.CHECK || checkInvariants();
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
          relatedOffset = leftward ? myLeftOffset : myRightOffset;
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
    assert !IntegersDebug.CHECK || checkInvariants();
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
      #E#Segment s = myEnv.allocate(mySegmentSize);
      #E#Segment olds = mySegments.segments[0];
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
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  private void allocateSegments(int allocateCount, boolean leftward) {
    int totalCount = mySegmentCount + allocateCount;
    if (mySegments == null || mySegments.segments.length < totalCount) {
      #E#Segments newsegm = newSegments(totalCount);
      if (mySegmentCount > 0) {
        System.arraycopy(mySegments.segments, 0, newsegm.segments, leftward ? allocateCount : 0, mySegmentCount);
      }
      #E#Segments oldsegm = mySegments;
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
      #E#Segment s = myEnv.allocate(mySegmentSize);
      use(s);
      mySegments.segments[index] = s;
      // cannot use setSegment, because old segment will be derefcounted
    }
    mySegmentCount = totalCount;
  }

  private #E#Segments newSegments(int count) {
    int allocate = SEGS_INITIAL;
    while (allocate < count)
      allocate <<= 1;
    #E#Segments r = myEnv.allocateSegments(allocate);
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
      #E#Segments olds = mySegments;
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
    assert !IntegersDebug.CHECK || checkInvariants();
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
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  public void set(int index, #e# value) {
    assert !IntegersDebug.CHECK || checkInvariants();
    writeValue(myLeftOffset + index, value);
    assert !IntegersDebug.CHECK || checkInvariants();
  }

  public void setRange(int from, int to, #e# value) {
    assert !IntegersDebug.CHECK || checkInvariants();

    int len = to - from;
    setAll(from, #E#Collections.repeat(value, len), 0, len);

    assert !IntegersDebug.CHECK || checkInvariants();
  }

  private void writeValue(int absIndex, #e# value) {
    modify(absIndex >> mySegmentBits).data[absIndex & mySegmentMask] = value;
  }

  private #E#Segment modify(int segmentIndex) {
    #E#Segment s = mySegments.segments[segmentIndex];
    if (s.refCount == 1)
      return s;
    #E#Segment newseg = myEnv.allocate(mySegmentSize);
    int L = segmentIndex == 0 ? myLeftOffset : 0;
    int R = segmentIndex == mySegmentCount - 1 ? myRightOffset : 0;
    myEnv.copy(s.data, L, newseg.data, L, s.data.length - L - R);
    return setSegment(segmentIndex, newseg);
  }

  private #E#Segment setSegment(int index, #E#Segment segment) {
    #E#Segment old = mySegments.segments[index];
    if (old != segment) {
      modifySegments();
      mySegments.segments[index] = segment;
      use(segment);
      unuse(old);
    }
    return segment;
  }

  private void use(#E#Segment segment) {
    if (segment != null) {
      segment.refCount++;
    }
  }

  private void unuse(#E#Segment old) {
    if (old != null) {
      if (--old.refCount == 0) {
        myEnv.free(old);
      }
    }
  }

  public void apply(int from, int to, #E#To#E# function) {
    assert !IntegersDebug.CHECK || checkInvariants();

    int len = to - from;
    if (len <= 0) return;

    checkRange(from, to);
    setAll(from, #E#Collections.map(function, this), from, len);
    assert !IntegersDebug.CHECK || checkInvariants();
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
  public int binarySearch(#e# value, int from, int to) {
    return super.binarySearch(value, from, to);
  }

  // todo more effective sort
  public void sort(Writable#E#List... sortAlso) {
    super.sort(sortAlso);
  }

  // todo more effective swap
  public void swap(int index1, int index2) {
    super.swap(index1, index2);
  }



  private class SegmentedIterator extends Abstract#E#Iterator implements Writable#E#ListIterator {
    private final int myFrom;
    private int myTo;
    private int myNext;
    private #e# myCurrent;

    /**
     * The segment of the next value
     */
    private int mySegmentIndex;
    private #E#Segment mySegment;

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
      assert !IntegersDebug.CHECK || checkIterator();
    }

    public void move(int offset) throws ConcurrentModificationException, NoSuchElementException {
      assert !IntegersDebug.CHECK || checkIterator();
      checkMod();
      if (myNext < 0) {
        throw new IllegalStateException();
      }
      if (offset == 0) {
        return;
      }
      int p = myNext - 1 + offset;
      if (p < myFrom || p >= myTo) {
        throw new NoSuchElementException(offset + " " + this);
      }
      updateOffset(myOffset - 1 + offset);
      if (mySegment == null) {
        mySegment = mySegments.segments[mySegmentIndex];
      }
      myCurrent = mySegment.data[myOffset];
      myNext = p + 1;
      updateOffset(myOffset + 1);
      assert !IntegersDebug.CHECK || checkIterator();
    }

    public Writable#E#ListIterator next() throws ConcurrentModificationException, NoSuchElementException {
      assert !IntegersDebug.CHECK || checkIterator();
      checkMod();
      if (myNext < 0)
        myNext = -myNext-1;
      if (myNext < myFrom || myNext >= myTo)
        throw new NoSuchElementException(String.valueOf(this));
      if (mySegment == null) {
        mySegment = mySegments.segments[mySegmentIndex];
      }
      myCurrent = mySegment.data[myOffset];
      myNext++;
      updateOffset(myOffset + 1);
      assert !IntegersDebug.CHECK || checkIterator();
      return this;
    }

    public boolean hasValue() {
      return myFrom < myNext;
    }

    public #e# value() throws NoSuchElementException {
      if (myNext < 0) throw new IllegalStateException();
      if (!hasValue()) throw new NoSuchElementException();
      return myCurrent;
    }

    // update myOffset, mySegment, mySegmentIndex
    private void updateOffset(int newOffset) {
      myOffset = newOffset;
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

    public #e# get(int relativeOffset) throws IndexOutOfBoundsException, NoSuchElementException {
      assert !IntegersDebug.CHECK || checkIterator();
      checkMod();
      if (myNext < 0)
        throw new IllegalStateException();
      int idx = myNext - 1 + relativeOffset;
      if (idx < myFrom || idx >= myTo)
        throw new NoSuchElementException(relativeOffset + " " + this);
      int off = myOffset - 1 + relativeOffset;
      int si = mySegmentIndex;
      #E#Segment seg;
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

    public void set(int offset, #e# value) throws NoSuchElementException, IndexOutOfBoundsException {
      assert !IntegersDebug.CHECK || checkIterator();
      checkMod();
      if (myNext < 0) {
        throw new IllegalStateException();
      }
      int idx = myNext - 1 + offset;
      if (idx < myFrom || idx >= myTo) {
        throw new NoSuchElementException(offset + " " + this);
      }
      int off = myOffset - 1 + offset;
      #E#Segment seg;
      if (off < 0 || off >= mySegmentSize) {
        int si = mySegmentIndex;
        if (off < 0) {
          si -= ((-off - 1) >> mySegmentBits) + 1;
        } else {
          si += off >> mySegmentBits;
        }
        off &= mySegmentMask;
        seg = modify(si);
      } else {
        seg = modify(mySegmentIndex);
        if (mySegment != null) {
          mySegment = seg;
        }
      }
      seg.data[off] = value;
      assert !IntegersDebug.CHECK || checkIterator();
    }

    public void removeRange(int offsetFrom, int offsetTo) throws NoSuchElementException {
      assert !IntegersDebug.CHECK || checkIterator();
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
      #E#SegmentedArray.this.removeRange(f, t);
      myNext = f;
      myTo -= (t - f);
      int p = myLeftOffset + myNext;
      mySegmentIndex = p >> mySegmentBits;
      mySegment = null;
      myOffset = p & mySegmentMask;
      myIterationModCount = modCount();
      myNext = -myNext-1;
      assert !IntegersDebug.CHECK || checkIterator();
    }

    @Override
    public void remove() throws NoSuchElementException, ConcurrentModificationException {
      assert !IntegersDebug.CHECK || checkIterator();
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
        updateOffset(myOffset - 1);
      }
      myIterationModCount = modCount();
      myNext = -myNext-1;
      assert !IntegersDebug.CHECK || checkIterator();
    }

    public String toString() {
      return myNext + "[" + myFrom + ";" + myTo + ") " + #E#SegmentedArray.this;
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
