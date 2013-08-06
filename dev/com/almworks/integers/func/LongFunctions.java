/*
 * Copyright 2011 ALM Works Ltd
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

  public static final LongFunction NEG = new LongFunction() {
    @Override
    public long invoke(long a) {
      return -a;
    }

    @Override
    public String toString() {
      return "-";
    }
  };

  public static final LongFunction2 ADD = new LongFunction2() {
    @Override
    public long invoke(long a, long b) {
      return a + b;
    }

    @Override
    public String toString() {
      return "+";
    }
  };

  public static final LongFunction2 MULT = new LongFunction2() {
    @Override
    public long invoke(long a, long b) {
      return a * b;
    }

    @Override
    public String toString() {
      return "*";
    }
  };

  public static final LongFunction2 MOD = new LongFunction2() {
    @Override
    public long invoke(long a, long b) {
      return a % b;
    }

    @Override
    public String toString() {
      return "%";
    }
  };

  public static final LongFunction I = new LongFunction() {
    @Override
    public long invoke(long a) {
      return a;
    }

    @Override
    public String toString() {
      return "I";
    }
  };

  public static LongFunction apply(final LongFunction2 f, final long a) {
    return new LongFunction() {
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

  public static LongFunction2 swap(final LongFunction2 f) {
    return new LongFunction2() {
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

  public static LongFunction2 ignore1(final LongFunction f) {
    return new LongFunction2() {
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

  public static LongFunction swap(final long v1, final long v2) {
    return new LongFunction() {
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
  
  public static LongFunction compose(final LongFunction f1, final LongFunction f2) {
    return new LongFunction() {
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
  public static LongFunction sequence(final LongIterable iterable) {
    return new LongFunction() {
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
}
