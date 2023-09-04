package sugar.function;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class FuncUtilsTest {

    @Test
    void buildFromCollection() {
        Stream<Integer> stream = FuncUtils.stream(Arrays.asList(1, 2, null));
        assert !stream.isParallel();
        assertEquals(Arrays.asList(1, 2, null), stream.collect(Collectors.toList()));
        assertFalse(FuncUtils.stream((Collection<Object>) null).findAny().isPresent());
        assertFalse(FuncUtils.stream(new ArrayDeque<>(2)).findAny().isPresent());
    }

    @Test
    void buildFromCollections() {
        Stream<Integer> stream = FuncUtils.stream(
                Arrays.asList(1, 2, null), Arrays.asList(4, 5, null), Collections.singleton(6));
        assert !stream.isParallel();
        assertEquals(Arrays.asList(1, 2, null, 4, 5, null, 6), stream.collect(Collectors.toList()));
        assertFalse(FuncUtils.stream((Collection<Object>[]) null).findAny().isPresent());
        assertFalse(FuncUtils.stream().findAny().isPresent());
    }

    @Test
    void buildFromIterable() {
        Stream<Integer> stream = FuncUtils.stream(new IterableWrapper<>(1, 2, null));
        assert !stream.isParallel();
        assertEquals(Arrays.asList(1, 2, null), stream.collect(Collectors.toList()));
        assertFalse(FuncUtils.stream((Iterable<Object>) null).findAny().isPresent());
        assertFalse(FuncUtils.stream(new IterableWrapper<>()).findAny().isPresent());
    }

    @Test
    void buildNonNullStream() {
        Stream<Integer> stream = FuncUtils.nonNullStream(Arrays.asList(1, null, 2));
        assert !stream.isParallel();
        assertEquals(Arrays.asList(1, 2), stream.collect(Collectors.toList()));
        stream = FuncUtils.nonNullStream(Arrays.asList(1, 2, null), Collections.singleton(3));
        assert !stream.isParallel();
        assertEquals(Arrays.asList(1, 2, 3), stream.collect(Collectors.toList()));
        stream = FuncUtils.nonNullStream(new IterableWrapper<>(1, null, 22, null, 33));
        assert !stream.isParallel();
        assertEquals(Arrays.asList(1, 22, 33), stream.collect(Collectors.toList()));
    }

    @Test
    void streamForMap() {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "A");
        map.put(2, "B");
        map.put(3, "C");
        List<Integer> keys = FuncUtils.streamFromKey(map).collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 2, 3), keys);
        List<String> values = FuncUtils.streamFromValue(map).collect(Collectors.toList());
        assertEquals(Arrays.asList("A", "B", "C"), values);
    }

    @Test
    void collectArrayList() {
        ArrayList<Integer> list = Stream.of(1, 2, null).collect(FuncUtils.collectArrayList());
        assertEquals(Arrays.asList(1, 2, null), list);
    }

    @Test
    void collectLinkedList() {
        LinkedList<Integer> list = Stream.of(1, 2, null).collect(FuncUtils.collectLinkedList());
        assertEquals(Arrays.asList(1, 2, null), list);
    }

    @Test
    void collectHashSet() {
        HashSet<Integer> list = Stream.of(1, 2, null).collect(FuncUtils.collectHashSet());
        assertEquals(new HashSet<>(Arrays.asList(1, 2, null)), list);
    }

    @Test
    void collectHashMap() {
        List<Data> examples = Collections.unmodifiableList(Arrays.asList(
                new Data(0, "X"), new Data(1, null), new Data(2, "An"),
                new Data(3, "X"), new Data(2, " is"),
                new Data(1, "able"), new Data(2, "land")
        ));
        HashMap<Integer, String> map = new HashMap<>();
        map.put(0, "X");
        map.put(1, null);
        map.put(2, "An");
        map.put(3, "X");
        assertEquals(map, examples.stream().parallel().collect(FuncUtils.collectHashMap(Data::index, Data::value,
                (a, b) -> a)));
        map.clear();
        map.put(0, "X");
        map.put(1, "able");
        map.put(2, "land");
        map.put(3, "X");
        assertEquals(map, examples.stream().parallel().collect(FuncUtils.collectHashMap(Data::index, Data::value,
                (a, b) -> b)));
        map.clear();
        map.put(0, "X");
        map.put(1, "nullable");
        map.put(2, "An island");
        map.put(3, "X");
        assertEquals(map, examples.stream().parallel().collect(FuncUtils.collectHashMap(Data::index, Data::value,
                (a, b) -> a + b)));
        List<Data> examples2 = Collections.unmodifiableList(Arrays.asList(
                new Data(1, "1"), new Data(1, "2"), new Data(1, "3"),
                new Data(1, "4"), new Data(1, "5"), new Data(1, "6")
        ));
        assertEquals(Collections.singletonMap(1, "123456"), examples2.stream().parallel().collect(
                FuncUtils.collectHashMap(Data::index, Data::value, String::concat)));
    }

    @Test
    void toEnumMap() {
        Class<TestEnum> c = TestEnum.class;
        EnumMap<TestEnum, String> example = new EnumMap<>(c);
        example.put(TestEnum.ZERO, TestEnum.ZERO.name());
        example.put(TestEnum.ONE, TestEnum.ONE.name());
        example.put(TestEnum.TWO, TestEnum.TWO.name());
        assertEquals(example, Arrays.stream(TestEnum.values()).collect(FuncUtils.collectEnumMap(c,
                Function.identity(), Enum::name, (a, b) -> a)));
        example.remove(TestEnum.TWO);
        assertEquals(example, Arrays.stream(TestEnum.values()).collect(FuncUtils.collectEnumMap(c,
                e -> e == TestEnum.TWO ? TestEnum.ZERO : e, e -> e == TestEnum.TWO ? null : e.name(),
                (a, b) -> a)));
        example.put(TestEnum.ZERO, null);
        assertEquals(example, Arrays.stream(TestEnum.values()).collect(FuncUtils.collectEnumMap(c,
                e -> e == TestEnum.TWO ? TestEnum.ZERO : e, e -> e == TestEnum.TWO ? null : e.name(),
                (a, b) -> b)));
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

    public static class Data {
        private final Integer index;
        private final String value;

        public Data(Integer index, String value) {
            this.index = index;
            this.value = value;
        }

        public Integer index() {
            return index;
        }

        public String value() {
            return value;
        }
    }

    public enum TestEnum {
        ZERO, ONE, TWO
    }
}