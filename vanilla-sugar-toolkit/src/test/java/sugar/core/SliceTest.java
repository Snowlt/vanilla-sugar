package sugar.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;
import static sugar.core.Slice.slice;


class SliceTest {
    @Test
    void sliceForwardTest() {
        int[] a = toArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int[] a2 = toArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        // 正向
        assertArrayEquals(toArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), slice(a, null, null, null));
        assertArrayEquals(toArray(0, 3, 6, 9), slice(a, null, null, 3));
        assertArrayEquals(toArray(0, 3, 6, 9), slice(a, 0, 10, 3));
        assertArrayEquals(toArray(1, 4, 7, 10), slice(a, 1, null, 3));
        assertArrayEquals(toArray(2, 5, 8), slice(a, 2, null, 3));
        assertArrayEquals(toArray(0, 3, 6), slice(a, null, 9, 3));
        assertArrayEquals(toArray(0, 3, 6, 9), slice(a, null, 10, 3));
        assertArrayEquals(toArray(2, 4, 6, 8), slice(a, -9, -1, 2));
        assertArrayEquals(toArray(2, 4, 6, 8, 10), slice(a, -9, null, 2));
        assertArrayEquals(toArray(10), slice(a, -1, null, 3));
        assertArrayEquals(toArray(9), slice(a, -2, -1, 1));
        // 正向2
        assertArrayEquals(toArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), slice(a2, 0, null, 1));
        assertArrayEquals(toArray(0, 2, 4, 6, 8), slice(a2, null, null, 2));
        assertArrayEquals(toArray(0, 2, 4, 6, 8), slice(a2, 0, 10, 2));
        assertArrayEquals(toArray(0, 2, 4, 6, 8), slice(a2, 0, 9, 2));
        assertArrayEquals(toArray(0, 2, 4, 6, 8), slice(a2, -10, -1, 2));
        assertArrayEquals(toArray(1, 3, 5, 7, 9), slice(a2, 1, null, 2));
        assertArrayEquals(toArray(1, 3, 5, 7, 9), slice(a2, 1, 10, 2));
        assertArrayEquals(toArray(1, 3, 5, 7), slice(a2, 1, 9, 2));
    }

    @Test
    void sliceForwardSpecialTest() {
        int[] a = toArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // 正向空结果
        assertArrayEquals(toArray(), slice(a, 0, 0, 1));
        assertArrayEquals(toArray(), slice(a, 1, 1, 1));
        assertArrayEquals(toArray(), slice(a, 2, 2, 3));
        assertArrayEquals(toArray(), slice(a, -1, -1, 1));
        assertArrayEquals(toArray(), slice(a, -2, -2, 1));
        assertArrayEquals(toArray(), slice(a, 2, 1, 3));
        // 正向越界
        assertArrayEquals(toArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), slice(a, null, 12, null));
        assertArrayEquals(toArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), slice(a, -20, 20, null));
        assertArrayEquals(toArray(0, 3, 6, 9), slice(a, null, 12, 3));
        assertArrayEquals(toArray(1, 4, 7, 10), slice(a, 1, 14, 3));
        assertArrayEquals(toArray(), slice(a, 12, null, 3));
        assertArrayEquals(toArray(), slice(a, 12, 14, 3));
    }

    @Test
    void sliceBackwardTest() {
        int[] a = toArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int[] a2 = toArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        // 逆向切1
        assertArrayEquals(toArray(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0), slice(a, null, null, -1));
        assertArrayEquals(toArray(10, 7, 4, 1), slice(a, null, null, -3));
        assertArrayEquals(toArray(10, 7, 4), slice(a, 10, 3, -3));
        assertArrayEquals(toArray(10, 7, 4), slice(a, -1, 3, -3));
        assertArrayEquals(toArray(10, 7, 4), slice(a, -1, -9, -3));
        assertArrayEquals(toArray(9), slice(a, -2, -4, -2));
        // 逆向切2
        assertArrayEquals(toArray(9, 7, 5, 3, 1), slice(a2, null, null, -2));
        assertArrayEquals(toArray(9, 7, 5, 3, 1), slice(a2, 10, 0, -2));
        assertArrayEquals(toArray(9, 7, 5, 3, 1), slice(a2, 9, 0, -2));
        assertArrayEquals(toArray(9, 7, 5, 3), slice(a2, 9, 1, -2));
        assertArrayEquals(toArray(9, 7, 5, 3, 1), slice(a2, -1, -10, -2));
        assertArrayEquals(toArray(8, 6, 4, 2), slice(a2, -2, -10, -2));
        assertArrayEquals(toArray(8, 6, 4, 2, 0), slice(a2, -2, null, -2));
        assertArrayEquals(toArray(8, 6, 4, 2, 0), slice(a2, -2, -11, -2));
        // 逆向空结果
        assertArrayEquals(toArray(), slice(a, 1, 1, -1));
        assertArrayEquals(toArray(), slice(a, 0, 0, -1));
        assertArrayEquals(toArray(), slice(a, 0, 3, -1));
        assertArrayEquals(toArray(), slice(a, 2, 2, -3));
        assertArrayEquals(toArray(), slice(a, -1, -1, -1));
        assertArrayEquals(toArray(), slice(a, -5, -5, -3));
        // 逆向越界
        assertArrayEquals(toArray(10, 7, 4), slice(a, 12, 3, -3));
        assertArrayEquals(toArray(), slice(a, -12, 3, -3));
    }

    @Test
    void listAndCollection() {
        List<Long> list = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        assertEquals(Arrays.asList(5L, 3L), slice(list, -1, 0, -2));
        assertEquals(Arrays.asList(5L, 3L, 1L), slice(list, -1, null, -2));
        Queue<Long> queue = new ArrayDeque<>(list);
        assertEquals(Arrays.asList(5L, 3L), slice(queue, -1, 0, -2));
        assertEquals(Arrays.asList(5L, 3L, 1L), slice(queue, -1, null, -2));
    }

    @Test
    void charSequence() {
        String s = "a1b2c3d4e5f6g7";
        assertEquals("abc", slice(s, null, 5, 2));
        assertEquals("abc", slice(s, null, 6, 2));
        assertEquals("abc", slice(new StringBuilder(s), null, 6, 2));
        assertEquals("1234567", slice(new StringBuilder(s), 1, s.length(), 2));
        assertEquals("7654321", slice(new StringBuilder(s), -1, 0, -2));
    }

    @Test
    void array() {
        Character[] characters = {'A', 'B', 'C', 'D', 'E', 'F'};
        Character[] sliced1 = slice(characters, 1, null, 2);
        assertArrayEquals(new Character[]{'B', 'D', 'F'}, sliced1);
        assertEquals(Character[].class, sliced1.getClass());
        assertNotEquals(Comparable[].class, sliced1.getClass());
        double[] doubles = {1.0, 2.2, 3.3, 4.4, 5.5};
        double[] sliced2 = slice(doubles, null, null, 2);
        assertArrayEquals(new double[]{1.0, 3.3, 5.5}, sliced2);
        assertEquals(double[].class, sliced2.getClass());
        assertArrayEquals(new double[0], slice(doubles, -1, 0, null));
    }

    public static int[] toArray(int... num) {
        return num;
    }

    static class Slice2 extends Slice {
        private int start, stopIncluded, step, size;

        // 另一个版本
        private Slice2(Integer start, Integer stop, Integer step, int length) {
            this.step = step == null ? 1 : step;
            if (this.step == 0) throw new IllegalArgumentException("Argument step cannot be zero");
            // fix start(included) and stop(excluded) index range
            int stopExcluded;
            if (this.step > 0) {
                this.start = start == null ? 0 : start;
                stopExcluded = stop == null ? length : stop;
                if (stopExcluded < 0) stopExcluded += length;
            } else {
                this.start = start == null ? length - 1 : start;
                if (stop == null) {
                    stopExcluded = -1;
                } else {
                    stopExcluded = stop;
                    if (stopExcluded < 0) stopExcluded += length;
                }
            }
            if (this.start < 0) this.start += length;
            // calculate stop(included) index and size for different conditions
            if (
                    (this.step > 0 && (this.start >= stopExcluded || this.start >= length)) ||
                            (this.step < 0 && (this.start <= stopExcluded || this.start < 0))
            ) {
                // the above condition will make the result empty
                this.start = 0;
                stopIncluded = -1;
                this.size = 0;
            } else {
                // result of slice is not empty
                this.start = limit(this.start, 0, length - 1);
                if (this.step > 0) {
                    stopExcluded = limit(stopExcluded, 1, length);
                    stopIncluded = stopExcluded - 1;
                } else {
                    stopExcluded = limit(stopExcluded, -1, length - 1);
                    stopIncluded = stopExcluded + 1;
                }
                this.size = Math.abs((stopIncluded - this.start) / this.step) + 1;
            }
        }
    }
}
