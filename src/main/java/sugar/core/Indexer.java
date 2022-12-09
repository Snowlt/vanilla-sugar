package sugar.core;

import java.util.*;

/**
 * 索引器，可根据下标获取数组 / List 等有序（可迭代）对象中元素，如果无法访问时抛出 {@link IndexOutOfBoundsException}
 * <p>
 * 主要提供了以下增强方法：
 * <ul>
 *     <li><b>at()</b> 通过下标获取元素，支持逆向访问（使用负数下标表示从右往左访问）</li>
 *     <li><b>atOrDefault()</b> 尝试获取元素（支持逆向访问），不抛出异常，例如 {@link #atOrDefault(List, int, Object)}</li>
 *     <li><b>first()</b> 获取首个元素</li>
 *     <li><b>last()</b> 获取尾部元素</li>
 * </ul>
 * </p>
 * 设计思想来自 Python / Javascript 中的索引器{@code [*]} 和 .Net 的 System.Linq.Enumerable
 * <pre>
 *  例如:
 *      a = [1, 2, 3, 4, 5] -> a[0] = 1, a[-1] = 5
 *      s = 'ABC' -> s[0] = 'A', s[1] = 'B' s[-1] = 'C'
 * </pre>
 *
 * @author SnowLT
 * @version 1.0
 */
public class Indexer {

    /**
     * 获取字符序列中指定下标处的字符(可用负数索引)
     * <pre>
     * 例如：
     *      at("ABC", 0) -> 'A'
     *      at("ABC", -1) -> 'C'
     * </pre>
     *
     * @param cs    字符序列
     * @param index 下标(负数表示从右侧选取)
     * @return 指定位置的字符
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static char at(CharSequence cs, int index) {
        if (cs == null) oob();
        return cs.charAt(normalIndex(cs.length(), index));
    }

    /**
     * 获取列表中指定下标处的元素(可用负数索引)
     * <pre>
     * 例如：
     *      at({1, 2, 3}, 0) -> 1
     *      at({1, 2, 3}, -1) -> 3
     * </pre>
     *
     * @param <T>   泛型
     * @param list  列表
     * @param index 下标(负数表示从右侧选取)
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static <T> T at(List<T> list, int index) {
        if (list == null) oob();
        return list.get(normalIndex(list.size(), index));
    }

    /**
     * 按顺序访问可迭代对象，获取指定下标处的元素(可用负数索引)
     * <pre>
     * 例如：
     *      at({1, 2, 3}, 0) -> 1
     *      at({1, 2, 3}, -1) -> 3
     * </pre>
     * <p>操作耗时最大为O(n)，n是对象的总长度</p>
     *
     * @param <T>      泛型
     * @param iterable 可迭代对象
     * @param index    下标(负数表示从右侧选取)
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static <T> T at(Iterable<T> iterable, int index) {
        if (iterable == null) oob();
        if (iterable instanceof List) return at((List<T>) iterable, index);
        if (index >= 0) return iterateTo(iterable.iterator(), index);
        if (iterable instanceof Collection) {
            index = normalIndex(((Collection<T>) iterable).size(), index);
            return iterateTo(iterable.iterator(), index);
        }
        List<T> list = new ArrayList<>();
        for (T t : iterable) {
            list.add(t);
        }
        return at(list, index);
    }

    /**
     * 获取数组中指定下标处的元素(可用负数索引)
     * <pre>
     * 例如：
     *      at({1, 2, 3}, 0) -> 1
     *      at({1, 2, 3}, -1) -> 3
     * </pre>
     *
     * @param <T>   泛型
     * @param array 数组
     * @param index 下标(负数表示从右侧选取)
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static <T> T at(T[] array, int index) {
        if (array == null) oob();
        return array[normalIndex(array.length, index)];
    }

    /**
     * 获取数组中指定下标处的元素(可用负数索引)
     *
     * @param array 数组
     * @param index 下标(负数表示从右侧选取)
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static byte at(byte[] array, int index) {
        if (array == null) oob();
        return array[normalIndex(array.length, index)];
    }

    /**
     * 获取数组中指定下标处的元素(可用负数索引)
     *
     * @param array 数组
     * @param index 下标(负数表示从右侧选取)
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static short at(short[] array, int index) {
        if (array == null) oob();
        return array[normalIndex(array.length, index)];
    }

    /**
     * 获取数组中指定下标处的元素(可用负数索引)
     *
     * @param array 数组
     * @param index 下标(负数表示从右侧选取)
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static int at(int[] array, int index) {
        if (array == null) oob();
        return array[normalIndex(array.length, index)];
    }

    /**
     * 获取数组中指定下标处的元素(可用负数索引)
     *
     * @param array 数组
     * @param index 下标(负数表示从右侧选取)
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static long at(long[] array, int index) {
        if (array == null) oob();
        return array[normalIndex(array.length, index)];
    }

    /**
     * 获取数组中指定下标处的元素(可用负数索引)
     *
     * @param array 数组
     * @param index 下标(负数表示从右侧选取)
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static float at(float[] array, int index) {
        if (array == null) oob();
        return array[normalIndex(array.length, index)];
    }

    /**
     * 获取数组中指定下标处的元素(可用负数索引)
     *
     * @param array 数组
     * @param index 下标(负数表示从右侧选取)
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static double at(double[] array, int index) {
        if (array == null) oob();
        return array[normalIndex(array.length, index)];
    }

    /**
     * 获取数组中指定下标处的元素(可用负数索引)
     *
     * @param array 数组
     * @param index 下标(负数表示从右侧选取)
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 超出范围时抛出
     */
    public static char at(char[] array, int index) {
        if (array == null) oob();
        return array[normalIndex(array.length, index)];
    }

