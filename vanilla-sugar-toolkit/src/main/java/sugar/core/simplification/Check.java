package sugar.core.simplification;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.IntFunction;

/**
 * 整合了一些用于判断和检测方法
 * <p>
 * 此类中的方法主要是为了简化例如 if 等条件判断语句，支持类型自适应。
 * 没有特殊注明外，此类中的所有方法都支持空值检测（null安全）
 * </p>
 * <p><i>这个类中提供了一些类似其他语言的写法或逻辑</i></p>
 *
 * @author SnowLT
 * @version 1.8
 */
public class Check {


    /**
     * 检查字符序列是否不为 null 且长度不为空且包含非{@linkplain Character#isWhitespace(char) 空白字符}
     *
     * <pre>
     * e.g.
     *     notBlank(null) = false
     *     notBlank("") = false
     *     notBlank(" \t") = false
     *     notBlank("abc") = true
     *     notBlank(" abc ") = true
     * </pre>
     *
     * @param cs 字符序列
     * @return 如果字符序列为 null 或长度为空或只包含空白字符返回 false，否则返回 true
     * @see Character#isWhitespace(int)
     */
    public static boolean notBlank(CharSequence cs) {
        if (cs == null) return false;
        int len = cs.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) return true;
        }
        return false;
    }

    /**
     * 检查字符序列是否为 null、长度为空或只包含{@linkplain Character#isWhitespace(char) 空白字符}
     *
     * <pre>
     * e.g.
     *     isBlank(null) = true
     *     isBlank("") = true
     *     isBlank(" \t") = true
     *     isBlank("abc") = false
     *     isBlank(" abc ") = false
     * </pre>
     *
     * @param cs 字符序列
     * @return 如果字符序列为 null、长度为空或只包含空白字符返回 true，否则返回 false
     * @see Character#isWhitespace(int)
     * @see #notBlank(CharSequence)
     */
    public static boolean isBlank(CharSequence cs) {
        return !notBlank(cs);
    }

    /**
     * 判断字符序列是否不为空
     *
     * @param s 字符序列
     * @return 判断结果
     */
    public static boolean notEmpty(CharSequence s) {
        return s != null && s.length() > 0;
    }

    /**
     * 判断集合是否不为空
     *
     * @param c 集合
     * @return 判断结果
     */
    public static boolean notEmpty(Collection<?> c) {
        return c != null && !c.isEmpty();
    }

    /**
     * 判断 Map 是否不为空
     *
     * @param map 集合
     * @return 判断结果
     */
    public static boolean notEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 判断数组长度是否不为 0
     *
     * @param array 数组
     * @return 判断结果
     */
    public static <T> boolean notEmpty(T[] array) {
        return array != null && array.length > 0;
    }

    /**
     * 判断可迭代对象是否还没到末尾
     *
     * @param iterable 可迭代对象
     * @return 判断结果
     */
    public static boolean notEmpty(Iterable<?> iterable) {
        return iterable != null && iterable.iterator().hasNext();
    }

    /**
     * 判断迭代器是否还没到末尾
     *
     * @param iterator 迭代器
     * @return 判断结果
     */
    public static boolean notEmpty(Iterator<?> iterator) {
        return iterator != null && iterator.hasNext();
    }

    /**
     * 判断 Optional 是否包含值
     *
     * @param optional Optional
     * @return 判断结果
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static boolean notEmpty(Optional<?> optional) {
        return optional != null && optional.isPresent();
    }

    /**
     * 判断字符序列是否为空
     *
     * @param s 字符串
     * @return 判断结果
     */
    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 判断集合是否为空
     *
     * @param c 集合
     * @return 判断结果
     */
    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    /**
     * 判断 Map 是否为空
     *
     * @param map 集合
     * @return 判断结果
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断数组长度是否为 0
     *
     * @param array 数组
     * @return 判断结果
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断可迭代对象是否达到了末尾
     *
     * @param iterable 可迭代对象
     * @return 判断结果
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return iterable == null || !iterable.iterator().hasNext();
    }

    /**
     * 判断迭代器是否达到了末尾
     *
     * @param iterator 迭代器
     * @return 判断结果
     */
    public static boolean isEmpty(Iterator<?> iterator) {
        return iterator == null || !iterator.hasNext();
    }

    /**
     * 判断 Optional 是否不含值(Optional.empty())
     *
     * @param optional Optional
     * @return 判断结果
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static boolean isEmpty(Optional<?> optional) {
        return optional == null || !optional.isPresent();
    }

    /**
     * 判断布尔值是否等于 true
     *
     * @param b 布尔值
     * @return 判断结果
     */
    public static boolean isTrue(Boolean b) {
        return b != null && b;
    }

    /**
     * 判断字符不为结束符（ASCII 码 0）
     * <p><i>参考 C 语言的字符数组结尾</i></p>
     *
     * @param c 字符
     * @return 不为 ASCII 码 0 时返回 true，否则返回 false
     */
    public static boolean isTrue(Character c) {
        return c != null && c != 0;
    }

    /**
     * 判断数字是否不等于 0
     * <p>备注：如果是用于比较 float / double 等浮点数计算结果，建议使用 {@link BigDecimal} 相关方法替代以避免精度问题</p>
     *
     * @param n 数字
     * @return 判断结果
     */
    public static boolean isTrue(Number n) {
        if (n == null) return false;
        if (n instanceof BigDecimal) return BigDecimal.ZERO.compareTo((BigDecimal) n) != 0;
        if (n instanceof BigInteger) return BigInteger.ZERO.compareTo((BigInteger) n) != 0;
        return n.doubleValue() != 0.0;
    }

    /**
     * 判断传入的值是否等效于 true
     * <p>会自动根据传入类型和值进行判断，返回结果有以下情况：
     * <ol>
     *     <li>{@code null} 值: 总是返回 false</li>
     *     <li>{@link Boolean} 布尔值: 总是返回自身</li>
     *     <li>{@link CharSequence} / {@link Collection} / {@link Map} / 数组:
     *     长度不为空时返回 true，否则返回 false</li>
     *     <li>{@link Number} 数字: 不等于 0 返回 true，等于 0 返回 false</li>
     *     <li>{@link Iterable} 可迭代对象: {@link Iterable#iterator()}
     *     的迭代器没有到达末尾时返回 true，否则返回 false</li>
     *     <li>{@link Iterator} 迭代器: 没有到达末尾时返回 true，否则返回 false(同 {@link Iterator#hasNext()})</li>
     *     <li>{@link Character} 字符: ASCII 码不等于 0 返回 true，否则返回 false(见 {@link #isTrue(Character)})</li>
     *     <li>{@link Optional}: 容器不为空返回 true，否则返回 false(同 {@link Optional#isPresent()})</li>
     *     <li>其他情况: 总是返回 true</li>
     * </ol></p>
     *
     * @param o 被检测对象
     * @return 值等效于 true 且不为 null 时返回 true
     */
    public static boolean isTrue(Object o) {
        if (o == null) return false;
        Class<?> cls = o.getClass();
        if (o instanceof Boolean) return (boolean) o;
        if (o instanceof CharSequence) return notEmpty((CharSequence) o);
        if (o instanceof Collection) return notEmpty((Collection<?>) o);
        if (o instanceof Map) return notEmpty((Map<?, ?>) o);
        if (cls.isArray()) return Array.getLength(o) > 0;
        if (o instanceof Number) return isTrue((Number) o);
        if (o instanceof Iterable) return notEmpty((Iterable<?>) o);
        if (o instanceof Iterator) return notEmpty((Iterator<?>) o);
        if (o instanceof Character) return isTrue((Character) o);
        if (o instanceof Optional<?>) return notEmpty((Optional<?>) o);
        return true;
    }

    /**
     * 对左右两侧的值调用 {@code isTrue()} 后进行异或运算
     * <ul>
     *      <li>如果左、右值的 isTrue() 结果不相同，则返回 true</li>
     *      <li>如果左、右值的 isTrue() 结果相同，则返回 false</li>
     * </ul>
     *
     * @param left  左值
     * @param right 右值
     * @return 异或运算结果
     * @see #isTrue(Object)
     * @see Boolean#logicalXor(boolean, boolean)
     */
    public static boolean xor(Object left, Object right) {
        return isTrue(left) ^ isTrue(right);
    }

    /**
     * 判断所有值是否都等效于 true
     * <pre>
     *     allTrue(值1, 值2, ...)
     *     等同于： isTrue(值1) && isTrue(值2) && ...
     * </pre>
     *
     * @param targets 要判断的值
     * @return 所有值都等效于 true 时返回 true
     * @see #isTrue(Object)
     */
    public static boolean allTrue(Object... targets) {
        if (targets == null || targets.length == 0) return false;
        for (Object condition : targets) {
            if (!isTrue(condition)) return false;
        }
        return true;
    }

    /**
     * 判断是否有任意一个值等效于 true
     * <pre>
     *     anyTrue(值1, 值2, ...)
     *     等同于： isTrue(值1) || isTrue(值2) || ...
     * </pre>
     *
     * @param targets 要判断的值
     * @return 有任意一个值等效于 true 时返回 true
     * @see #isTrue(Object)
     */
    public static boolean anyTrue(Object... targets) {
        if (targets == null || targets.length == 0) return false;
        for (Object condition : targets) {
            if (isTrue(condition)) return true;
        }
        return false;
    }

    /**
     * 判断所有值是否都不等效于 true
     * <pre>
     *     noneTrue(值1, 值2, ...)
     *     等同于： !isTrue(值1) || !isTrue(值2) || ...
     * </pre>
     *
     * @param targets 要判断的值
     * @return 所有值都不等效于 true 时返回 true
     * @see #isTrue(Object)
     */
    public static boolean noneTrue(Object... targets) {
        return !anyTrue(targets);
    }

    /**
     * 判断所有对象是否都为 null
     *
     * @param targets 判断的对象
     * @return 所有对象都为 null 时返回 true
     */
    public static boolean allNull(Object... targets) {
        if (targets == null || targets.length == 0) return false;
        for (Object condition : targets) {
            if (condition != null) return false;
        }
        return true;
    }

    /**
     * 判断是否有任意一个对象为 null
     *
     * @param targets 判断的对象
     * @return 有任意对象为 null 时返回 true
     */
    public static boolean anyNull(Object... targets) {
        if (targets == null || targets.length == 0) return false;
        for (Object condition : targets) {
            if (condition == null) return true;
        }
        return false;
    }

    /**
     * 判断所有对象是否都不为 null
     *
     * @param targets 判断的对象
     * @return 所有对象都不为 null 时返回 true
     */
    public static boolean noneNull(Object... targets) {
        return !anyNull(targets);
    }

    /**
     * 如果参数彼此相等返回 true，否则 false，和 {@link Objects#equals(Object, Object)} 相同。
     * 如果两个参数都是 null 则返回 true，否则调用 {@code a.equals(b)} 来确定相等性。
     *
     * @param a 对象
     * @param b 与 a 进行比较的对象
     * @return 如果参数彼此相等返回 true，否则 false
     * @see Objects#equals(Object, Object)
     */
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    /**
     * 判断左右两个对象的数值是否相同，忽略具体的类型。
     * 可避免类型转换时的精度问题（基于 BigDecimal 判断）
     * <pre>
     * e.g.
     *     equals(23, 23) -> true
     *     equals(23, 23.0D) -> true
     *     equals(12.34D, 12.34F) -> true
     *     equals(2L, new BigDecimal("2.0")) -> true
     *     equals(new BigDecimal("1.00"), new BigDecimal("1")) -> true
     *     equals(23.0, (Number) null) -> false
     *     12.34D == 12.34F -> false（Java 原生写法，非此方法）
     * </pre>
     *
     * @param left  左
     * @param right 右
     * @return 两者内容相同时返回 true
     */
    public static boolean equalsAsNumber(Number left, Number right) {
        // 空值检测
        if (left == right) return true;
        if (left == null || right == null) return false;
        // 整形直接比较
        if (isIntegerType(left) && isIntegerType(right)) {
            return left.longValue() == right.longValue();
        }
        // 浮点数使用 BigDecimal 转换后再比较，防止精度损失
        BigDecimal d1 = left instanceof BigDecimal ?
                ((BigDecimal) left) : new BigDecimal(left.toString());
        BigDecimal d2 = right instanceof BigDecimal ?
                ((BigDecimal) right) : new BigDecimal(right.toString());
        return d1.compareTo(d2) == 0;
    }

    /**
     * 判断左右两个字符序列的文本内容是否相同（区分大小写）
     * <pre>
     * e.g.
     *     equals("1", "1") -> true
     *     equals(new StringBuilder("AAA"), "AAA") -> true
     *     equals("1.0", "1") -> false
     *     equals("Abc", "abc") -> false
     * </pre>
     *
     * @param left  左
     * @param right 右
     * @return 两者内容相同时返回 true
     */
    public static boolean contentEquals(CharSequence left, CharSequence right) {
        // 自身检测和空值检测
        if (left == right) return true;
        if (left == null || right == null) return false;
        // 如果存在 String，使用 String 的 contentEquals 方法
        if (left instanceof String && right instanceof String) return left.equals(right);
        return left.toString().contentEquals(right);
    }

    /**
     * 判断左侧字符序列与右侧字符数组的文本内容是否相同（区分大小写）
     * <pre>
     * e.g.
     *     equals("1A", new char[] {'1', 'A'}) -> true
     *     equals(null, null) -> true
     *     equals("sth", null) -> false
     * </pre>
     *
     * @param left  左（字符序列）
     * @param right 右（字符数组）
     * @return 两者内容相同时返回 true
     */
    public static boolean equalsAsString(CharSequence left, char[] right) {
        // 空值检测
        if (left == null && right == null) {
            return true;
        } else if (left == null || right == null) {
            return false;
        }
        if (left.length() != right.length) {
            return false;
        }
        return new String(right).contentEquals(left);
    }

    /**
     * 判断左侧字符序列与右侧字符数组的文本内容是否相同（重载方法，区分大小写）
     * <pre>
     * e.g.
     *     equals(new char[] {'1', 'A'}, "1A") -> true
     *     equals(null, null) -> true
     *     equals(null, "sth") -> false
     * </pre>
     *
     * @param left  左（字符数组）
     * @param right 右（字符序列）
     * @return 两者内容相同时返回 true
     * @see #equalsAsString(CharSequence, char[])
     */
    public static boolean equalsAsString(char[] left, CharSequence right) {
        return equalsAsString(right, left);
    }

    /**
     * 判断左侧的字符和右侧对象的内容是否相同
     * 支持和以下类型进行比较：
     * 字符、字符序列（CharSequence）、数字（Number）
     * <pre>
     * 右侧对象的类型有如下情况：
     * 与 Character/char 比较为常规的判断
     *      equalsAsChar('A', 'A') -> true
     *      equalsAsChar('A', 'B') -> false
     * 与 CharSequence 比较，会在右侧长度为1时提取首个字符进行比较
     *      equalsAsChar('A', "A") -> true
     *      equalsAsChar('A', "B") -> false
     *      equalsAsChar('A', "ABC") -> false
     * 与 Number 比较，会使用 left 的 ASCII 码与右侧比较
     *      equalsAsChar('A', 65) -> true
     *      equalsAsChar('A', 55) -> false
     * 其他情况返回 false
     *      equalsAsChar('A', new Object()) -> false
     * </pre>
     *
     * @param left  左（字符）
     * @param right 右（任意对象）
     * @return 两者内容相同时返回 true
     */
    public static boolean equalsAsChar(Character left, Object right) {
        // 空值检测
        if (left == right) return true;
        if (left == null || right == null) return false;
        char ch = left;
        // 字符
        if (right instanceof Character) return left == ((Character) right).charValue();
        // 检测另一个对象的类型，并分类处理，字符序列
        if (right instanceof CharSequence) {
            CharSequence cs = (CharSequence) right;
            return cs.length() == 1 && ch == cs.charAt(0);
        }
        // 数字
        if (right instanceof Number) {
            return isIntegerType(right) ? ch == ((Number) right).longValue() :
                    equalsAsNumber((int) ch, (Number) right);
        }
        return false;
    }

    /**
     * 判断左侧的数字和右侧字符串的内容是否相同
     * 会尝试将字符串转为数字进行比较，如果失败则返回 false
     * <pre>
     * 例如有如下情况：
     *      equalsAsNumber(233, "233") -> true
     *      equalsAsNumber(233.0, "233") -> true
     *      equalsAsNumber(1.0, "1.00") -> true
     *      equalsAsNumber(2, "AAA") -> false
     * </pre>
     *
     * @param left  左（数字）
     * @param right 右（字符序列）
     * @return 两者内容相同时返回 true
     */
    public static boolean equalsAsNumber(Number left, CharSequence right) {
        // 空值检测
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        // 尝试将字符串转为数字
        try {
            BigDecimal decimal = new BigDecimal(right.toString());
            return equalsAsNumber(decimal, left);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断左侧的字符串和右侧数字的内容是否相同（重载方法）
     * 会尝试将字符串转为数字进行比较，如果失败则将数字转为字符串对比文本
     * <pre>
     * 例如有如下情况：
     *      equalsAsNumber("233", 233) -> true
     *      equalsAsNumber("233", 233.0) -> true
     *      equalsAsNumber("1.00", 1.0) -> true
     *      equalsAsNumber("AAA", 2) -> false
     * </pre>
     *
     * @return 两者内容相同时返回 true
     * @see #equalsAsNumber(Number, CharSequence)
     */
    public static boolean equalsAsNumber(CharSequence left, Number right) {
        return equalsAsNumber(right, left);
    }


    /**
     * 判断两个对象的内容是否相同，会根据类型自动按顺序匹配并调用以下方法：
     * <ol>
     *     <li>{@link #equalsAsNumber(Number, Number)}</li>
     *     <li>{@link #contentEquals(CharSequence, CharSequence)}</li>
     *     <li>{@link #equalsAsNumber(Number, CharSequence)} 和 {@link #equalsAsNumber(CharSequence, Number)}</li>
     *     <li>{@link #equalsAsString(CharSequence, char[])} 和 {@link #equalsAsString(char[], CharSequence)}</li>
     *     <li>{@link #equalsAsIterable(Object, Object)}</li>
     *     <li>{@link #equalsAsChar(Character, Object)}</li>
     * </ol>
     * 如果匹配不到以上方法，则会调用 {@link Object#equals(Object)} 作为结果
     * <p>
     * <i>如果需要用自定义的方式比较两个集合中的内容是否相同，可使用 {@link #contentEquals(Collection, Collection, BiPredicate)}</i>
     * </p>
     *
     * @param left  任意对象
     * @param right 任意对象
     * @return 结果
     */
    public static boolean contentEquals(Object left, Object right) {
        if (left == right) return true;
        if (left == null || right == null) return false;
        // 都是数字（此处已经被自动装箱，不用处理基本数据类型）
        if (left instanceof Number && right instanceof Number) return equalsAsNumber((Number) left, (Number) right);
        // 字符串和其他比较
        if (left instanceof CharSequence) {
            if (right instanceof CharSequence) return contentEquals((CharSequence) left, (CharSequence) right);
            if (right instanceof Number) return equalsAsNumber((Number) right, (CharSequence) left);
            if (right instanceof char[]) return equalsAsString((CharSequence) left, (char[]) right);
        } else if (right instanceof CharSequence) {
            if (left instanceof Number) return equalsAsNumber((Number) left, (CharSequence) right);
            if (left instanceof char[]) return equalsAsString((CharSequence) right, (char[]) left);
        }
        Class<?> c1 = left.getClass();
        Class<?> c2 = right.getClass();
        if ((left instanceof Iterable || c1.isArray()) && (right instanceof Iterable || c2.isArray()))
            return equalsAsIterable(left, right, Objects::equals);
        // 左右存在字符类型
        if (c1 == Character.class) {
            return equalsAsChar((Character) left, right);
        } else if (c2 == Character.class) {
            return equalsAsChar((Character) right, left);
        }
        // 其他情况，直接使用对象的 equals 方法
        return left.equals(right);
    }

    /**
     * 将左右侧的 Collection 当作 Set 判断内容是否相同（元素使用 {@link Objects#equals(Object, Object)} 进行比较）
     * <p>如果传入的对象不是 Set，会自动转化为 Set 后再进行比较</p>
     * <pre>
     * 例如有如下情况：
     *      contentEqualsAsSet({"A", "B"}, {"B", "A"}) -> true
     *      contentEqualsAsSet({"A"}, {"A", "A"}) -> true
     *      contentEqualsAsSet({"A", "B"}, {"A", "B", "C"}) -> false
     *      contentEqualsAsSet({"A", "B", "C"}, {"A", "B", "B"}) -> false
     * </pre>
     *
     * @return 两者内容相同时返回 true
     */
    public static <T> boolean equalsAsSet(Collection<T> left, Collection<T> right) {
        if (left == right) return true;
        if (left == null || right == null) return false;
        Set<T> set1 = left instanceof Set ? ((Set<T>) left) : new HashSet<>(left);
        Set<T> set2 = right instanceof Set ? ((Set<T>) right) : new HashSet<>(right);
        return set1.equals(set2);
    }

    /**
     * 判断左侧和右侧可迭代对象中，对应位置的元素内容是否相同，且两者长度相等（元素使用
     * {@link Objects#equals(Object, Object)} 进行比较）
     * <p>
     * 左侧和右侧支持传入：
     * <ul>
     *     <li>{@link Collection}</li>
     *     <li>{@link Iterable}</li>
     *     <li>{@code Object[]}, {@code int[]} 等对象数组和基本类型数组</li>
     * </ul></p>
     *
     * <pre>
     * 例如有如下情况：
     *      contentEqualsAsCollection(new int[]{1, 2, 3}, Arrays.asList(1, 2, 3)) -> true
     *      contentEqualsAsCollection(new int[]{12, 34, 12, 34}, Arrays.asList("12", "34", "12", "34")) -> false
     *      contentEqualsAsCollection(new String[]{"A"}, Arrays.asList("A", "A")) -> false
     * </pre>
     *
     * @param left  集合
     * @param right 集合
     * @return 左右的长度相等，且对应位置元素内容相同时返回 true
     * @throws IllegalArgumentException 当对象不是 Collection / Iterable / 数组 时抛出
     */
    public static boolean equalsAsIterable(Object left, Object right) {
        return equalsAsIterable(left, right, Objects::equals);
    }

    /**
     * 判断左侧和右侧可迭代对象中，对应位置的元素内容是否相同，且两者长度相等
     *
     * <p>
     * 左侧和右侧支持传入：
     * <ul>
     *     <li>{@link Collection}</li>
     *     <li>{@link Iterable}</li>
     *     <li>{@code Object[]} 对象数组, {@code int[]} 等基本类型数组</li>
     * </ul>
     * 判断元素是否相同时使用 {@code equalsPredicate} 参数指定的 {@link java.util.function.BiPredicate} 进行判断
     * </p>
     *
     * @param left  集合
     * @param right 集合
     * @return 左右的长度相等，且对应位置元素内容相同时返回 true
     * @throws IllegalArgumentException 当对象不是 Collection / Iterable / 数组，或 equalsPredicate 为 null 时抛出
     */
    @SuppressWarnings("unchecked")
    public static boolean equalsAsIterable(Object left, Object right,
                                           BiPredicate<Object, Object> equalsPredicate) {
        if (equalsPredicate == null) throw new IllegalArgumentException("equalsPredicate cannot be null");
        if (left == right) return true;
        if (left == null || right == null) return false;
        Iterable<?> l = tryWrappingInCollection(left);
        Iterable<?> r = tryWrappingInCollection(right);
        return (l instanceof Collection && r instanceof Collection) ?
                contentEquals((Collection<Object>) l, (Collection<Object>) r, equalsPredicate) :
                contentEqualsByIteration((Iterable<Object>) l, (Iterable<Object>) r, equalsPredicate);
    }

    /**
     * 判断左侧和右侧 {@link Collection} 长度和对应位置的元素(通过 {@link Collection#iterator()} 获取)内容是否相同（元素使用
     * {@link Objects#equals(Object, Object)} 进行比较）
     *
     * <pre>
     * 例如有如下情况：
     *      collectionEquals({"A", "B", "A", "B"}, {"A", "B", "A", "B"}) -> true
     *      collectionEquals({12, 34, 12, 34}, {"12", "34", "12", "34"}) -> false
     *      collectionEquals({"A"}, {"A", "A"}) -> false
     *      collectionEquals({"A", "B", "C"}, {"C", "B", "A"}) -> false
     * </pre>
     *
     * @param left  集合
     * @param right 集合
     * @return 左右 size() 相等，且对应位置元素内容相同时返回 true
     */
    public static boolean contentEquals(Collection<?> left, Collection<?> right) {
        return contentEquals(left, right, Objects::equals);
    }

    /**
     * 判断左侧和右侧 {@link Collection} 长度和对应位置的元素(通过 {@link Collection#iterator()} 获取)内容是否相同
     * <p>在判断元素是否相同时使用 {@code equalsPredicate} 参数指定的 {@link java.util.function.BiPredicate} 进行判断</p>
     *
     * @param left            集合
     * @param right           集合
     * @param equalsPredicate 用于判断两个对象是否相等，相同则返回 true，不同返回 false
     * @return 左右 size() 相等，且对应位置元素内容相同时返回 true
     * @throws IllegalArgumentException 当对象不是 Collection / Iterable / 数组，或 equalsPredicate 为 null 时抛出
     */
    public static <T1, T2> boolean contentEquals(Collection<T1> left, Collection<T2> right,
                                                 BiPredicate<T1, T2> equalsPredicate) {
        if (equalsPredicate == null) throw new IllegalArgumentException("equalsPredicate cannot be null");
        if (left == right) return true;
        if (left == null || right == null || left.size() != right.size()) return false;
        return contentEqualsByIteration(left, right, equalsPredicate);
    }

    private static <T1, T2> boolean contentEqualsByIteration(Iterable<T1> left, Iterable<T2> right,
                                                             BiPredicate<T1, T2> predicate) {
        Iterator<T1> iterator1 = left.iterator();
        Iterator<T2> iterator2 = right.iterator();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            if (!predicate.test(iterator1.next(), iterator2.next())) return false;
        }
        return !iterator1.hasNext() && !iterator2.hasNext();
    }

    private static Iterable<?> tryWrappingInCollection(Object o) {
        if (o.getClass().isArray()) return ArrayAdaptor.build(o);
        if (o instanceof Iterable) return (Iterable<?>) o;
        throw new IllegalArgumentException("Unsupported type: " + o.getClass());
    }

    /**
     * 判断数字是否是整数的类型（不包括原始类型）
     *
     * @param o 判断的对象
     * @return 结果
     */
    private static boolean isIntegerType(Object o) {
        return o instanceof Integer || o instanceof Long ||
                o instanceof Byte || o instanceof Short;
    }

    protected Check() {
    }

    static class ArrayAdaptor extends AbstractCollection<Object> {
        private final int length;
        private final IntFunction<Object> getArrayContent;

        public ArrayAdaptor(int length, IntFunction<Object> getArrayContent) {
            this.length = length;
            this.getArrayContent = getArrayContent;
        }

        public static ArrayAdaptor build(Object array) {
            if (array instanceof byte[]) {
                byte[] bytes = (byte[]) array;
                return new ArrayAdaptor(bytes.length, value -> bytes[value]);
            } else if (array instanceof short[]) {
                short[] shorts = (short[]) array;
                return new ArrayAdaptor(shorts.length, value -> shorts[value]);
            } else if (array instanceof int[]) {
                int[] integers = (int[]) array;
                return new ArrayAdaptor(integers.length, value -> integers[value]);
            } else if (array instanceof long[]) {
                long[] longs = (long[]) array;
                return new ArrayAdaptor(longs.length, value -> longs[value]);
            } else if (array instanceof char[]) {
                char[] chars = (char[]) array;
                return new ArrayAdaptor(chars.length, value -> chars[value]);
            } else if (array instanceof float[]) {
                float[] floats = (float[]) array;
                return new ArrayAdaptor(floats.length, value -> floats[value]);
            } else if (array instanceof double[]) {
                double[] doubles = (double[]) array;
                return new ArrayAdaptor(doubles.length, value -> doubles[value]);
            } else if (array instanceof boolean[]) {
                boolean[] booleans = (boolean[]) array;
                return new ArrayAdaptor(booleans.length, value -> booleans[value]);
            } else {
                // 其他对象数组、多维数组都可以转为 Object[]
                Object[] objects = (Object[]) array;
                return new ArrayAdaptor(objects.length, value -> objects[value]);
            }
        }

        @Override
        public Iterator<Object> iterator() {
            return new Itr();
        }

        @Override
        public int size() {
            return length;
        }

        private class Itr implements Iterator<Object> {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < length;
            }

            @Override
            public Object next() {
                try {
                    return getArrayContent.apply(index++);
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
        }
    }

}