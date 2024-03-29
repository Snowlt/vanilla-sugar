package sugar.core.simplification;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opentest4j.AssertionFailedError;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class CheckTest {

    @Test
    void checkBlankString() {
        assertFalse(Check.notBlank(null));
        assertFalse(Check.notBlank(""));
        assertFalse(Check.notBlank("\t\r\n "));
        assertTrue(Check.notBlank("123"));
        assertTrue(Check.notBlank(" 123 "));
        assertTrue(Check.isBlank(null));
        assertTrue(Check.isBlank(""));
        assertTrue(Check.isBlank("\t\n "));
        assertFalse(Check.isBlank("123"));
        assertFalse(Check.isBlank(" 123 "));
        // check empty
        assertFalse(Check.notEmpty((CharSequence) null));
        assertFalse(Check.notEmpty(""));
        assertTrue(Check.notEmpty(" \t\r\n "));
        assertTrue(Check.notEmpty(" 123 "));
        assertTrue(Check.isEmpty((CharSequence) null));
        assertTrue(Check.isEmpty(""));
        assertFalse(Check.isEmpty("\t\n "));
        assertFalse(Check.isEmpty(" 123 "));
    }

    @Test
    void checkBlankStrings() {
        assertTrue(Check.allBlank("", null, " \t\n "));
        assertTrue(Check.anyBlank("", "something"));
        assertTrue(Check.anyBlank(" \t\n ", "something"));
        assertTrue(Check.noneBlank("a", "b", "else"));
        assertFalse(Check.allBlank("a", " \t\n "));
        assertFalse(Check.anyBlank("a", "b", "else"));
        assertFalse(Check.noneBlank("", null, " \t\n "));
        // empty array
        assertFalse(Check.allBlank());
        assertFalse(Check.allBlank((CharSequence[]) null));
        assertFalse(Check.anyBlank());
        assertFalse(Check.anyBlank((CharSequence[]) null));
        assertTrue(Check.noneBlank());
        assertTrue(Check.noneBlank((CharSequence[]) null));
    }

    @Test
    void checkNumber() {
        BiAssertion<Number> isTrue = new BiAssertion<>(true, Check::isTrue, false, Check::notTrue);
        BiAssertion<Number> isFalse = new BiAssertion<>(false, Check::isTrue, true, Check::notTrue);
        isTrue.test((byte) -1);
        isTrue.test((short) -1);
        isTrue.test(-1);
        isTrue.test(-1L);
        isTrue.test(0.0000000001F);
        isTrue.test(0.0000000001);
        isFalse.test((byte) 0);
        isFalse.test((short) 0);
        isFalse.test(0);
        isFalse.test(0L);
        isFalse.test(0.00000000000F);
        isFalse.test(0.00000000000);
        // special types
        isTrue.test(new BigInteger("-1"));
        isTrue.test(new BigDecimal("-1.00002"));
        isTrue.test(new BigDecimal("-0.0000001"));
        isFalse.test(new BigInteger("0"));
        isFalse.test(new BigDecimal("0.0000000"));
        isTrue.test(new AtomicInteger(1));
        isFalse.test(new AtomicInteger(0));
    }

    @Test
    void checkBoolean() {
        assertTrue(Check.isTrue(true));
        assertFalse(Check.isTrue(false));
        assertFalse(Check.isTrue((Boolean) null));
        assertFalse(Check.notTrue(true));
        assertTrue(Check.notTrue(false));
        assertTrue(Check.notTrue((Boolean) null));
    }

    @Test
    void checkChar() {
        assertTrue(Check.isTrue('a'));
        assertFalse(Check.isTrue('\0'));
        assertFalse(Check.isTrue((Character) null));
        assertFalse(Check.notTrue('a'));
        assertTrue(Check.notTrue('\0'));
        assertTrue(Check.notTrue((Character) null));
    }

    @Test
    void checkObject() {
        BiAssertion<Object> isTrue = new BiAssertion<>(true, Check::isTrue, false, Check::notTrue);
        isTrue.test('a');
        isTrue.test(12.34);
        isTrue.test("123");
        isTrue.test(new int[]{1, 2});
        isTrue.test(Collections.singletonMap("any", new Object()));
        isTrue.test(new Object());
        isTrue.test(Optional.of("any"));
        isTrue.test(toEnumeration(Collections.singletonList(1)));
        BiAssertion<Object> isFalse = new BiAssertion<>(false, Check::isTrue, true, Check::notTrue);
        isFalse.test(null);
        isFalse.test(new BigDecimal("0.0000"));
        isFalse.test(new StringBuilder());
        isFalse.test('\0');
        isFalse.test(Collections.emptyList());
        isFalse.test(Collections.emptyMap());
        isFalse.test(Optional.empty());
        isFalse.test(toEnumeration(Collections.emptyList()));
    }

    @Test
    void objectEquals() {
        assertTrue(Check.equals(null, null));
        assertTrue(Check.equals(1, 1));
        assertTrue(Check.equals("aa", "aa"));
        assertTrue(Check.notEquals("aa", "ab"));
        assertTrue(Check.notEquals("aa", new Object()));
        assertTrue(Check.notEquals(new Object(), null));
    }

    @Test
    void checkTrues() {
        assertTrue(Check.allTrue(true));
        assertTrue(Check.allTrue(true, 1, "text", new Object(), new int[1], Arrays.asList(0, 1)));
        assertFalse(Check.allTrue(true, false, 2));
        assertFalse(Check.allTrue(true, null, 2));
        assertFalse(Check.allTrue());
        assertFalse(Check.allTrue((Object[]) null));
        // any
        assertTrue(Check.anyTrue(true));
        assertTrue(Check.anyTrue(true, new Object()));
        assertTrue(Check.anyTrue(true, false, false));
        assertFalse(Check.anyTrue(0, false, "", null, new ArrayList<>(), new int[0]));
        assertFalse(Check.anyTrue());
        assertFalse(Check.anyTrue((Object[]) null));
        // none
        assertTrue(Check.noneTrue(0, false, "", null, new ArrayList<>(), new int[0]));
        assertTrue(Check.noneTrue((Object) null));
        assertTrue(Check.noneTrue());
        assertTrue(Check.noneTrue((Object[]) null));
        assertFalse(Check.noneTrue(true, 0));
        assertFalse(Check.noneTrue(false, false, 1));
    }

    @Test
    void checkNulls() {
        assertTrue(Check.allNull((Object) null));
        assertTrue(Check.allNull(null, null, null));
        assertFalse(Check.allNull(null, 0, false, ""));
        assertFalse(Check.allNull());
        assertFalse(Check.allNull((Object[]) null));
        assertFalse(Check.allNull(true, 0));
        assertFalse(Check.allNull(false, false, 1));
        // any
        assertTrue(Check.anyNull((Object) null));
        assertTrue(Check.anyNull(null, null, null));
        assertTrue(Check.anyNull(null, 0, false, ""));
        assertFalse(Check.anyNull());
        assertFalse(Check.anyNull((Object[]) null));
        assertFalse(Check.anyNull(true, 0));
        assertFalse(Check.anyNull(false, false, 1));
        // none
        assertFalse(Check.noneNull((Object) null));
        assertFalse(Check.noneNull(null, null, null));
        assertFalse(Check.noneNull(null, 0, false, ""));
        assertTrue(Check.noneNull());
        assertTrue(Check.noneNull((Object[]) null));
        assertTrue(Check.noneNull(true, 0));
        assertTrue(Check.noneNull(false, false, 1));
    }

    @Test
    void contentEqualsCharSequence() {
        assertTrue(Check.contentEquals("123", new StringBuilder("123")));
        assertTrue(Check.contentEquals("", new StringBuilder()));
        assertTrue(Check.contentEquals((CharSequence) null, (CharSequence) null));
        assertFalse(Check.contentEquals("A", new StringBuilder("a")));
        assertFalse(Check.contentEquals("", new StringBuilder("456")));
        assertFalse(Check.contentEquals("0", new StringBuilder("456")));
        assertFalse(Check.contentEquals((CharSequence) null, new StringBuilder("0")));
        assertFalse(Check.contentEquals("0", (CharSequence) null));
    }

    @Test
    void equalsAsNumber() {
        assertTrue(Check.equalsAsNumber(1, 1));
        assertTrue(Check.equalsAsNumber((Number) null, (Number) null));
        assertTrue(Check.equalsAsNumber(Integer.parseInt("1"), Integer.parseInt("1")));
        assertTrue(Check.equalsAsNumber(Integer.parseInt("100"), Long.parseLong("100")));
        assertTrue(Check.equalsAsNumber(Double.parseDouble("2.1300"), Float.parseFloat("2.13")));
        assertTrue(Check.equalsAsNumber(new BigDecimal("2.1300"), new BigDecimal("2.13")));
        assertFalse(Check.equalsAsNumber(0, 1));
        assertFalse(Check.equalsAsNumber((Number) null, 0));
        assertFalse(Check.equalsAsNumber(0, (Number) null));
        assertFalse(Check.equalsAsNumber(Double.parseDouble("2.0001"), Float.parseFloat("2.0002")));
    }

    @Test
    void equalsAsString() {
        assertTrue(Check.equalsAsString((CharSequence) null, (char[]) null));
        assertTrue(Check.equalsAsString("", new char[]{}));
        assertTrue(Check.equalsAsString("A", new char[]{'A'}));
        assertTrue(Check.equalsAsString("ABC", new char[]{'A', 'B', 'C'}));
        assertTrue(Check.equalsAsString("你好, 世界", new char[]{'你', '好', ',', ' ', '世', '界'}));
        assertTrue(Check.equalsAsString("\uD83D\uDE2F", new char[]{'\uD83D', '\uDE2F'}));
        assertFalse(Check.equalsAsString((CharSequence) null, new char[]{}));
        assertFalse(Check.equalsAsString("Ab", new char[]{'A', 'B'}));
        // Reverse
        assertTrue(Check.equalsAsString((char[]) null, (CharSequence) null));
        assertTrue(Check.equalsAsString(new char[]{}, ""));
        assertTrue(Check.equalsAsString(new char[]{'A'}, "A"));
        assertTrue(Check.equalsAsString(new char[]{'A', 'B', 'C'}, "ABC"));
        assertTrue(Check.equalsAsString(new char[]{'你', '好', ',', ' ', '世', '界'}, "你好, 世界"));
        assertTrue(Check.equalsAsString(new char[]{'\uD83D', '\uDE2F'}, "\uD83D\uDE2F"));
        assertFalse(Check.equalsAsString(new char[]{}, (CharSequence) null));
        assertFalse(Check.equalsAsString(new char[]{'A', 'B'}, "Ab"));
    }

    @Test
    void equalsAsCharacter() {
        assertTrue(Check.equalsAsChar(null, (Object) null));
        assertTrue(Check.equalsAsChar('A', 'A'));
        assertTrue(Check.equalsAsChar('A', "A"));
        assertTrue(Check.equalsAsChar('A', 65));
        assertFalse(Check.equalsAsChar('\0', (Object) null));
        assertFalse(Check.equalsAsChar('A', 'B'));
        assertFalse(Check.equalsAsChar('A', "B"));
        assertFalse(Check.equalsAsChar('A', "ABC"));
        assertFalse(Check.equalsAsChar('A', 55));
    }

    @Test
    void equalsAsNumberAndCharSequence() {
        assertTrue(Check.equalsAsNumber(null, (CharSequence) null));
        assertTrue(Check.equalsAsNumber(23, "23.00"));
        assertTrue(Check.equalsAsNumber(23.001, "23.001000"));
        assertFalse(Check.equalsAsNumber(null, ""));
        assertFalse(Check.equalsAsNumber(new BigDecimal("2.001"), "AAA"));
        assertFalse(Check.equalsAsNumber(2, "AAA"));
        // Reverse
        assertTrue(Check.equalsAsNumber((CharSequence) null, null));
        assertTrue(Check.equalsAsNumber("23.00", 23));
        assertTrue(Check.equalsAsNumber("23.001000", 23.001));
        assertFalse(Check.equalsAsNumber("AAA", 2));
    }

    @Test
    void contentEqualsObject() {
        assertTrue(Check.contentEquals(null, null));
        assertFalse(Check.contentEquals(null, new Object()));
        assertFalse(Check.contentEquals(new Object(), null));
        // Number
        assertTrue(Check.contentEquals(2L, 2.0));
        assertTrue(Check.contentEquals(12.34F, 12.34D));
        assertTrue(Check.contentEquals(1.23456789, new BigDecimal("1.23456789")));
        assertTrue(Check.contentEquals(new BigDecimal("2.1300"), new BigDecimal("2.13")));
        assertFalse(Check.contentEquals(12, 12.1));
        // CharSequence
        assertTrue(Check.contentEquals(new StringBuilder("123"), new StringBuffer("123")));
        assertFalse(Check.contentEquals(new StringBuilder("123"), ""));
        // Number and CharSequence
        assertTrue(Check.contentEquals(new StringBuilder("123456.789"), 123456.789));
        assertTrue(Check.contentEquals(123456.789, new StringBuilder("123456.789")));
        assertTrue(Check.contentEquals(Long.MAX_VALUE, String.valueOf(Long.MAX_VALUE)));
        assertFalse(Check.contentEquals(654.321, new StringBuilder("654")));
        assertFalse(Check.contentEquals(new StringBuilder("654"), 654.321));
        // CharSequence and Char Array
        assertTrue(Check.contentEquals(new StringBuilder("123456.789"), "123456.789".toCharArray()));
        assertTrue(Check.contentEquals("987.654321".toCharArray(), new StringBuilder("987.654321")));
        assertFalse(Check.contentEquals("987.654321".toCharArray(), new StringBuilder()));
        // Char
        assertTrue(Check.contentEquals('A', "A"));
        assertTrue(Check.contentEquals('A', 65));
        assertFalse(Check.contentEquals('A', "AA"));
        assertFalse(Check.contentEquals('A', 'B'));
        assertFalse(Check.contentEquals('A', 66));
        assertFalse(Check.contentEquals('A', Collections.singletonList('A')));
    }

    @Test
    void contentEqualsIterable() {
        // Iterable
        assertTrue(Check.contentEquals(Arrays.asList(1, 2, 3), toQueue(1, 2, 3)));
        assertFalse(Check.contentEquals(toQueue("A"), Arrays.asList("A", "A")));
        assertFalse(Check.contentEquals(toQueue("A", "B", "C"), Arrays.asList("A", "B", "B")));
        // Array
        assertTrue(Check.contentEquals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        assertTrue(Check.contentEquals(toQueue(1, 2, 3), new int[]{1, 2, 3}));
        assertTrue(Check.contentEquals(new int[]{3, 2, 1}, Arrays.asList(3, 2, 1)));
        assertTrue(Check.contentEquals(new int[]{3, 2, 1}, new IterableWrapper<>(3, 2, 1)));
        assertTrue(Check.contentEquals(new char[]{'a', 'b', 'c'}, Arrays.asList('a', 'b', 'c')));
        assertFalse(Check.contentEquals(new int[]{1, 2}, new int[]{1, 2, 2}));
    }

    @Test
    void equalsAsSet() {
        assertTrue(Check.equalsAsSet(null, null));
        assertTrue(Check.equalsAsSet(Collections.emptySet(), Collections.emptyList()));
        assertTrue(Check.equalsAsSet(toSet("A", "B"), Arrays.asList("B", "A")));
        assertTrue(Check.equalsAsSet(toSet("A"), Arrays.asList("A", "A")));
        assertFalse(Check.equalsAsSet(null, Collections.emptyList()));
        assertFalse(Check.equalsAsSet(Collections.emptyList(), null));
        assertFalse(Check.equalsAsSet(toSet(1), Collections.singletonList("1")));
        assertFalse(Check.equalsAsSet(toSet(1, 2), Arrays.asList("1", "2")));
        assertFalse(Check.equalsAsSet(toSet("A", "B", "C"), Arrays.asList("A", "B")));
        assertFalse(Check.equalsAsSet(toSet("A", "B", "C"), Arrays.asList("A", "B", "B")));
    }

    @Test
    void equalsAsList() {
        assertTrue(Check.equalsAsList(null, null));
        assertTrue(Check.equalsAsList(toQueue("A", "B"), Arrays.asList("A", "B")));
        assertTrue(Check.equalsAsList(toQueue("A", "B", "A", "B"), Arrays.asList("A", "B", "A", "B")));
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertTrue(Check.equalsAsList(stack, Arrays.asList(1, 2, 3)));
        assertTrue(Check.equalsAsList(new TreeSet<>(Arrays.asList(2, 3, 1)), Arrays.asList(1, 2, 3)));
        assertFalse(Check.equalsAsList(toQueue(12, 34, 12, 34), Arrays.asList("12", "34", "12", "34")));
        assertFalse(Check.equalsAsList(toQueue(12F, 34F, 56F), Arrays.asList(12L, 34L, 56L)));
        assertFalse(Check.equalsAsList(toQueue(65, 66, 67), Arrays.asList('A', 'B', 'C')));
        assertFalse(Check.equalsAsList(null, Collections.emptyList()));
        assertFalse(Check.equalsAsList(Collections.emptyList(), null));
        assertFalse(Check.equalsAsList(toQueue("A"), Arrays.asList("A", "A")));
        assertFalse(Check.equalsAsList(toQueue("A", "B", "C"), Arrays.asList("A", "B", "B")));
        assertFalse(Check.equalsAsList(toQueue("A", "B", "C"), Arrays.asList("C", "B", "A")));
    }

    @Test
    void equalsAsListByPredicate() {
        BiPredicate<Object, Object> p = Check::contentEquals;
        assertTrue(Check.equalsAsList(null, null, p));
        assertTrue(Check.equalsAsList(toQueue("A", "B", "A", "B"), Arrays.asList("A", "B", "A", "B"), p));
        assertTrue(Check.equalsAsList(toQueue(12, 34, 12, 34), Arrays.asList("12", "34", "12", "34"), p));
        assertTrue(Check.equalsAsList(toQueue(12F, 34F, 56F), Arrays.asList(12L, 34L, 56L), p));
        assertTrue(Check.equalsAsList(toQueue(65, 66, 67), Arrays.asList('A', 'B', 'C'), p));
        assertFalse(Check.equalsAsList(toQueue("A"), Arrays.asList("A", "A"), p));
        assertFalse(Check.equalsAsList(toQueue("A", "B", "C"), Arrays.asList("A", "B", "B"), p));
        assertFalse(Check.equalsAsList(toQueue("A", "B", "C"), Arrays.asList("C", "B", "A"), p));
    }

    @Test
    void equalsAsIterable() {
        assertTrue(Check.equalsAsIterable(null, null));
        assertTrue(Check.equalsAsIterable(new int[]{1, 2, 3}, Arrays.asList(1, 2, 3)));
        assertTrue(Check.equalsAsIterable(new IterableWrapper<>("12", "34", "12", "34"), Arrays.asList("12", "34", "12", "34")));
        assertTrue(Check.equalsAsIterable(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B", "C")));
        assertTrue(Check.equalsAsIterable(new long[]{12, 34, 12, 34}, new IterableWrapper<>(12L, 34L, 12L, 34L)));
        assertFalse(Check.equalsAsIterable(new float[]{12F, 34F, 56F}, new long[]{12L, 34L, 56L}));
        assertFalse(Check.equalsAsIterable(new long[]{12, 34, 12, 34}, Arrays.asList("12", "34", "12", "34")));
        assertFalse(Check.equalsAsIterable(new long[]{12, 34, 12, 34}, new IterableWrapper<>("12", "34", "12", "34")));
        assertFalse(Check.equalsAsIterable(new int[1], null));
        assertFalse(Check.equalsAsIterable(new String[]{"A"}, Arrays.asList("A", "A")));
        assertFalse(Check.equalsAsIterable(new IterableWrapper<>(), null));
        assertFalse(Check.equalsAsIterable(new String[]{"A"}, new IterableWrapper<>("A", "A")));
        assertFalse(Check.equalsAsIterable(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B", "X")));
        assertFalse(Check.equalsAsIterable(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B")));
        assertThrows(IllegalArgumentException.class, () -> Check.equalsAsIterable(new int[1], "string"));
    }

    @Test
    void equalsAsIterableByPredicate() {
        BiPredicate<Object, Object> p = Check::contentEquals;
        assertTrue(Check.equalsAsIterable(null, null, p));
        assertTrue(Check.equalsAsIterable(new int[]{1, 2, 3}, Arrays.asList(1, 2, 3), p));
        assertTrue(Check.equalsAsIterable(new float[]{12F, 34F, 56F}, new long[]{12L, 34L, 56L}, p));
        assertTrue(Check.equalsAsIterable(new long[]{12, 34, 12, 34}, new IterableWrapper<>("12", "34", "12", "34"), p));
        assertTrue(Check.equalsAsIterable(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B", "C"), p));
        assertFalse(Check.equalsAsIterable(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B", "X"), p));
        assertFalse(Check.equalsAsIterable(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B"), p));
        assertThrows(IllegalArgumentException.class, () -> Check.equalsAsIterable(new int[1], "string", p));
    }

    @SafeVarargs
    static <T> Set<T> toSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    @SafeVarargs
    static <T> Queue<T> toQueue(T... elements) {
        return new ArrayDeque<>(Arrays.asList(elements));
    }

    public static <T> Enumeration<T> toEnumeration(Iterable<T> iterable) {
        Iterator<T> iterator = iterable.iterator();
        return new Enumeration<T>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();
            }
        };
    }

    public static class IterableWrapper<T> implements Iterable<T> {
        private final List<T> content;

        @SafeVarargs
        public IterableWrapper(T... elements) {
            content = Arrays.asList(elements);
        }

        @Override
        public Iterator<T> iterator() {
            return content.iterator();
        }
    }

    public static class BiAssertion<T> {
        private final Predicate<T> first, second;
        private final boolean firstExpected, secondExcepted;

        public BiAssertion(boolean firstExpected, Predicate<T> first, boolean secondExcepted, Predicate<T> second) {
            this.firstExpected = firstExpected;
            this.first = first;
            this.secondExcepted = secondExcepted;
            this.second = second;
        }

        public void test(T value) {
            try {
                assertEquals(firstExpected, first.test(value));
                assertEquals(secondExcepted, second.test(value));
            } catch (AssertionFailedError e) {
                String msg = "Method return an unexpected value for parameter: " + value.toString() +
                        ", parameter type: " + value.getClass().getName();
                throw new AssertionFailedError(msg, e.getExpected(), e.getActual());
            }
        }

    }

}