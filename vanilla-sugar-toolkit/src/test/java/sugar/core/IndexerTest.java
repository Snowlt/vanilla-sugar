package sugar.core;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.MethodName.class)
class IndexerTest {

    final static String[] ARRAY = new String[]{"hello", "code", "world"};
    final static List<Integer> LIST = new LinkedList<>(Arrays.asList(1, 2, 3));
    final static Deque<Integer> DEQUE = new ArrayDeque<>(Arrays.asList(1, 2, 3));
    final static Iterable<Integer> ITER = new IterableWrapper<>(1, 2, 3);
    final static CharSequence STRING = new StringBuilder("To be, or not to be");

    final static String[] EMPTY_ARRAY = new String[0];
    final static List<Integer> EMPTY_LIST = Collections.emptyList();
    final static Deque<Integer> EMPTY_DEQUE = new ArrayDeque<>();
    final static Iterable<Integer> EMPTY_ITER = new IterableWrapper<>();
    final static CharSequence EMPTY_STRING = new StringBuilder();

    final static String DEF_STRING = "default";
    final static int DEF_INT = 0;
    final static char DEF_CHAR = 'd';

    @Test
    void at() {
        assertEquals('T', Indexer.at(STRING, 0));
        assertEquals(1, Indexer.at(LIST, 0));
        assertEquals("hello", Indexer.at(ARRAY, 0));
        assertEquals('e', Indexer.at(STRING, -1));
        assertEquals(3, Indexer.at(LIST, -1));
        assertEquals("world", Indexer.at(ARRAY, -1));
        // Middle element
        assertEquals('r', Indexer.at(STRING, 8));
        assertEquals('r', Indexer.at(STRING, -11));
        assertEquals(2, Indexer.at(LIST, 1));
        assertEquals(2, Indexer.at(LIST, -2));
        assertEquals("code", Indexer.at(ARRAY, 1));
        assertEquals("code", Indexer.at(ARRAY, -2));
    }

    @Test
    void atWithException() {
        runOob(() -> Indexer.at(STRING, STRING.length()));
        runOob(() -> Indexer.at(LIST, LIST.size()));
        runOob(() -> Indexer.at(ARRAY, ARRAY.length));
        runOob(() -> Indexer.at(STRING, STRING.length() + 2));
        runOob(() -> Indexer.at(LIST, LIST.size() + 2));
        runOob(() -> Indexer.at(ARRAY, ARRAY.length + 2));
        runOob(() -> Indexer.at(STRING, -STRING.length() - 2));
        runOob(() -> Indexer.at(LIST, -LIST.size() - 2));
        runOob(() -> Indexer.at(ARRAY, -ARRAY.length - 2));
        runOob(() -> Indexer.at(EMPTY_STRING, -2));
        runOob(() -> Indexer.at(EMPTY_LIST, -2));
        runOob(() -> Indexer.at(EMPTY_ARRAY, -2));
        runOob(() -> Indexer.at((CharSequence) null, -2));
        runOob(() -> Indexer.at((String[]) null, -2));
        runOob(() -> Indexer.at((List<Integer>) null, -2));
    }

    @Test
    void atForEmptyWithException() {
        runOob(() -> Indexer.at(EMPTY_STRING, 0));
        runOob(() -> Indexer.at(EMPTY_LIST, 0));
        runOob(() -> Indexer.at(EMPTY_ARRAY, 0));
        runOob(() -> Indexer.at((CharSequence) null, 0));
        runOob(() -> Indexer.at((String[]) null, 0));
        runOob(() -> Indexer.at((List<Integer>) null, 0));
        runOob(() -> Indexer.at(EMPTY_STRING, -1));
        runOob(() -> Indexer.at(EMPTY_LIST, -1));
        runOob(() -> Indexer.at(EMPTY_ARRAY, -1));
        runOob(() -> Indexer.at((CharSequence) null, -1));
        runOob(() -> Indexer.at((String[]) null, -1));
        runOob(() -> Indexer.at((List<Integer>) null, -1));
    }

