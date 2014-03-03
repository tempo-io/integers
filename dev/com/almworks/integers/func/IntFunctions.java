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

import com.almworks.integers.IntIterable;
import com.almworks.integers.IntIterator;

public class IntFunctions {
  private IntFunctions() {}

  public static final IntToInt NEG = new IntToInt() {
    @Override
    public int invoke(int a) {
      return -a;
    }

    @Override
    public String toString() {
      return "-";
    }
  };

  public static final IntToInt INC = new IntToInt() {
    @Override
    public int invoke(int a) {
      return a + 1;
    }

    @Override
    public String toString() {
      return "+1";
    }
  };

  public static final IntToInt DEC = new IntToInt() {
    @Override
    public int invoke(int a) {
      return a - 1;
    }

    @Override
    public String toString() {
      return "-1";
    }
  };

  public static final IntToInt SQR = new IntToInt() {
    @Override
    public int invoke(int a) {
      return a * a;
    }

    @Override
    public String toString() {
      return "^2";
    }
  };

  public static final IntToInt I = new IntToInt() {
    @Override
    public int invoke(int a) {
      return a;
    }

    @Override
    public String toString() {
      return "I";
    }
  };

  public static final IntIntToInt ADD = new IntIntToInt() {
    @Override
    public int invoke(int a, int b) {
      return a + b;
    }

    @Override
    public String toString() {
      return "+";
    }
  };

  public static final IntIntToInt MULT = new IntIntToInt() {
    @Override
    public int invoke(int a, int b) {
      return a * b;
    }

    @Override
    public String toString() {
      return "*";
    }
  };

  public static final IntIntToInt MOD = new IntIntToInt() {
    @Override
    public int invoke(int a, int b) {
      return a % b;
    }

    @Override
    public String toString() {
      return "%";
    }
  };

  public static IntToInt apply(final IntIntToInt f, final int a) {
    return new IntToInt() {
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

  public static IntToInt swap(final int v1, final int v2) {
    return new IntToInt() {
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

  public static IntToInt compose(final IntToInt f1, final IntToInt f2) {
    return new IntToInt() {
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
  public static IntToInt sequence(final IntIterable iterable) {
    return new IntToInt() {
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

  public static IntIntToInt swap(final IntIntToInt f) {
    return new IntIntToInt() {
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

  public static IntIntToInt ignore1(final IntToInt f) {
    return new IntIntToInt() {
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
}
