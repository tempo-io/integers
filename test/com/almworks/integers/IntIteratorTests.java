package com.almworks.integers;

import com.almworks.integers.util.SortedIntListIntersectionIterator;
import junit.framework.TestCase;
import com.almworks.integers.util.SortedIntListMinusIterator;

public class IntIteratorTests extends TestCase {
    protected final int MIN = Integer.MIN_VALUE;
    protected final int MAX = Integer.MAX_VALUE;

    public void testSimple() {
        assertContents(
                a(1, 3, 5).iterator(),
                a(1, 3, 5));
    }

    public void testMinusEmptyArrays() {
        testMinus(a(), a(), a());
        testMinus(a(), a(1, 3, 5), a());
        testMinus(a(1, 3, 5), a(), a(1, 3, 5));
    }

    public void testMinusExcludesNotPresent() {
        testMinus(a(1, 3, 5), a(0), a(1, 3, 5));
        testMinus(a(1, 3, 5), a(-2), a(1, 3, 5));
        testMinus(a(1, 3, 5), a(6), a(1, 3, 5));
        testMinus(a(1, 3, 5), a(-2, 0, 2, 6), a(1, 3, 5));
    }

    public void testMinusExcludesPresent() {
        testMinus(a(1, 3, 5), a(1), a(3, 5));
        testMinus(a(1, 3, 5), a(3), a(1, 5));
        testMinus(a(1, 3, 5), a(5), a(1, 3));
        testMinus(a(1, 3, 5), a(1, 3, 5), a());
    }

    public void testMinusExtremeValues() {
        testMinus(a(1, 3, 5), a(MIN), a(1, 3, 5));
        testMinus(a(1, 3, 5), a(MAX), a(1, 3, 5));
        testMinus(a(MIN, 1, 3, 5, MAX), a(1, 3, 5), a(MIN, MAX));
        testMinus(a(MIN, 1, 3, 5, MAX), a(MIN, 3, MAX), a(1, 5));
    }

    public void testMinusNotUnique() {
        testMinus(a(1, 1, 3, 3, 5, 5), a(1, 3, 5), a());
        testMinus(a(1, 1, 3, 3, 5, 5), a(1, 5), a(3, 3));
        testMinus(a(1, 3, 5), a(1, 1, 5, 5), a(3));
    }

    private void testMinus(IntArray include, IntArray exclude, IntArray difference) {
        assertContents(new SortedIntListMinusIterator(include.iterator(), exclude.iterator()), difference);
    }

    public void testInterEmptyArrays() {
        testInterSym(a(), a(), a());
        testInterSym(a(), a(1, 3, 5), a());
        testInterSym(a(1, 3, 5), a(), a());
    }

    public void testInterEmptyIntersection() {
        testInterSym(a(1, 3, 5), a(0), a());
        testInterSym(a(1, 3, 5), a(-2), a());
        testInterSym(a(1, 3, 5), a(6), a());
        testInterSym(a(1, 3, 5), a(-2, 0, 2, 6), a());
    }

    public void testInterNotEmptyIntersection() {
        testInterSym(a(1, 3, 5), a(1), a(1));
        testInterSym(a(1, 3, 5), a(3), a(3));
        testInterSym(a(1, 3, 5), a(5), a(5));
        testInterSym(a(1, 3, 5), a(1, 3, 5), a(1, 3, 5));
        testInterSym(a(1, 3, 5), a(0, 1, 2, 3, 4, 5, 6), a(1, 3, 5));
    }

    public void testInterExtremeValues() {
        testInterSym(a(1, 3, 5), a(MIN), a());
        testInterSym(a(1, 3, 5), a(MAX), a());
        testInterSym(a(MIN, 1, 3, 5, MAX), a(1, 3, 5), a(1, 3, 5));
        testInterSym(a(MIN, 1, 3, 5, MAX), a(MIN, 3, MAX), a(MIN, 3, MAX));
    }

    private void testInterSym(IntArray array1, IntArray array2, IntArray intersection) {
        // when intersection is symmetric
        testInter(array1, array2, intersection);
        testInter(array2, array1, intersection);
    }

    public void testInterNotUnique() {
        // behavior is non-symmetric, prefers first array
        // don't depend on that!

        testInter(a(1, 1, 3, 3, 5, 5), a(1, 3, 5), a(1, 1, 3, 3, 5, 5));
        testInter(a(1, 3, 5), a(1, 1, 3, 3, 5, 5), a(1, 3, 5));

        testInter(a(1, 1, 3, 3, 5, 5), a(1, 5), a(1, 1, 5, 5));
        testInter(a(1, 5), a(1, 1, 3, 3, 5, 5), a(1, 5));

        testInter(a(1, 3, 3, 5), a(1, 3, 3, 3, 3, 5, 5), a(1, 3, 3, 5));
        testInter(a(1, 3, 3, 3, 3, 5, 5), a(1, 3, 3, 5), a(1, 3, 3, 3, 3, 5, 5));
    }

    private void testInter(IntArray array1, IntArray array2, IntArray intersection) {
        assertContents(new SortedIntListIntersectionIterator(array1.iterator(), array2.iterator()), intersection);
    }

    private IntArray a(int... values) {
        return new IntArray(values);
    }

    private void assertContents(IntIterator it, IntArray values) {
        int index = 0;
        while(it.hasNext()) {
            if(index >= values.size()) {
                fail("Iterator is too long: " + s(it) + " past expected " + s(values.iterator()));
            }
            assertEquals("Wrong value at index " + index, values.get(index), it.next());
            index++;
        }
        assertEquals("Iterator is too short", values.size(), index);
    }

    private String s(IntIterator it) {
        final StringBuilder b = new StringBuilder();
        while(it.hasNext()) {
            b.append(it.next()).append(", ");
        }
        if(b.length() > 0) {
            b.setLength(b.length() - 2);
        }
        b.insert(0, '[');
        b.append(']');
        return b.toString();
    }

}