    @Test
    void atForIterable() {
        assertEquals(1, Indexer.at(ITER, 0));
        assertEquals(2, Indexer.at(ITER, 1));
        assertEquals(3, Indexer.at(ITER, 2));
        assertEquals(1, Indexer.at(ITER, -3));
        assertEquals(2, Indexer.at(ITER, -2));
        assertEquals(3, Indexer.at(ITER, -1));
        runOob(() -> Indexer.at((Iterable<Integer>) null, 0));
        runOob(() -> Indexer.at((Iterable<Integer>) null, -1));
        // forward out of bounds
        runOob(() -> Indexer.at(DEQUE, DEQUE.size()));
        runOob(() -> Indexer.at(DEQUE, DEQUE.size() + 2));
        final int iterSize = ((IterableWrapper<?>) ITER).size();
        runOob(() -> Indexer.at(ITER, iterSize));
        runOob(() -> Indexer.at(ITER, iterSize + 2));
        // backward out of bounds
        runOob(() -> Indexer.at(DEQUE, -DEQUE.size() - 2));
        runOob(() -> Indexer.at(ITER, -iterSize - 1));
        runOob(() -> Indexer.at(ITER, -iterSize - 2));
    }

    @Test
    void atOrDefault() {
        assertEquals(DEF_CHAR, Indexer.atOrDefault(EMPTY_STRING, 0, DEF_CHAR));
        assertEquals(DEF_INT, Indexer.atOrDefault(EMPTY_LIST, 0, DEF_INT));
        assertEquals(DEF_STRING, Indexer.atOrDefault(EMPTY_ARRAY, 0, DEF_STRING));
        assertEquals(DEF_CHAR, Indexer.atOrDefault(EMPTY_STRING, 2, DEF_CHAR));
        assertEquals(DEF_INT, Indexer.atOrDefault(EMPTY_LIST, 2, DEF_INT));
        assertEquals(DEF_STRING, Indexer.atOrDefault(EMPTY_ARRAY, 2, DEF_STRING));
        assertEquals(DEF_CHAR, Indexer.atOrDefault(EMPTY_STRING, -1, DEF_CHAR));
        assertEquals(DEF_INT, Indexer.atOrDefault(EMPTY_LIST, -1, DEF_INT));
        assertEquals(DEF_STRING, Indexer.atOrDefault(EMPTY_ARRAY, -1, DEF_STRING));
        // for iterable
        assertEquals(DEF_INT, Indexer.atOrDefault(EMPTY_ITER, 0, DEF_INT));
        assertEquals(DEF_INT, Indexer.atOrDefault(EMPTY_ITER, 2, DEF_INT));
        assertEquals(DEF_INT, Indexer.atOrDefault(EMPTY_ITER, -1, DEF_INT));
    }

    @Test
    void atForPrimitive() {
        assertEquals(2, Indexer.at(new byte[]{1, 2, 3, 4, 5, 6}, 1));
        assertEquals(2, Indexer.at(new short[]{1, 2, 3, 4, 5, 6}, 1));
        assertEquals(2, Indexer.at(new int[]{1, 2, 3, 4, 5, 6}, 1));
        assertEquals(2, Indexer.at(new long[]{1, 2, 3, 4, 5, 6}, 1));
        assertEquals(2.2F, Indexer.at(new float[]{1.1F, 2.2F, 3.3F, 4.4F, 5.5F, 6.6F}, 1));
        assertEquals(2.2, Indexer.at(new double[]{1.1, 2.2, 3.3, 4.4, 5.5, 6.6}, 1));
        assertEquals(5, Indexer.at(new byte[]{1, 2, 3, 4, 5, 6}, -2));
        assertEquals(5, Indexer.at(new short[]{1, 2, 3, 4, 5, 6}, -2));
        assertEquals(5, Indexer.at(new int[]{1, 2, 3, 4, 5, 6}, -2));
        assertEquals(5, Indexer.at(new long[]{1, 2, 3, 4, 5, 6}, -2));
        assertEquals(5.5F, Indexer.at(new float[]{1.1F, 2.2F, 3.3F, 4.4F, 5.5F, 6.6F}, -2));
        assertEquals(5.5, Indexer.at(new double[]{1.1, 2.2, 3.3, 4.4, 5.5, 6.6}, -2));
        // Exception
        runOob(() -> Indexer.at((byte[]) null, 1));
        runOob(() -> Indexer.at((short[]) null, 1));
        runOob(() -> Indexer.at((int[]) null, 1));
        runOob(() -> Indexer.at((long[]) null, 1));
        runOob(() -> Indexer.at((float[]) null, 1));
        runOob(() -> Indexer.at((double[]) null, 1));
    }