    /**
     * 尝试获取字符序列中指定下标处的字符(可用负数索引)，不抛出异常
     * <pre>
     * 例如：
     *      atOrDefault("ABC", -1, null) -> 'C'
     *      atOrDefault(null, -1, 'B') -> 'B'
     * </pre>
     *
     * @param cs           字符序列
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的字符
     */
    public static char atOrDefault(CharSequence cs, int index, char defaultValue) {
        try {
            return at(cs, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取列表中指定下标处的元素(可用负数索引)，不抛出异常
     * <pre>
     * 例如：
     *      atOrDefault({1, 2, 3}, -1, null) -> 3
     *      atOrDefault(null, -1, 2) -> 2
     * </pre>
     *
     * @param <T>          泛型
     * @param list         列表
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的元素
     */
    public static <T> T atOrDefault(List<T> list, int index, T defaultValue) {
        try {
            return at(list, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 按顺序访问可迭代对象，获取指定下标处的元素(可用负数索引)，不抛出异常
     * <pre>
     * 例如：
     *      atOrDefault({1, 2, 3}, -1, null) -> 3
     *      atOrDefault(null, -1, 2) -> 2
     * </pre>
     * <p>(这是一个耗时接近O(n)的操作)</p>
     *
     * @param <T>          泛型
     * @param iterable     可迭代对象
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的元素
     */
    public static <T> T atOrDefault(Iterable<T> iterable, int index, T defaultValue) {
        try {
            return at(iterable, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取数组中指定下标处的元素(可用负数索引)，不抛出异常
     * <pre>
     * 例如：
     *      atOrDefault({1, 2, 3}, -1, null) -> 3
     *      atOrDefault(null, -1, 2) -> 2
     * </pre>
     *
     * @param <T>          泛型
     * @param array        数组
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的元素
     */
    public static <T> T atOrDefault(T[] array, int index, T defaultValue) {
        try {
            return at(array, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取数组中指定下标处的元素(可用负数索引)，不抛出异常
     *
     * @param array        数组
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的元素
     */
    public static byte atOrDefault(byte[] array, int index, byte defaultValue) {
        try {
            return at(array, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取数组中指定下标处的元素(可用负数索引)，不抛出异常
     *
     * @param array        数组
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的元素
     */
    public static short atOrDefault(short[] array, int index, short defaultValue) {
        try {
            return at(array, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取数组中指定下标处的元素(可用负数索引)，不抛出异常
     *
     * @param array        数组
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的元素
     */
    public static int atOrDefault(int[] array, int index, int defaultValue) {
        try {
            return at(array, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取数组中指定下标处的元素(可用负数索引)，不抛出异常
     *
     * @param array        数组
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的元素
     */
    public static long atOrDefault(long[] array, int index, long defaultValue) {
        try {
            return at(array, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取数组中指定下标处的元素(可用负数索引)，不抛出异常
     *
     * @param array        数组
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的元素
     */
    public static float atOrDefault(float[] array, int index, float defaultValue) {
        try {
            return at(array, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取数组中指定下标处的元素(可用负数索引)，不抛出异常
     *
     * @param array        数组
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的元素
     */
    public static double atOrDefault(double[] array, int index, double defaultValue) {
        try {
            return at(array, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取数组中指定下标处的元素(可用负数索引)，不抛出异常
     *
     * @param array        数组
     * @param index        下标(负数表示从右侧选取)
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 指定位置的元素
     */
    public static char atOrDefault(char[] array, int index, char defaultValue) {
        try {
            return at(array, index);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 获取字符序列中的首个字符，等效 {@code at(cs, 0)}
     * <pre>
     * 例如：
     *      first("ABC") -> 'A'
     * </pre>
     *
     * @param cs 字符序列
     * @return 首个字符
     * @throws IndexOutOfBoundsException 字符序列为 null 或下标越界时抛出
     */
    public static char first(CharSequence cs) {
        if (cs == null || cs.length() == 0) oob();
        return cs.charAt(0);
    }

    /**
     * 获取列表中的首个元素，等效 {@code at(list, 0)}
     * <pre>
     * 例如：
     *      first({1, 2, 3}) -> 1
     * </pre>
     *
     * @param <T>  泛型
     * @param list 列表
     * @return 首个元素
     * @throws IndexOutOfBoundsException 列表为 null 或下标越界时抛出
     */
    public static <T> T first(List<T> list) {
        if (list == null || list.isEmpty()) oob();
        return list.get(0);
    }

    /**
     * 获取可迭代对象中的首个元素，等效 {@code at(iterable, 0)}
     * <pre>
     * 例如：
     *      first({1, 2, 3}) -> 1
     * </pre>
     * <p>这是一个耗时为O(1)的操作</p>
     *
     * @param <T>      泛型
     * @param iterable 可迭代对象
     * @return 首个元素
     * @throws IndexOutOfBoundsException 可迭代对象为 null 或下标越界时抛出
     */
    public static <T> T first(Iterable<T> iterable) {
        if (iterable == null) oob();
        if (iterable instanceof List) return first((List<T>) iterable);
        if (iterable instanceof Queue) {
            Queue<T> queue = (Queue<T>) iterable;
            if (queue.isEmpty()) oob();
            return queue.peek();
        }
        Iterator<T> iterator = iterable.iterator();
        if (!iterator.hasNext()) oob();
        return iterator.next();
    }

    /**
     * 获取数组中的首个元素，等效 {@code at(array, 0)}
     * <pre>
     * 例如：
     *      first({1, 2, 3}) -> 1
     * </pre>
     *
     * @param <T>   泛型
     * @param array 数组
     * @return 首个元素
     * @throws IndexOutOfBoundsException 数组为 null 或下标越界时抛出
     */
    public static <T> T first(T[] array) {
        if (array == null || array.length == 0) oob();
        return array[0];
    }

    /**
     * 尝试获取字符序列中的首个字符，不抛出异常，等效 {@code atOrDefault(cs, 0, defaultValue)}
     * <pre>
     * 例如：
     *      firstOrDefault("ABC", null) -> 'A'
     *      firstOrDefault(null, 'B') -> 'B'
     * </pre>
     *
     * @param cs           字符序列
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 首个字符
     */
    public static char firstOrDefault(CharSequence cs, char defaultValue) {
        try {
            return cs.charAt(0);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取列表中的首个元素，不抛出异常，等效 {@code atOrDefault(list, 0, defaultValue)}
     * <pre>
     * 例如：
     *      firstOrDefault({1, 2, 3}, null) -> 1
     *      firstOrDefault(null, 2) -> 2
     * </pre>
     *
     * @param <T>          泛型
     * @param list         列表
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 首个元素
     */
    public static <T> T firstOrDefault(List<T> list, T defaultValue) {
        try {
            return list.get(0);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取可迭代对象中的首个元素，不抛出异常，等效 {@code atOrDefault(iterable, 0, defaultValue)}
     * <pre>
     * 例如：
     *      firstOrDefault({1, 2, 3}, null) -> 1
     *      firstOrDefault(null, 2) -> 2
     * </pre>
     * <p>这是一个耗时为O(1)的操作</p>
     *
     * @param <T>          泛型
     * @param iterable     可迭代对象
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 首个元素
     */
    public static <T> T firstOrDefault(Iterable<T> iterable, T defaultValue) {
        try {
            return first(iterable);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取数组中的首个元素，不抛出异常，等效 {@code atOrDefault(array, 0, defaultValue)}
     * <pre>
     * 例如：
     *      firstOrDefault({1, 2, 3}, null) -> 1
     *      firstOrDefault(null, 2) -> 2
     * </pre>
     *
     * @param <T>          泛型
     * @param array        数组
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 首个元素
     */
    public static <T> T firstOrDefault(T[] array, T defaultValue) {
        try {
            return array[0];
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 获取字符序列中的末尾字符，等效 {@code at(cs, -1)}
     * <pre>
     * 例如：
     *      last("ABC") -> 'C'
     * </pre>
     *
     * @param cs 字符序列
     * @return 末尾字符
     * @throws IndexOutOfBoundsException 字符序列为 null 或下标越界时抛出
     */
    public static char last(CharSequence cs) {
        if (cs == null || cs.length() == 0) oob();
        return cs.charAt(cs.length() - 1);
    }

    /**
     * 获取列表中的末尾元素，等效 {@code at(list, -1)}
     * <pre>
     * 例如：
     *      last({1, 2, 3}) -> 3
     * </pre>
     *
     * @param <T>  泛型
     * @param list 列表
     * @return 末尾元素
     * @throws IndexOutOfBoundsException 列表为 null 或下标越界时抛出
     */
    public static <T> T last(List<T> list) {
        if (list == null || list.isEmpty()) oob();
        return list.get(list.size() - 1);
    }

    /**
     * 获取可迭代对象中的末尾元素，等效 {@code at(iterable, -1)}
     * <pre>
     * 例如：
     *      last({1, 2, 3}) -> 3
     * </pre>
     * <p>操作耗时: <ul>
     * <li>{@link Deque}: 一般为 O(1)，同 {@link Deque#peekLast()} 耗时</li>
     * <li>{@link List}: 一般为 O(1)，同 {@link List#get(int)} 耗时</li>
     * <li>其他: O(n)，n是对象的总长度</li>
     * </ul></p>
     *
     * @param <T>      泛型
     * @param iterable 可迭代对象
     * @return 末尾元素
     * @throws IndexOutOfBoundsException 可迭代对象为 null 或下标越界时抛出
     */
    public static <T> T last(Iterable<T> iterable) {
        if (iterable == null) oob();
        if (iterable instanceof Deque) {
            Deque<T> deque = (Deque<T>) iterable;
            if (!deque.isEmpty()) return deque.peekLast();
        }
        if (iterable instanceof List) return last((List<T>) iterable);
        for (Iterator<T> iterator = iterable.iterator(); iterator.hasNext(); ) {
            T last = iterator.next();
            if (!iterator.hasNext()) return last;
        }
        oob();
        throw new IndexOutOfBoundsException();
    }

    /**
     * 获取数组中的末尾元素，等效 {@code at(array, -1)}
     * <pre>
     * 例如：
     *      last({1, 2, 3}) -> 3
     * </pre>
     *
     * @param <T>   泛型
     * @param array 数组
     * @return 末尾元素
     * @throws IndexOutOfBoundsException 数组为 null 或下标越界时抛出
     */
    public static <T> T last(T[] array) {
        if (array == null || array.length == 0) oob();
        return array[array.length - 1];
    }

    /**
     * 尝试获取字符序列中的末尾字符，不抛出异常，等效 {@code atOrDefault(cs, -1, defaultValue)}
     * <pre>
     * 例如：
     *      lastOrDefault("ABC", null) -> 'C'
     *      lastOrDefault(null, 'B') -> 'B'
     * </pre>
     *
     * @param cs           字符序列
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 末尾字符
     */
    public static char lastOrDefault(CharSequence cs, char defaultValue) {
        try {
            return cs.charAt(cs.length() - 1);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取列表中的末尾元素，不抛出异常，等效 {@code atOrDefault(list, -1, defaultValue)}
     * <pre>
     * 例如：
     *      lastOrDefault({1, 2, 3}, null) -> 3
     *      lastOrDefault(null, 2) -> 2
     * </pre>
     *
     * @param <T>          泛型
     * @param list         列表
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 末尾元素
     */
    public static <T> T lastOrDefault(List<T> list, T defaultValue) {
        try {
            return list.get(list.size() - 1);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取可迭代对象中的末尾元素，不抛出异常，等效 {@code atOrDefault(iterable, -1, defaultValue)}
     * <pre>
     * 例如：
     *      lastOrDefault({1, 2, 3}, null) -> 3
     *      lastOrDefault(null, 2) -> 2
     * </pre>
     * <p>这是一个耗时为O(n)的操作，n是对象的总长度</p>
     *
     * @param <T>          泛型
     * @param iterable     可迭代对象
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 末尾元素
     */
    public static <T> T lastOrDefault(Iterable<T> iterable, T defaultValue) {
        try {
            return last(iterable);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * 尝试获取数组中的末尾元素，不抛出异常，等效 {@code atOrDefault(array, -1, defaultValue)}
     * <pre>
     * 例如：
     *      lastOrDefault({1, 2, 3}, null) -> 3
     *      lastOrDefault(null, 2) -> 2
     * </pre>
     *
     * @param <T>          泛型
     * @param array        数组
     * @param defaultValue 无法获取指定元素时返回的默认值
     * @return 末尾元素
     */
    public static <T> T lastOrDefault(T[] array, T defaultValue) {
        try {
            return array[array.length - 1];
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /* 内部方法 */

    protected static int normalIndex(int length, int index) {
        int i = index >= 0 ? index : length + index;
        if (i >= length || i < 0) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        return i;
    }

    /**
     * 迭代到指定的下标
     * 对 LinkedList 等类使用迭代器查找的时间和空间消耗会更小
     *
     * @param iterator 新的迭代器
     * @param index    下标(>=0)
     * @return 下标对应元素
     */
    static <T> T iterateTo(Iterator<T> iterator, int index) {
        for (int i = 0; i < index && iterator.hasNext(); i++) iterator.next();
        if (!iterator.hasNext()) oob();
        return iterator.next();
    }

    private static void oob() {
        throw new IndexOutOfBoundsException("Index out of range");
    }

    protected Indexer() {
    }

}