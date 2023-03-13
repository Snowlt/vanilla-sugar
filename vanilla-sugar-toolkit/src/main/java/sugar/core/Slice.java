package sugar.core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 切片，为可索引对象提供增强的裁剪等功能
 * <p>每次切片操作的返回值都是新对象，不会对源对象产生副作用。
 * 可以用于替代 {@link List#subList(int, int)}, {@link String#substring(int, int)},
 * {@link java.util.Collections#reverse(List)}, {@link StringBuilder#reverse()} 等常用方法。
 * 支持为以下对象进行切片：
 * <ul>
 *     <li>{@link CharSequence} 字符序列</li>
 *     <li>{@link List} 列表</li>
 *     <li>{@link Collection} 集合（会转为列表进行处理）</li>
 *     <li>对象数组({@code Object[]}) / 基本类型数组({@code int[], double[]} 等)</li>
 * </ul></p>
 *
 * <p>主要用法为：{@code Slice.slice(target, start, stop, step)}</p>
 * <p>参数作用如下：<ul>
 *     <li>{@code target} 进行切片操作的源对象: 不可为 null，否则会抛出 {@link NullPointerException}</li>
 *     <li>{@code start} 操作的起点下标(包含): 下标从0开始，负数下标表示从右往左数，null 表示不做限制</li>
 *     <li>{@code stop} 操作的结束下标(不包含): 下标从0开始，负数下标表示从右往左数，null 表示不做限制</li>
 *     <li>{@code step} 步长: 正数表示从左到右操作，负数表示从右到左操作，null 表示取默认值 1；
 *     不可为 0，否则会抛出 {@link IllegalArgumentException}</li>
 * </ul></p>
 *
 * <p>设计思路来自 Python 中的切片操作，使用方式也相同</p>
 * <pre>
 *  例如:
 *     int[] a = new int[] {1, 2, 3, 4, 5};
 *     int[] b = Slice.slice(s, 1, 4, null);     // 截取 -> b = [2, 3, 4]
 *     int[] c = Slice.slice(s, null, null, -1); // 逆序 -> c = [5, 4, 3, 2, 1]
 *  对应 Python 中:
 *     a = [1, 2, 3, 4, 5]
 *     b = a[1:4]           # -> b = [2, 3, 4]
 *     c = a[::-1]          # -> c = [5, 4, 3, 2, 1]
 * </pre>
 *
 * @author SnowLT
 * @version 1.2
 */
public class Slice {
    private int start;
    private int stopIncluded;
    private int step;
    private int size;

    /**
     * 对列表进行切片操作
     *
     * @param <T>   元素类型(泛型)
     * @param list  列表
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 新生成的列表
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static <T> List<T> slice(List<T> list, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, list.size());
        ArrayList<T> ts = new ArrayList<>(slice.size);
        slice.forEachIndex((i, n) -> ts.add(list.get(i)));
        return ts;
    }

    /**
     * 对集合对象进行切片操作(先转化为List在进行访问)
     *
     * @param <T>        元素类型(泛型)
     * @param collection 集合
     * @param start      起始下标(包含)
     * @param stop       结束下标(不包含)
     * @param step       步长(默认为1，不能为0)
     * @return 新生成的列表
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static <T> Collection<T> slice(Collection<T> collection, Integer start, Integer stop, Integer step) {
        List<T> list = collection instanceof List ? (List<T>) collection : new ArrayList<>(collection);
        return slice(list, start, stop, step);
    }

    /**
     * 对字符序列进行切片操作
     *
     * @param cs    字符序列
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 处理后生成的字符串
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static String slice(CharSequence cs, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, cs.length());
        char[] chars = new char[slice.size];
        slice.forEachIndex((i, n) -> chars[n] = cs.charAt(i));
        return new String(chars);
    }

    /**
     * 对数组进行切片操作
     *
     * @param <T>   元素类型(泛型)
     * @param array 数组
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 新生成的数组
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] slice(T[] array, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, array.length);
        T[] a = (T[]) Array.newInstance(array.getClass().getComponentType(), slice.size);
        slice.forEachIndex((i, n) -> a[n] = array[i]);
        return a;
    }

    /**
     * 对数组进行切片操作
     *
     * @param array 数组
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 新生成的数组
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static byte[] slice(byte[] array, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, array.length);
        byte[] a = new byte[slice.size];
        slice.forEachIndex((i, n) -> a[n] = array[i]);
        return a;
    }

    /**
     * 对数组进行切片操作
     *
     * @param array 数组
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 新生成的数组
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static short[] slice(short[] array, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, array.length);
        short[] a = new short[slice.size];
        slice.forEachIndex((i, n) -> a[n] = array[i]);
        return a;
    }

    /**
     * 对数组进行切片操作
     *
     * @param array 数组
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 新生成的数组
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static int[] slice(int[] array, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, array.length);
        int[] a = new int[slice.size];
        slice.forEachIndex((i, n) -> a[n] = array[i]);
        return a;
    }

    /**
     * 对数组进行切片操作
     *
     * @param array 数组
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 新生成的数组
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static long[] slice(long[] array, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, array.length);
        long[] a = new long[slice.size];
        slice.forEachIndex((i, n) -> a[n] = array[i]);
        return a;
    }

    /**
     * 对数组进行切片操作
     *
     * @param array 数组
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 新生成的数组
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static float[] slice(float[] array, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, array.length);
        float[] a = new float[slice.size];
        slice.forEachIndex((i, n) -> a[n] = array[i]);
        return a;
    }

    /**
     * 对数组进行切片操作
     *
     * @param array 数组
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 新生成的数组
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static double[] slice(double[] array, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, array.length);
        double[] a = new double[slice.size];
        slice.forEachIndex((i, n) -> a[n] = array[i]);
        return a;
    }

    /**
     * 对数组进行切片操作
     *
     * @param array 数组
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 新生成的数组
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static char[] slice(char[] array, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, array.length);
        char[] a = new char[slice.size];
        slice.forEachIndex((i, n) -> a[n] = array[i]);
        return a;
    }

    /**
     * 对数组进行切片操作
     *
     * @param array 数组
     * @param start 起始下标(包含)
     * @param stop  结束下标(不包含)
     * @param step  步长(默认为1，不能为0)
     * @return 新生成的数组
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    public static boolean[] slice(boolean[] array, Integer start, Integer stop, Integer step) {
        Slice slice = new Slice(start, stop, step, array.length);
        boolean[] a = new boolean[slice.size];
        slice.forEachIndex((i, n) -> a[n] = array[i]);
        return a;
    }

    protected Slice() {
    }

    /**
     * 初始化一个切片对象
     *
     * @param start  起始下标(包含)
     * @param stop   结束下标(不包含)
     * @param step   步长(默认为1，不能为0)
     * @param length 长度
     * @throws IllegalArgumentException 当步长为 0 时抛出
     */
    protected Slice(Integer start, Integer stop, Integer step, int length) {
        int left, right, stopExcluded;
        this.step = step == null ? 1 : step;
        if (this.step > 0) {
            left = start == null ? 0 : start;
            right = stop == null ? length : stop;
            this.start = limit(left < 0 ? left + length : left, 0, length);
            stopExcluded = limit(right < 0 ? right + length : right, 0, length);
        } else if (this.step < 0) {
            left = start == null ? length - 1 : (start < 0 ? start + length : start);
            right = stop == null ? -1 : (stop < 0 ? stop + length : stop);
            this.start = limit(left, -1, length - 1);
            stopExcluded = limit(right, -1, length - 1);
        } else {
            throw new IllegalArgumentException("Argument step cannot be zero");
        }
        stopIncluded = this.step > 0 ? stopExcluded - 1 : stopExcluded + 1;
        if (
                (this.step > 0 && (this.start >= stopExcluded || this.start >= length)) ||
                        (this.step < 0 && (this.start <= stopExcluded || this.start < 0))
        ) size = 0;
        else size = Math.abs((stopIncluded - this.start) / this.step) + 1;
    }

    /**
     * 对切片后得到下标的进行遍历
     *
     * @param consumer 回调函数，传入旧/新下标
     */
    protected void forEachIndex(IndexConsumer consumer) {
        int newIndex = 0;
        if (step > 0) {
            for (int i = start; i <= stopIncluded; i += step) {
                consumer.accept(i, newIndex++);
            }
        } else {
            for (int i = start; i >= stopIncluded; i += step) {
                consumer.accept(i, newIndex++);
            }
        }
    }

    protected static int limit(int value, int min, int max) {
        return value < min ? min : (Math.min(value, max));
    }

    private interface IndexConsumer {
        void accept(int oldIndex, int newIndex);
    }

    @Override
    public String toString() {
        return "Slice{start=" + start + ", stopIncluded=" + stopIncluded + ", step=" + step + ", size=" + size + '}';
    }
}
