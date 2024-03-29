package sugar.tool;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数相关工具类
 * <p>
 * 此类中的所有方法的“随机”操作都基于伪随机数实现，请参考: {@link Random} / {@link ThreadLocalRandom}
 *
 * @author SnowLT
 * @version 1.0
 */
public class RandUtils {

    /**
     * 从列表中随机选取一个元素并返回
     *
     * @param <T>  元素类型
     * @param list 列表
     * @return 选取的新元素
     * @throws NullPointerException     如果 list 为 null
     * @throws IllegalArgumentException 如果 list 长度为 0
     */
    public static <T> T choice(List<T> list) {
        return list.get(nextInt(list.size()));
    }

    /**
     * 从数组中随机选取一个元素并返回
     *
     * @param <T>   元素类型
     * @param array 数组
     * @return 选取的新元素
     * @throws NullPointerException     如果 array 为 null
     * @throws IllegalArgumentException 如果 array 长度为 0
     */
    public static <T> T choice(T[] array) {
        return array[nextInt(array.length)];
    }

    /**
     * 从列表中随机选取 k 个可重复的元素，并将选取的元素组装为新列表返回
     * <p>这个方法的返回结果与抽样结果有区别，如需随机抽样请使用: {@link #sample(List, int)}。
     *
     * @param <T>  元素类型
     * @param list 列表
     * @param k    选取数量
     * @return 选取的多个新元素
     * @throws NullPointerException     如果 list 为 null
     * @throws IllegalArgumentException 如果 list 长度为 0 或 k < 0
     * @see #sample(List, int)
     */
    public static <T> List<T> choices(List<T> list, int k) {
        int n = list.size();
        ArrayList<T> nl = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            nl.add(list.get(nextInt(n)));
        }
        return nl;
    }

    /**
     * 从列表中无重复的随机抽样 k 个元素，将结果组成一个新列表并返回
     * <p>结果列表按选择顺序排列，因此返回的新列表也将是有效的随机样本。
     *
     * @param <T>  元素类型
     * @param list 列表
     * @param k    选取数量
     * @return 按顺序随机抽样出的 k 个元素
     * @throws NullPointerException     如果 list 为 null
     * @throws IllegalArgumentException 如果 list 长度为 0 ，k > list 长度或 k < 0
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> sample(List<T> list, int k) {
        if (k < 0 || k > list.size()) throw new IllegalArgumentException();
        else if (k == 1) return Collections.singletonList(choice(list));
        // 算法从 Python 库中的 random.sample 移植 https://github.com/python/cpython/blob/3.12/Lib/random.py#L359
        // 备注：思路类似 shuffle 方法中打乱顺序，交换位置的同时进行选择，只需重复 k 次即可
        ArrayList<T> nl = new ArrayList<>(k);
        Object[] pool = list.toArray();
        int n = pool.length;
        for (int i = 0; i < k; i++) {
            // 0 到 n-i 之间是还未选取的
            int j = nextInt(n - i);
            nl.add((T) pool[j]);
            // 将还未选取的元素移到前面的空位
            pool[j] = pool[n - 1 - i];
        }
        return nl;
    }

    /**
     * 将 List 中的元素打乱顺序，组成新的 List 返回，原对象不会被修改
     *
     * @param <T>  元素类型
     * @param list 原始未打乱的 List
     * @return 打乱顺序的新 List
     * @throws UnsupportedOperationException 如果参数不支持 {@link List#set(int, Object)} 方法则抛出
     * @throws NullPointerException          list 为 null
     */
    public static <T> List<T> shuffle(List<T> list) {
        ArrayList<T> nl = new ArrayList<>(list);
        shuffleSelf(nl);
        return nl;
    }

    /**
     * 将数组中的元素打乱顺序，组成新的数组返回，原对象不会被修改
     *
     * @param <T>   元素类型
     * @param array 原始未打乱的数组
     * @return 打乱顺序的新数组
     * @throws NullPointerException 数组为 null
     */
    public static <T> T[] shuffle(T[] array) {
        T[] na = Arrays.copyOf(array, array.length);
        shuffleSelf(na);
        return na;
    }

    /**
     * 将给定 List 中的元素顺序随机打乱
     *
     * @param <T>  元素类型
     * @param list 要打乱的 List
     * @throws UnsupportedOperationException 如果参数不支持 {@link List#set(int, Object)} 方法则抛出
     * @throws NullPointerException          list 为 null
     * @see java.util.Collections#shuffle(List)
     */
    public static <T> void shuffleSelf(List<T> list) {
        for (int i = list.size(); i > 1; i--) {
            // 从 0 到 i-1 中随机选择一个与 i 交换位置
            int j = nextInt(i);
            list.set(j, list.set(i - 1, list.get(j)));
        }
    }

    /**
     * 将给定数组中的元素顺序随机打乱
     *
     * @param <T>   元素类型
     * @param array 要打乱的数组
     * @throws NullPointerException array 为 null
     */
    public static <T> void shuffleSelf(T[] array) {
        for (int i = array.length; i > 1; i--) {
            // 从 0 到 i-1 中随机选择一个与 i 交换位置
            int j = nextInt(i);
            T temp = array[j];
            array[j] = array[i - 1];
            array[i - 1] = temp;
        }
    }

    /**
     * 返回一个 int 随机数 x， 0 <= x < max
     *
     * @param max 最大数（不包含）
     * @return 随机数
     * @throws IllegalArgumentException 如果 max <= 0
     * @see #randIntBetween(int, int)
     */
    public static int nextInt(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    /**
     * 返回一个 int 随机数 x， min <= x < max
     *
     * @param min 最小数（包含）
     * @param max 最大数（不包含）
     * @return 随机数
     * @throws IllegalArgumentException 如果 max <= min
     * @see #randIntBetween(int, int)
     */
    public static int nextInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * 返回一个 long 随机数 x， 0 <= x < max
     *
     * @param max 最大数（不包含）
     * @return 随机数
     * @throws IllegalArgumentException 如果 max <= 0
     */
    public static long nextLong(long max) {
        return ThreadLocalRandom.current().nextLong(max);
    }

    /**
     * 返回一个 long 随机数 x， min <= x < max
     *
     * @param min 最小数（包含）
     * @param max 最大数（不包含）
     * @return 随机数
     * @throws IllegalArgumentException 如果 max <= min
     */
    public static long nextLong(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    /**
     * 返回一个 float 随机数 x， 0.0F <= x < max
     *
     * @param max 最大数（不包含）
     * @return 随机数
     * @throws IllegalArgumentException 如果 max <= 0.0
     */
    public static float nextFloat(float max) {
        return nextFloat(0, max);
    }

    /**
     * 返回一个 float 随机数 x， min <= x < max
     *
     * @param min 最小数（包含）
     * @param max 最大数（不包含）
     * @return 随机数
     * @throws IllegalArgumentException 如果 max <= min
     */
    public static float nextFloat(float min, float max) {
        if (min > max) throw new IllegalArgumentException();
        return min + ((max - min) * ThreadLocalRandom.current().nextFloat());
    }

    /**
     * 返回一个 double 随机数 x， 0.0 <= x < 1.0
     *
     * @return 随机数
     */
    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    /**
     * 返回一个 double 随机数 x， 0.0 <= x < max
     *
     * @param max 最大数（不包含）
     * @return 随机数
     * @throws IllegalArgumentException 如果 max <= 0.0
     */
    public static double nextDouble(double max) {
        return ThreadLocalRandom.current().nextDouble(max);
    }

    /**
     * 返回一个 double 随机数 x， min <= x < max
     *
     * @param min 最小数（包含）
     * @param max 最大数（不包含）
     * @return 随机数
     * @throws IllegalArgumentException 如果 max <= min
     */
    public static double nextDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
     * 返回一个随机 boolean 值
     *
     * @return true 或 false
     */
    public static boolean nextBool() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * 返回一个 int 随机数 x， minInclusive <= x <= max
     *
     * @param minInclusive 最小数（包含）
     * @param maxInclusive 最大数（包含）
     * @return 随机数
     * @throws IllegalArgumentException 如果 maxInclusive < minInclusive
     */
    public static int randIntBetween(int minInclusive, int maxInclusive) {
        return (int) ThreadLocalRandom.current().nextLong(minInclusive, maxInclusive + 1L);
    }

    /**
     * 生成由 n 个随机字节组成的数组
     * <p>如果需要用于生成安全凭据、密码等请参考: {@link java.security.SecureRandom}，避免使用此方法。
     *
     * @param n 生成的字节数量
     * @return 随机生成的字节数组
     * @throws IllegalArgumentException 如果 n < 0
     */
    public static byte[] randBytes(int n) {
        if (n < 0) throw new IllegalArgumentException();
        byte[] result = new byte[n];
        ThreadLocalRandom.current().nextBytes(result);
        return result;
    }

    protected RandUtils() {
    }
}