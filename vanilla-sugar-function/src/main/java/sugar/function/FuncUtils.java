package sugar.function;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 提供 {@link Stream} 对象的辅助方法
 * <p>无特殊注明外，大部分方法的参数都可接受 null</p>
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
     * 将多个集合对象转换成 {@link Stream} 并拼接，并将 null 从流中过滤掉（{@code stream.filter(Objects::nonNull)}）
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
     * 将可迭代对象转换成 {@link Stream}，并将 null 从流中过滤掉（{@code stream.filter(Objects::nonNull)}）
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

    /**
     * 返回一个将元素累加到 {@link HashMap} 的 {@link Collector}，流中的元素将分别使用指定的函数来映射为 Map 的 Key 和 Value 。
     * <p>如果在添加映射元素时遇到了重复的 Key，则使用参数 {@code mergeFunction} 提供的函数来合并结果。</p>
     *
     * <p>类似 {@link java.util.stream.Collectors#toMap(Function, Function, BinaryOperator)}，但不同之处在于这个方法可以处理
     * Key / Value 中的 {@code null} 值（使用了 HashMap 的特性）。</p>
     * <p>由于实现方式不同，如果不需要处理 null 值，则推荐优先选择使用 {@code Collectors.toMap(...)} 方法。</p>
     *
     * @param <T>           流中的元素类型
     * @param <K>           Map 中 Key 的类型
     * @param <V>           Map 中 Value 的类型
     * @param keyMapper     将元素转为 Map 中 Key 的映射函数
     * @param valueMapper   将元素转为 Map 中 Value 的映射函数
     * @param mergeFunction 存入 Map 的 Key 冲突时的 Value 合并方法
     * @return 生成 HashMap 的 Collector
     * @throws NullPointerException 如果任意参数为空
     */
    public static <T, K, V> Collector<T, ?, HashMap<K, V>> collectHashMap(Function<T, K> keyMapper,
                                                                          Function<T, V> valueMapper,
                                                                          BinaryOperator<V> mergeFunction) {
        return collectMap(HashMap::new, keyMapper, valueMapper, mergeFunction);
    }

    /**
     * 返回一个将元素累加到 {@link EnumMap} 的 {@link Collector}，流中的元素将分别使用指定的函数来映射为 Map 的 Key 和 Value 。
     * <p>如果在添加映射元素时遇到了重复的 Key，则使用参数 {@code mergeFunction} 提供的函数来合并结果。</p>
     *
     * <p>类似 {@link java.util.stream.Collectors#toMap(Function, Function, BinaryOperator)}，但不同之处在于这个方法可以处理
     * Value 中的 {@code null} 值（EnumMap 的 Key 不能为 null 但 Value 可以为 null）。</p>
     *
     * @param <T>           流中的元素类型
     * @param <K>           Map 中 Key 的类型
     * @param <V>           Map 中 Value 的类型
     * @param keyMapper     将元素转为 Map 中 Key 的映射函数
     * @param valueMapper   将元素转为 Map 中 Value 的映射函数
     * @param mergeFunction 存入 Map 的 Key 冲突时的 Value 合并方法
     * @return 生成 HashMap 的 Collector
     * @throws NullPointerException 如果任意参数为空
     */
    public static <T, K extends Enum<K>, V>
    Collector<T, ?, EnumMap<K, V>> collectEnumMap(Class<K> keyClass,
                                                  Function<T, K> keyMapper,
                                                  Function<T, V> valueMapper,
                                                  BinaryOperator<V> mergeFunction) {
        Objects.requireNonNull(keyClass);
        return collectMap(() -> new EnumMap<>(keyClass), keyMapper, valueMapper, mergeFunction);
    }

    static <T, K, V, M extends Map<K, V>> Collector<T, ?, M> collectMap(Supplier<M> supplier, Function<T, K> keyMapper,
                                                                        Function<T, V> valueMapper,
                                                                        BinaryOperator<V> mergeFunction) {
        Objects.requireNonNull(keyMapper);
        Objects.requireNonNull(valueMapper);
        Objects.requireNonNull(mergeFunction);
        return Collector.of(supplier,
                (map, t) -> {
                    K key = keyMapper.apply(t);
                    V newValue = valueMapper.apply(t);
                    V oldValue = map.get(key);
                    if (oldValue != null || map.containsKey(key)) {
                        map.put(key, mergeFunction.apply(oldValue, newValue));
                    } else {
                        map.put(key, newValue);
                    }
                },
                (map1, map2) -> {
                    for (Map.Entry<K, V> entry : map2.entrySet()) {
                        K key = entry.getKey();
                        V newValue = entry.getValue();
                        V oldValue = map1.get(key);
                        if (oldValue != null || map1.containsKey(key)) {
                            map1.put(key, mergeFunction.apply(oldValue, newValue));
                        } else {
                            map1.put(key, newValue);
                        }
                    }
                    return map1;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

}
