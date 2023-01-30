package sugar.core.simplification;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;

import static org.junit.jupiter.api.Assertions.*;

class CheckTest {

    @Test
    void checkString() {
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
    void checkNumber() {
        assertTrue(Check.isTrue((byte) -1));
        assertTrue(Check.isTrue((short) -1));
        assertTrue(Check.isTrue(-1));
        assertTrue(Check.isTrue(-1L));
        assertTrue(Check.isTrue(0.0000000001F));
        assertTrue(Check.isTrue(0.0000000001));
        assertFalse(Check.isTrue((byte) 0));
        assertFalse(Check.isTrue((short) 0));
        assertFalse(Check.isTrue(0));
        assertFalse(Check.isTrue(0L));
        assertFalse(Check.isTrue(0.00000000000F));
        assertFalse(Check.isTrue(0.00000000000));
        // special types
        assertTrue(Check.isTrue(new BigInteger("-1")));
        assertFalse(Check.isTrue(new BigInteger("0")));
        assertTrue(Check.isTrue(new BigDecimal("-1.00002")));
        assertTrue(Check.isTrue(new BigDecimal("-0.0000001")));
        assertFalse(Check.isTrue(new BigDecimal("0.0000000")));
        assertTrue(Check.isTrue(new AtomicInteger(1)));
        assertFalse(Check.isTrue(new AtomicInteger(0)));
    }

    @Test
    void allTrue() {
        assertTrue(Check.allTrue(true));
        assertTrue(Check.allTrue(true, 1, "text", new Object(), new int[1], Arrays.asList(0, 1)));
        assertFalse(Check.allTrue(true, false, 2));
        assertFalse(Check.allTrue(true, null, 2));
        assertFalse(Check.allTrue());
        assertFalse(Check.allTrue((Object[]) null));
    }

    @Test
    void anyTrue() {
        assertTrue(Check.anyTrue(true));
        assertTrue(Check.anyTrue(true, new Object()));
        assertTrue(Check.anyTrue(true, false, false));
        assertFalse(Check.anyTrue(0, false, "", null, new ArrayList<>(), new int[0]));
        assertFalse(Check.anyTrue());
        assertFalse(Check.anyTrue((Object[]) null));
    }

    @Test
    void noneTrue() {
        assertTrue(Check.noneTrue(0, false, "", null, new ArrayList<>(), new int[0]));
        assertTrue(Check.noneTrue((Object) null));
        assertTrue(Check.noneTrue());
        assertTrue(Check.noneTrue((Object[]) null));
        assertFalse(Check.noneTrue(true, 0));
        assertFalse(Check.noneTrue(false, false, 1));
    }

    @Test
    void allNull() {
        assertTrue(Check.allNull((Object) null));
        assertTrue(Check.allNull(null, null, null));
        assertFalse(Check.allNull(null, 0, false, ""));
        assertFalse(Check.allNull());
        assertFalse(Check.allNull((Object[]) null));
        assertFalse(Check.allNull(true, 0));
        assertFalse(Check.allNull(false, false, 1));
    }

    @Test
    void anyNull() {
        assertTrue(Check.anyNull((Object) null));
        assertTrue(Check.anyNull(null, null, null));
        assertTrue(Check.anyNull(null, 0, false, ""));
        assertFalse(Check.anyNull());
        assertFalse(Check.anyNull((Object[]) null));
        assertFalse(Check.anyNull(true, 0));
        assertFalse(Check.anyNull(false, false, 1));
    }

    @Test
    void noneNull() {
        assertFalse(Check.noneNull((Object) null));
        assertFalse(Check.noneNull(null, null, null));
        assertFalse(Check.noneNull(null, 0, false, ""));
        assertTrue(Check.noneNull());
        assertTrue(Check.noneNull((Object[]) null));
        assertTrue(Check.noneNull(true, 0));
        assertTrue(Check.noneNull(false, false, 1));
    }

    @Test
    void equalsCharSequence() {
        assertTrue(Check.equals("123", new StringBuilder("123")));
        assertTrue(Check.equals("", new StringBuilder()));
        assertTrue(Check.equals((CharSequence) null, (CharSequence) null));
        assertFalse(Check.equals("A", new StringBuilder("a")));
        assertFalse(Check.equals("", new StringBuilder("456")));
        assertFalse(Check.equals("0", new StringBuilder("456")));
        assertFalse(Check.equals((CharSequence) null, new StringBuilder("0")));
        assertFalse(Check.equals("0", (CharSequence) null));
    }

    @Test
    void equalsNumber() {
        assertTrue(Check.equals(1, 1));
        assertTrue(Check.equals((Number) null, (Number) null));
        assertTrue(Check.equals(Integer.parseInt("1"), Integer.parseInt("1")));
        assertTrue(Check.equals(Integer.parseInt("100"), Long.parseLong("100")));
        assertTrue(Check.equals(Double.parseDouble("2.1300"), Float.parseFloat("2.13")));
        assertTrue(Check.equals(new BigDecimal("2.1300"), new BigDecimal("2.13")));
        assertFalse(Check.equals(0, 1));
        assertFalse(Check.equals(null, 0));
        assertFalse(Check.equals(0, null));
        assertFalse(Check.equals(Double.parseDouble("2.0001"), Float.parseFloat("2.0002")));
    }

    @Test
    void contentEqualsCharSequence() {
        assertTrue(Check.equals((CharSequence) null, (char[]) null));
        assertTrue(Check.equals("", new char[]{}));
        assertTrue(Check.equals("A", new char[]{'A'}));
        assertTrue(Check.equals("ABC", new char[]{'A', 'B', 'C'}));
        assertTrue(Check.equals("你好, 世界", new char[]{'你', '好', ',', ' ', '世', '界'}));
        assertTrue(Check.equals("\uD83D\uDE2F", new char[]{'\uD83D', '\uDE2F'}));
        assertFalse(Check.equals((CharSequence) null, new char[]{}));
        assertFalse(Check.equals("Ab", new char[]{'A', 'B'}));
        // Reverse
        assertTrue(Check.equals((char[]) null, (CharSequence) null));
        assertTrue(Check.equals(new char[]{}, ""));
        assertTrue(Check.equals(new char[]{'A'}, "A"));
        assertTrue(Check.equals(new char[]{'A', 'B', 'C'}, "ABC"));
        assertTrue(Check.equals(new char[]{'你', '好', ',', ' ', '世', '界'}, "你好, 世界"));
        assertTrue(Check.equals(new char[]{'\uD83D', '\uDE2F'}, "\uD83D\uDE2F"));
        assertFalse(Check.equals(new char[]{}, (CharSequence) null));
        assertFalse(Check.equals(new char[]{'A', 'B'}, "Ab"));
    }

    @Test
    void contentEqualsCharacter() {
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
    void contentEqualsNumberAndCharSequence() {
        assertTrue(Check.equalsAsNumber((Number) null, (CharSequence) null));
        assertTrue(Check.equalsAsNumber(23, "23.00"));
        assertTrue(Check.equalsAsNumber(23.001, "23.001000"));
        assertFalse(Check.equalsAsNumber((Number) null, ""));
        assertFalse(Check.equalsAsNumber(new BigDecimal("2.001"), "AAA"));
        assertFalse(Check.equalsAsNumber(2, "AAA"));
        // Reverse
        assertTrue(Check.equalsAsNumber((CharSequence) null, (Number) null));
        assertTrue(Check.equalsAsNumber("23.00", 23));
        assertTrue(Check.equalsAsNumber("23.001000", 23.001));
        assertFalse(Check.equalsAsNumber("AAA", 2));
    }

    @Test
    void contentEqualsObject() {
        assertTrue(Check.contentEquals((Object) null, (Object) null));
        assertFalse(Check.contentEquals((Object) null, new Object()));
        assertFalse(Check.contentEquals(new Object(), (Object) null));
        // Number
        assertTrue(Check.contentEquals((Object) 2L, (Object) 2.0));
        assertTrue(Check.contentEquals((Object) 12.34F, (Object) 12.34D));
        assertTrue(Check.contentEquals((Object) 1.23456789, (Object) new BigDecimal("1.23456789")));
        assertTrue(Check.contentEquals((Object) new BigDecimal("2.1300"), (Object) new BigDecimal("2.13")));
        assertFalse(Check.contentEquals((Object) 12, (Object) 12.1));
        // CharSequence
        assertTrue(Check.contentEquals((Object) new StringBuilder("123"), (Object) new StringBuffer("123")));
        assertFalse(Check.contentEquals((Object) new StringBuilder("123"), (Object) ""));
        // Number and CharSequence
        assertTrue(Check.contentEquals((Object) new StringBuilder("123456.789"), (Object) 123456.789));
        assertTrue(Check.contentEquals((Object) 123456.789, (Object) new StringBuilder("123456.789")));
        assertTrue(Check.contentEquals((Object) Long.MAX_VALUE, (Object) String.valueOf(Long.MAX_VALUE)));
        assertFalse(Check.contentEquals((Object) 654.321, (Object) new StringBuilder("654")));
        assertFalse(Check.contentEquals((Object) new StringBuilder("654"), (Object) 654.321));
        // CharSequence and Char Array
        assertTrue(Check.contentEquals((Object) new StringBuilder("123456.789"), (Object) "123456.789".toCharArray()));
        assertTrue(Check.contentEquals((Object) "987.654321".toCharArray(), (Object) new StringBuilder("987.654321")));
        assertFalse(Check.contentEquals((Object) "987.654321".toCharArray(), (Object) new StringBuilder()));
        // Char
        assertTrue(Check.contentEquals((Object) 'A', (Object) "A"));
        assertTrue(Check.contentEquals((Object) 'A', (Object) 65));
        assertFalse(Check.contentEquals((Object) 'A', (Object) "AA"));
        assertFalse(Check.contentEquals((Object) 'A', (Object) 'B'));
        assertFalse(Check.contentEquals((Object) 'A', (Object) 66));
    }

    @Test
    void contentEqualsSet() {
        assertTrue(Check.contentEqualsAsSet(null, null));
        assertTrue(Check.contentEqualsAsSet(Collections.emptySet(), Collections.emptyList()));
        assertTrue(Check.contentEqualsAsSet(toSet("A", "B"), Arrays.asList("B", "A")));
        assertTrue(Check.contentEqualsAsSet(toSet("A"), Arrays.asList("A", "A")));
        assertFalse(Check.contentEqualsAsSet(null, Collections.emptyList()));
        assertFalse(Check.contentEqualsAsSet(Collections.emptyList(), null));
        assertFalse(Check.contentEqualsAsSet(toSet(1), Collections.singletonList("1")));
        assertFalse(Check.contentEqualsAsSet(toSet(1, 2), Arrays.asList("1", "2")));
        assertFalse(Check.contentEqualsAsSet(toSet("A", "B", "C"), Arrays.asList("A", "B")));
        assertFalse(Check.contentEqualsAsSet(toSet("A", "B", "C"), Arrays.asList("A", "B", "B")));
    }

    @Test
    void collectionEquals() {
        assertTrue(Check.collectionEquals(null, null));
        assertTrue(Check.collectionEquals(toQueue("A", "B"), Arrays.asList("A", "B")));
        assertTrue(Check.collectionEquals(toQueue("A", "B", "A", "B"), Arrays.asList("A", "B", "A", "B")));
        assertFalse(Check.collectionEquals(toQueue(12, 34, 12, 34), Arrays.asList("12", "34", "12", "34")));
        assertFalse(Check.collectionEquals(toQueue(12F, 34F, 56F), Arrays.asList(12L, 34L, 56L)));
        assertFalse(Check.collectionEquals(toQueue(65, 66, 67), Arrays.asList('A', 'B', 'C')));
        assertFalse(Check.collectionEquals(null, Collections.emptyList()));
        assertFalse(Check.collectionEquals(Collections.emptyList(), null));
        assertFalse(Check.collectionEquals(toQueue("A"), Arrays.asList("A", "A")));
        assertFalse(Check.collectionEquals(toQueue("A", "B", "C"), Arrays.asList("A", "B", "B")));
        assertFalse(Check.collectionEquals(toQueue("A", "B", "C"), Arrays.asList("C", "B", "A")));
    }

    @Test
    void collectionEqualsByPredicate() {
        BiPredicate<Object, Object> p = Check::contentEquals;
        assertTrue(Check.collectionEquals(null, null, p));
        assertTrue(Check.collectionEquals(toQueue("A", "B", "A", "B"), Arrays.asList("A", "B", "A", "B"), p));
        assertTrue(Check.collectionEquals(toQueue(12, 34, 12, 34), Arrays.asList("12", "34", "12", "34"), p));
        assertTrue(Check.collectionEquals(toQueue(12F, 34F, 56F), Arrays.asList(12L, 34L, 56L), p));
        assertTrue(Check.collectionEquals(toQueue(65, 66, 67), Arrays.asList('A', 'B', 'C'), p));
        assertFalse(Check.collectionEquals(toQueue("A"), Arrays.asList("A", "A"), p));
        assertFalse(Check.collectionEquals(toQueue("A", "B", "C"), Arrays.asList("A", "B", "B"), p));
        assertFalse(Check.collectionEquals(toQueue("A", "B", "C"), Arrays.asList("C", "B", "A"), p));
    }

    @Test
    void contentEqualsIterable() {
        assertTrue(Check.contentEqualsAsCollection(null, null));
        assertTrue(Check.contentEqualsAsCollection(new int[]{1, 2, 3}, Arrays.asList(1, 2, 3)));
        assertTrue(Check.contentEqualsAsCollection(new IterableWrapper<>("12", "34", "12", "34"), Arrays.asList("12", "34", "12", "34")));
        assertTrue(Check.contentEqualsAsCollection(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B", "C")));
        assertTrue(Check.contentEqualsAsCollection(new long[]{12, 34, 12, 34}, new IterableWrapper<>(12L, 34L, 12L, 34L)));
        assertFalse(Check.contentEqualsAsCollection(new float[]{12F, 34F, 56F}, new long[]{12L, 34L, 56L}));
        assertFalse(Check.contentEqualsAsCollection(new long[]{12, 34, 12, 34}, Arrays.asList("12", "34", "12", "34")));
        assertFalse(Check.contentEqualsAsCollection(new long[]{12, 34, 12, 34}, new IterableWrapper<>("12", "34", "12", "34")));
        assertFalse(Check.contentEqualsAsCollection(new int[1], null));
        assertFalse(Check.contentEqualsAsCollection(new String[]{"A"}, Arrays.asList("A", "A")));
        assertFalse(Check.contentEqualsAsCollection(new IterableWrapper<>(), null));
        assertFalse(Check.contentEqualsAsCollection(new String[]{"A"}, new IterableWrapper<>("A", "A")));
        assertFalse(Check.contentEqualsAsCollection(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B", "X")));
        assertFalse(Check.contentEqualsAsCollection(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B")));
        assertThrows(IllegalArgumentException.class, () -> Check.contentEqualsAsCollection(new int[1], "string"));
    }

    @Test
    void contentEqualsIterableByPredicate() {
        BiPredicate<Object, Object> p = Check::contentEquals;
        assertTrue(Check.contentEqualsAsCollection(null, null, p));
        assertTrue(Check.contentEqualsAsCollection(new int[]{1, 2, 3}, Arrays.asList(1, 2, 3), p));
        assertTrue(Check.contentEqualsAsCollection(new float[]{12F, 34F, 56F}, new long[]{12L, 34L, 56L}, p));
        assertTrue(Check.contentEqualsAsCollection(new long[]{12, 34, 12, 34}, new IterableWrapper<>("12", "34", "12", "34"), p));
        assertTrue(Check.contentEqualsAsCollection(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B", "C"), p));
        assertFalse(Check.contentEqualsAsCollection(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B", "X"), p));
        assertFalse(Check.contentEqualsAsCollection(new IterableWrapper<>("A", "B", "C"), new IterableWrapper<>("A", "B"), p));
        assertThrows(IllegalArgumentException.class, () -> Check.contentEqualsAsCollection(new int[1], "string", p));
    }

    @SafeVarargs
    static <T> Set<T> toSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    @SafeVarargs
    static <T> Queue<T> toQueue(T... elements) {
        return new ArrayDeque<>(Arrays.asList(elements));
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
}