package com.almworks.integers.util;

import com.almworks.integers.IntArray;
import com.almworks.integers.LongArray;
import com.almworks.integers.LongIterator;
import com.almworks.integers.NativeIntFixture;

import static com.almworks.integers.LongArray.create;

public class SortedLongListUnionIteratorTests extends NativeIntFixture {
  long max = Long.MAX_VALUE, min = Long.MIN_VALUE;


  public void templateCase(LongArray a, LongArray b, long ... expected) {

    SortedLongListUnionIterator res = SortedLongListUnionIterator.create(a.iterator(), b.iterator());
    CHECK.order(res, expected);
  }

  public void testSimpleCase() {
    templateCase(create(1, 3, 5, 7),
          create(2, 3, 4, 6, 100), 1, 2, 3, 4, 5, 6, 7, 100);

    templateCase(create(1, 2, 3, 4),
        create(5,6,7,8), 1,2,3,4,5,6,7,8);

    templateCase(create(),
        create(2, 8), 2, 8);
  }
  public void testMinMaxCase() {
    templateCase(create(min),
        create(5,10,15,max), min, 5, 10, 15, max);

    templateCase(create(min, max),
        create(3, 7), min, 3, 7, max);

  }

  public void testComplexCase() {
    templateCase(create(1, 5, 7, 14, 17, 34, 45, 53, 74, 79, 83, 86, 91, 102, 108, 109, 114, 124, 141, 151, 157, 188, 205, 207, 220, 260, 268, 270, 272, 276, 282, 303, 313, 315, 321, 323, 329, 338, 349, 356, 391, 395, 397, 402, 416, 427, 441, 459, 488, 494, 501, 515, 517, 520, 544, 560, 564, 565, 566, 569, 592, 629, 642, 643, 655, 689, 691, 706, 723, 727, 762, 773, 774, 805, 811, 827, 837, 839, 843, 846, 867, 872, 873, 877, 880, 916, 927, 938, 941, 955, 973, 996, 999),
        create(6, 16, 25, 27, 34, 35, 36, 40, 45, 83, 99, 108, 122, 146, 152, 156, 160, 191, 205, 208, 216, 231, 233, 238, 252, 257, 261, 270, 273, 276, 302, 305, 308, 326, 347, 354, 357, 358, 363, 365, 376, 388, 392, 397, 398, 405, 416, 426, 432, 433, 448, 458, 475, 477, 501, 505, 514, 530, 537, 544, 545, 557, 571, 597, 634, 649, 657, 669, 676, 702, 703, 717, 720, 727, 736, 762, 765, 769, 778, 783, 795, 804, 808, 858, 862, 868, 887, 909, 911, 918, 921, 928, 964, 969, 981),
        1, 5, 6, 7, 14, 16, 17, 25, 27, 34, 35, 36, 40, 45, 53, 74, 79, 83, 86, 91, 99, 102, 108, 109, 114, 122, 124, 141, 146, 151, 152, 156, 157, 160, 188, 191, 205, 207, 208, 216, 220, 231, 233, 238, 252, 257, 260, 261, 268, 270, 272, 273, 276, 282, 302, 303, 305, 308, 313, 315, 321, 323, 326, 329, 338, 347, 349, 354, 356, 357, 358, 363, 365, 376, 388, 391, 392, 395, 397, 398, 402, 405, 416, 426, 427, 432, 433, 441, 448, 458, 459, 475, 477, 488, 494, 501, 505, 514, 515, 517, 520, 530, 537, 544, 545, 557, 560, 564, 565, 566, 569, 571, 592, 597, 629, 634, 642, 643, 649, 655, 657, 669, 676, 689, 691, 702, 703, 706, 717, 720, 723, 727, 736, 762, 765, 769, 773, 774, 778, 783, 795, 804, 805, 808, 811, 827, 837, 839, 843, 846, 858, 862, 867, 868, 872, 873, 877, 880, 887, 909, 911, 916, 918, 921, 927, 928, 938, 941, 955, 964, 969, 973, 981, 996, 999);


  }

}