    @Test
    void first() {
        assertEquals('T', Indexer.first(STRING));
        assertEquals(1, Indexer.first(LIST));
        assertEquals(1, Indexer.first(DEQUE));
        assertEquals(1, Indexer.first(ITER));
        assertEquals("hello", Indexer.first(ARRAY));
        assertEquals(DEF_CHAR, Indexer.firstOrDefault(EMPTY_STRING, DEF_CHAR));
        assertEquals(DEF_INT, Indexer.firstOrDefault(EMPTY_LIST, DEF_INT));
        assertEquals(DEF_INT, Indexer.firstOrDefault(EMPTY_DEQUE, DEF_INT));
        assertEquals(DEF_INT, Indexer.firstOrDefault(EMPTY_ITER, DEF_INT));
        assertEquals(DEF_STRING, Indexer.firstOrDefault(EMPTY_ARRAY, DEF_STRING));
        // Exception
        runOob(() -> Indexer.first(EMPTY_STRING));
        runOob(() -> Indexer.first(EMPTY_LIST));
        runOob(() -> Indexer.first(EMPTY_DEQUE));
        runOob(() -> Indexer.first(EMPTY_ITER));
        runOob(() -> Indexer.first(EMPTY_ARRAY));
        runOob(() -> Indexer.first((CharSequence) null));
        runOob(() -> Indexer.first((String[]) null));
        runOob(() -> Indexer.first((List<Integer>) null));
        runOob(() -> Indexer.first((Deque<Integer>) null));
        runOob(() -> Indexer.first((Iterable<Integer>) null));
    }

    @Test
    void last() {
        assertEquals('e', Indexer.last(STRING));
        assertEquals(3, Indexer.last(LIST));
        assertEquals(3, Indexer.last(DEQUE));
        assertEquals(3, Indexer.last(ITER));
        assertEquals("world", Indexer.last(ARRAY));
        assertEquals(DEF_CHAR, Indexer.lastOrDefault(EMPTY_STRING, DEF_CHAR));
        assertEquals(DEF_INT, Indexer.lastOrDefault(EMPTY_LIST, DEF_INT));
        assertEquals(DEF_INT, Indexer.lastOrDefault(EMPTY_DEQUE, DEF_INT));
        assertEquals(DEF_INT, Indexer.lastOrDefault(EMPTY_ITER, DEF_INT));
        assertEquals(DEF_STRING, Indexer.lastOrDefault(EMPTY_ARRAY, DEF_STRING));
        // Exception
        runOob(() -> Indexer.last(EMPTY_STRING));
        runOob(() -> Indexer.last(EMPTY_LIST));
        runOob(() -> Indexer.last(EMPTY_DEQUE));
        runOob(() -> Indexer.last(EMPTY_ITER));
        runOob(() -> Indexer.last(EMPTY_ARRAY));
        runOob(() -> Indexer.last((CharSequence) null));
        runOob(() -> Indexer.last((String[]) null));
        runOob(() -> Indexer.last((List<Integer>) null));
        runOob(() -> Indexer.last((Deque<Integer>) null));
        runOob(() -> Indexer.last((Iterable<Integer>) null));
    }

    static void runOob(Supplier<?> supplier) {
        try {
            supplier.get();
            fail("Should not run");
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    public static class IterableWrapper<T> implements Iterable<T> {
        private final List<T> content;

        public int size() {
            return content.size();
        }

        @SafeVarargs
        public IterableWrapper(T... elements) {
            content = Arrays.asList(elements);
        }

        @Override
        public Iterator<T> iterator() {
            return content.iterator();
        }
    }
}