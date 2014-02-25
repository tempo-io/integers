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

// CODE GENERATED FROM com/almworks/integers/func/PFunctions.tpl




package com.almworks.integers.func;

import com.almworks.integers.LongIterable;
import com.almworks.integers.LongIterator;

public class LongFunctions {
  private LongFunctions() {}

  public static final LongToLong NEG = new LongToLong() {
    @Override
    public long invoke(long a) {
      return -a;
    }

    @Override
    public String toString() {
      return "-";
    }
  };

  public static final LongToLong INC = new LongToLong() {
    @Override
    public long invoke(long a) {
      return a + 1;
    }

    @Override
    public String toString() {
      return "+1";
    }
  };

  public static final LongToLong DEC = new LongToLong() {
    @Override
    public long invoke(long a) {
      return a - 1;
    }

    @Override
    public String toString() {
      return "-1";
    }
  };

  public static final LongToLong SQR = new LongToLong() {
    @Override
    public long invoke(long a) {
      return a * a;
    }

    @Override
    public String toString() {
      return "^2";
    }
  };

  public static final LongToLong I = new LongToLong() {
    @Override
    public long invoke(long a) {
      return a;
    }

    @Override
    public String toString() {
      return "I";
    }
  };

  public static LongToLong apply(final LongLongToLong f, final long a) {
    return new LongToLong() {
      @Override
      public long invoke(long b) {
        return f.invoke(a, b);
      }

      @Override
      public String toString() {
        return f + " " + a;
      }
    };
  }

  public static LongToLong swap(final long v1, final long v2) {
    return new LongToLong() {
      @Override
      public long invoke(long x) {
        return
            x == v1 ? v2 :
                x == v2 ? v1 :
                    x;
      }

      @Override
      public String toString() {
        return v1 + " <-> " + v2;
      }
    };
  }

  public static LongToLong compose(final LongToLong f1, final LongToLong f2) {
    return new LongToLong() {
      @Override
      public long invoke(long a) {
        return f1.invoke(f2.invoke(a));
      }

      @Override
      public String toString() {
        return '(' + f1.toString() + ") o (" + f2.toString() + ')';
      }
    };
  }

  /** Returns a function that returns values from the supplied Iterable. Function argument is ignored. */
  public static LongToLong sequence(final LongIterable iterable) {
    return new LongToLong() {
      LongIterator it = iterable.iterator();
      @Override
      public long invoke(long a) {
        return it.nextValue();
      }

      @Override
      public String toString() {
        return "i => " + iterable;
      }
    };
  }

  public static final LongLongToLong ADD = new LongLongToLong() {
    @Override
    public long invoke(long a, long b) {
      return a + b;
    }

    @Override
    public String toString() {
      return "+";
    }
  };

  public static final LongLongToLong MULT = new LongLongToLong() {
    @Override
    public long invoke(long a, long b) {
      return a * b;
    }

    @Override
    public String toString() {
      return "*";
    }
  };

  public static final LongLongToLong MOD = new LongLongToLong() {
    @Override
    public long invoke(long a, long b) {
      return a % b;
    }

    @Override
    public String toString() {
      return "%";
    }
  };

  public static LongLongToLong swap(final LongLongToLong f) {
    return new LongLongToLong() {
      @Override
      public long invoke(long a, long b) {
        return f.invoke(b, a);
      }

      @Override
      public String toString() {
        return "swap (" + f + ')';
      }
    };
  }

  public static LongLongToLong ignore1(final LongToLong f) {
    return new LongLongToLong() {
      @Override
      public long invoke(long a, long b) {
        return f.invoke(b);
      }

      @Override
      public String toString() {
        return "ingore1 (" + f + ')';
      }
    };
  }
}
