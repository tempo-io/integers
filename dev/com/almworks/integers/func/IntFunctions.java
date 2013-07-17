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

import com.almworks.integers.IntIterable;
import com.almworks.integers.IntIterator;

public class IntFunctions {
  private IntFunctions() {}

  public static final IntFunction NEG = new IntFunction() {
    @Override
    public int invoke(int a) {
      return -a;
    }

    @Override
    public String toString() {
      return "-";
    }
  };

  public static final IntFunction2 ADD = new IntFunction2() {
    @Override
    public int invoke(int a, int b) {
      return a + b;
    }

    @Override
    public String toString() {
      return "+";
    }
  };

  public static final IntFunction2 MULT = new IntFunction2() {
    @Override
    public int invoke(int a, int b) {
      return a * b;
    }

    @Override
    public String toString() {
      return "*";
    }
  };

  public static final IntFunction2 MOD = new IntFunction2() {
    @Override
    public int invoke(int a, int b) {
      return a % b;
    }

    @Override
    public String toString() {
      return "%";
    }
  };

  public static final IntFunction I = new IntFunction() {
    @Override
    public int invoke(int a) {
      return a;
    }

    @Override
    public String toString() {
      return "I";
    }
  };

  public static IntFunction apply(final IntFunction2 f, final int a) {
    return new IntFunction() {
      @Override
      public int invoke(int b) {
        return f.invoke(a, b);
      }

      @Override
      public String toString() {
        return f + " " + a;
      }
    };
  }

  public static IntFunction2 swap(final IntFunction2 f) {
    return new IntFunction2() {
      @Override
      public int invoke(int a, int b) {
        return f.invoke(b, a);
      }

      @Override
      public String toString() {
        return "swap (" + f + ')';
      }
    };
  }

  public static IntFunction2 ignore1(final IntFunction f) {
    return new IntFunction2() {
      @Override
      public int invoke(int a, int b) {
        return f.invoke(b);
      }

      @Override
      public String toString() {
        return "ingore1 (" + f + ')';
      }
    };
  }

  public static IntFunction swap(final int v1, final int v2) {
    return new IntFunction() {
      @Override
      public int invoke(int x) {
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
  
  public static IntFunction compose(final IntFunction f1, final IntFunction f2) {
    return new IntFunction() {
      @Override
      public int invoke(int a) {
        return f1.invoke(f2.invoke(a));
      }

      @Override
      public String toString() {
        return '(' + f1.toString() + ") o (" + f2.toString() + ')';
      }
    };
  }
    
  /** Returns a function that returns values from the supplied Iterable. Function argument is ignored. */
  public static IntFunction sequence(final IntIterable iterable) {
    return new IntFunction() {
      IntIterator it = iterable.iterator();
      @Override
      public int invoke(int a) {
        return it.nextValue();
      }
  
      @Override
      public String toString() {
        return "i => " + iterable;
      }
    };
  }
}
