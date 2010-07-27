/*
 * Copyright 2010 ALM Works Ltd
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import static com.almworks.integers.IntegersUtils.*;

import java.util.Arrays;

 public final class #E#Array extends AbstractWritable#E#List {
   /**
    * Holds the host array
    */
   @NotNull
   private #e#[] myArray;

   public #E#Array() {
     myArray = EMPTY_#EC#S;
   }

   public #E#Array(int size) {
     myArray = size <= 0 ? EMPTY_#EC#S : new #e#[size];
   }

   public #E#Array(#E#List copyFrom) {
     this(copyFrom == null ? 0 : copyFrom.size());
     if (copyFrom != null) {
       addAll(copyFrom);
     }
   }

   public #E#Array(#E#Iterator iterator) {
     myArray = EMPTY_#EC#S;
     if (iterator != null)
       addAll(iterator);
   }

   public #E#Array(#e#[] hostArray) {
     this(hostArray, hostArray == null ? 0 : hostArray.length);
   }

   public #E#Array(#e#[] hostArray, int length) {
     myArray = hostArray == null ? EMPTY_#EC#S : hostArray;
     updateSize(length < 0 ? 0 : length >= myArray.length ? myArray.length : length);
   }

   public static #E#Array copy(#e#[] array) {
     return copy(array, array == null ? 0 : array.length);
   }

   public static #E#Array copy(#e#[] array, int length) {
     return new #E#Array(#E#Collections.arrayCopy(array, 0, length));
   }

   public static #E#Array copy(@Nullable #E#Iterable iterable) {
     if (iterable == null) return new #E#Array();
     if (iterable instanceof #E#Array) {
       #E#Array other = (#E#Array) iterable;
       return copy(other.myArray, other.size());
     } else if (iterable instanceof #E#List)
       return new #E#Array((#E#List) iterable);
     else return new #E#Array(iterable.iterator());
   }

   public static #E#Array create(#e# ... values) {
     if (values == null)
       values = EMPTY_#EC#S;
     return new #E#Array(values);
   }

   public static #E#Array singleton(#EW# value) {
     return new #E#Array(new #e#[]{value});
   }

   public int indexOf(#e# value) {
     return #E#Collections.indexOf(myArray, 0, size(), value);
   }

   public final #e# get(int index) {
     return myArray[index];
   }

   public #e#[] toArray(int sourceOffset, #e#[] dest, int destOffset, int length) {
     System.arraycopy(myArray, sourceOffset, dest, destOffset, length);
     return dest;
   }

   private void ensureCapacity(int expectedSize) {
     myArray = #E#Collections.ensureCapacity(myArray, expectedSize);
   }

   public void set(int index, #e# value) {
     myArray[index] = value;
   }

   public void setRange(int from, int to, #e# value) {
     Arrays.fill(myArray, from, to, value);
   }

   public void removeRange(int from, int to) {
     if (from >= to)
       return;
     int sz = size();
     if (to < sz)
       System.arraycopy(myArray, to, myArray, from, sz - to);
     updateSize(sz - (to - from));
   }

   public void clear() {
     updateSize(0);
   }

   public void insertMultiple(int index, #e# value, int count) {
     if (count > 0) {
       makeSpaceForInsertion(index, index + count);
       Arrays.fill(myArray, index, index + count, value);
     }
   }

   public void expand(int index, int count) {
     if (count > 0) {
       makeSpaceForInsertion(index, index + count);
     }
   }

   public void insert(int index, #e# value) {
     makeSpaceForInsertion(index, index + 1);
     myArray[index] = value;
   }

   public void add(#e# value) {
     int sz = size();
     int nsz = sz + 1;
     ensureCapacity(nsz);
     myArray[sz] = value;
     updateSize(nsz);
   }

   public void insertAll(int index, #E#List collection, int sourceIndex, int count) {
     if (count <= 0)
       return;
     makeSpaceForInsertion(index, index + count);
     collection.toArray(sourceIndex, myArray, index, count);
   }

   private void makeSpaceForInsertion(int from, int to) {
     int sz = size();
     if (from < 0 || from > sz)
       throw new IndexOutOfBoundsException(from + " " + to + " " + sz);
     if (from < to) {
       int added = to - from;
       int newsz = sz + added;
       ensureCapacity(newsz);
       int move = sz - from;
       if (move > 0)
         System.arraycopy(myArray, from, myArray, to, move);
       updateSize(newsz);
     }
   }

   public void setAll(int index, #E#List values, int sourceIndex, int count) {
     if (count <= 0)
       return;
     int sz = size();
     if (index < 0 || index >= sz)
       throw new IndexOutOfBoundsException(index + " " + sz);
     if (index + count > sz)
       throw new IndexOutOfBoundsException(index + " " + count + " " + sz);
     values.toArray(sourceIndex, myArray, index, count);
   }

   public void swap(int index1, int index2) {
     #E#Collections.swap(myArray, index1, index2);
   }

   public void addAll(#e#... ints) {
     if (ints.length == 0)
       return;
     int sz = size();
     int newsz = sz + ints.length;
     ensureCapacity(newsz);
     System.arraycopy(ints, 0, myArray, sz, ints.length);
     updateSize(newsz);
   }

   public void sort(Writable#E#List... sortAlso) {
     if (sortAlso == null || sortAlso.length == 0)
       Arrays.sort(myArray, 0, size());
     else
       super.sort(sortAlso);
   }

   public void addAll(#E#List collection) {
     if (collection instanceof #E#List) {
       #E#List list = (#E#List) collection;
       int added = list.size();
       int sz = size();
       int newSize = sz + added;
       ensureCapacity(newSize);
       list.toArray(0, myArray, sz, added);
       updateSize(newSize);
     } else {
       addAll(collection.iterator());
     }
   }

   @Override
   protected boolean checkSorted(boolean checkUnique) {
     int r = #E#Collections.isSortedUnique(!checkUnique, myArray, 0, size());
     return r == 0 || (r < 0 && !checkUnique);
   }

   public boolean equalOrder(#e#[] array) {
     if (size() != array.length)
       return false;
     for (int i = 0; i < size(); i++)
       if (array[i] != get(i))
         return false;
     return true;
   }

   public void sortUnique() {
     Arrays.sort(myArray, 0, size());
     updateSize(#E#Collections.removeSubsequentDuplicates(myArray, 0, size()));
   }

   public void retain(#E#List values) {
     if (values.isEmpty()) {
       clear();
       return;
     }
     #E#List sortedValues = #E#Collections.toSorted(false, values);
     for (int i = size() - 1; i >= 0; i--) {
       #e# v = myArray[i];
       if (sortedValues.binarySearch(v) >= 0) continue;
       removeAt(i);
     }
   }

   public boolean equalSortedValues(#E#List collection) {
     assert isUniqueSorted();
     if (size() != collection.size())
       return false;
     #E#Iterator ownIt = iterator();
     #e# prevOther = #EW#.MIN_VALUE;
     for (#E#Iterator it = collection.iterator(); it.hasNext();) {
       #e# own = ownIt.next();
       #e# other = it.next();
       if (other <= prevOther) {
         assert false : collection; // Not sorted
         return false;
       }
       if (own != other)
         return false;
       prevOther = other;
     }
     return true;
   }

   /**
    * Adds first maxCount elements from collection or the whole collection if it size is less than maxCount.
    * @param collection
    * @param maxCount
    * @return number of added elements
    */
   public int addAllNotMore(#E#Array collection, int maxCount) {
     int toAdd = Math.min(maxCount, collection.size());
     ensureCapacity(size() + toAdd);
     System.arraycopy(collection.myArray, 0, myArray, size(), toAdd);
     updateSize(size() + toAdd);
     return toAdd;
   }

   /**
    * Adds first maxCount elements from iterator or all if iterator end reached first
    * @param iterator
    * @param maxCount
    * @return number of added elements
    */
   public int addAllNotMore(#E#Iterator iterator, int maxCount) {
     int counter = 0;
     while (iterator.hasNext() && counter < maxCount) {
       add(iterator.next());
       counter++;
     }
     return counter;
   }

   public void removeSorted(#e# value) {
     assert isSorted();
     int index = binarySearch(value);
     if (index >= 0) removeAt(index);
   }

   public #e#[] extractHostArray() {
     #e#[] array = myArray;
     myArray = EMPTY_#EC#S;
     updateSize(0);
     return array;
   }
  }
