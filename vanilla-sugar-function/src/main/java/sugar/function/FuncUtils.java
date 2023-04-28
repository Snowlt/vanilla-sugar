package sugar.function;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 提供 {@link Stream} 对象的辅助方法，且入参是 null-safe
 *
 * @author SnowLT
 */
public class FuncUtils {
    private FuncUtils() {
    }

    /**
     * 将集合对象转换成 {@link Stream}，如果对象为 null 将返回一个空流
     *
     * @param <T>        元素的类型
     * @param collection 集合（可为 null）
     * @return Stream
     */
    public static <T> Stream<T> stream(Collection<T> collection) {
        return collection == null || collection.isEmpty() ? Stream.empty() : collection.stream();
    }

    /**
     * 将多个集合对象转换成 {@link Stream} 并拼接为一个流
     *
     * @param <T>         元素的类型
     * @param collections 多个集合（可包含 null）
     * @return Stream
     */
    @SafeVarargs
    public static <T> Stream<T> stream(Collection<T>... collections) {
        return collections == null || collections.length == 0 ? Stream.empty()
                : Arrays.stream(collections).filter(Objects::nonNull).flatMap(Collection::stream);
    }

    /**
     * 将可迭代对象转换成 {@link Stream}，如果对象为 null 将返回一个空流
     *
     * @param <T>      元素的类型
     * @param iterable 可迭代对象（可为 null）
     * @return Stream
     */
    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return iterable == null ? Stream.empty()
                : StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * 将集合对象转换成 {@link Stream}，如果对象为 null 将返回一个空流
     *
     * @param <T>        元素的类型
     * @param collection 集合（可为 null）
     * @return Stream
     */
    public static <T> Stream<T> nonNullStream(Collection<T> collection) {
        return stream(collection).filter(Objects::nonNull);
    }

    /**
     * 将多个集合对象转换成 {@link Stream} 并拼接，流中不包含 null 对象
     *
     * @param <T>         元素的类型
     * @param collections 多个集合（可包含 null）
     * @return Stream
     * @see #stream(Collection[])
     */
    @SafeVarargs
    public static <T> Stream<T> nonNullStream(Collection<T>... collections) {
        return stream(collections).filter(Objects::nonNull);
    }

    /**
     * 将可迭代对象转换成 {@link Stream}，流中不包含 null 对象
     *
     * @param <T>      元素的类型
     * @param iterable 可迭代对象（可为 null）
     * @return Stream
     */
    public static <T> Stream<T> nonNullStream(Iterable<T> iterable) {
        return stream(iterable).filter(Objects::nonNull);
    }

    /**
     * 生成一个包含 {@link Map} 中的所有 Key 的 {@link Stream}，如果对象为 null 将返回一个空流
     *
     * @param <T> 元素的类型
     * @param map Map 对象（可为 null）
     * @return Stream
     */
    public static <T> Stream<T> streamFromKey(Map<T, ?> map) {
        return map == null ? Stream.empty() : map.keySet().stream();
    }

    /**
     * 生成一个包含 {@link Map} 中的所有 Value 的 {@link Stream}，如果对象为 null 将返回一个空流
     *
     * @param <T> 元素的类型
     * @param map Map 对象（可为 null）
     * @return Stream
     */
    public static <T> Stream<T> streamFromValue(Map<?, T> map) {
        return map == null ? Stream.empty() : map.values().stream();
    }

    /**
     * 返回一个生成 {@link ArrayList} 的 {@link Collectors}
     * <p>例如: <pre>{@code
     *     Stream<Integer> stream = Stream.of(1, 2, 3);
     *     ArrayList<Integer> list = stream.collect(FuncUtils.collectArrayList());
     * }</pre>
     *
     * @param <T> 元素的类型
     * @return Collector
     */
    public static <T> Collector<T, ?, ArrayList<T>> collectArrayList() {
        return Collectors.toCollection(ArrayList::new);
    }

    /**
     * 返回一个生成 {@link LinkedList} 的 {@link Collectors}
     * <p>例如: <pre>{@code
     *     Stream<Integer> stream = Stream.of(1, 2, 3);
     *     LinkedList<Integer> list = stream.collect(FuncUtils.collectLinkedList());
     * }</pre>
     *
     * @param <T> 元素的类型
     * @return Collector
     */
    public static <T> Collector<T, ?, LinkedList<T>> collectLinkedList() {
        return Collectors.toCollection(LinkedList::new);
    }

    /**
     * 返回一个生成 {@link HashSet} 的 {@link Collectors}
     * <p>例如: <pre>{@code
     *     Stream<Integer> stream = Stream.of(1, 2, 3);
     *     HashSet<Integer> list = stream.collect(FuncUtils.collectHashSet());
     * }</pre>
     *
     * @param <T> 元素的类型
     * @return Collector
     */
    public static <T> Collector<T, ?, HashSet<T>> collectHashSet() {
        return Collectors.toCollection(HashSet::new);
    }

}
