package sugar.tool;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 枚举转换和处理工具类
 *
 * @author SnowLT
 * @version 1.0
 */
public class EnumUtils {

    /**
     * 检查对象是否为枚举
     *
     * @param o 任意对象
     * @return 是枚举返回 true，为 null 或者不是枚举返回 false
     */
    public static boolean isEnum(Object o) {
        return o != null && o.getClass().isEnum();
    }

    /**
     * 将目标值转换为枚举，转换失败返回 null
     *
     * @param <E>    枚举类型
     * @param <V>    值的类型
     * @param items  所有待查找的枚举
     * @param value  要查找的值
     * @param getter 获取枚举的字段
     * @return 目标枚举
     * @throws IllegalArgumentException 如果 getter 参数为空
     */
    public static <E extends Enum<E>, V> E getEnum(E[] items, V value, Function<E, V> getter) {
        return getEnum(items, value, getter, null);
    }

    /**
     * 将目标值转换为枚举
     *
     * @param <E>         枚举类型
     * @param <V>         值的类型
     * @param items       所有待查找的枚举
     * @param value       要查找的值
     * @param getter      获取枚举的字段
     * @param defaultEnum 转换失败时，返回的默认枚举值
     * @return 目标枚举
     * @throws IllegalArgumentException 如果 getter 参数为空
     */
    public static <E extends Enum<E>, V> E getEnum(E[] items, V value, Function<E, V> getter, E defaultEnum) {
        if (getter == null) throw new IllegalArgumentException("getter cannot be null");
        if (items == null) return defaultEnum;
        for (E item : items) {
            if (Objects.equals(value, getter.apply(item))) {
                return item;
            }
        }
        return defaultEnum;
    }

    /**
     * 将目标值转换为枚举，转换失败返回 null
     * <p><i>推荐优先使用{@link #getEnum(Enum[], Object, Function)}</i></p>
     *
     * @param <E>       枚举类型
     * @param <V>       值的类型
     * @param enumClass 枚举的类
     * @param value     要查找的值
     * @param getter    获取枚举的字段
     * @return 目标枚举
     * @throws IllegalArgumentException class 或 getter 参数为空时抛出
     */
    public static <E extends Enum<E>, V> E getEnum(Class<E> enumClass, V value, Function<E, V> getter) {
        if (enumClass == null) throw new IllegalArgumentException("enumClass cannot be null");
        return getEnum(enumClass.getEnumConstants(), value, getter);
    }

    /**
     * 尝试将枚举序号转为枚举，如果转换失败返回 null
     *
     * @param <E>       枚举类型（泛型）
     * @param enumClass 枚举的Class
     * @param ordinal   枚举的序号
     * @return 转换后的结果
     * @throws IllegalArgumentException 如果 enumClass 为空
     */
    public static <E extends Enum<E>> E fromOrdinal(Class<E> enumClass, Integer ordinal) {
        return fromOrdinalImplicit(enumClass, ordinal, null);
    }

    /**
     * 尝试将枚举序号转为枚举
     *
     * @param <E>          枚举类型（泛型）
     * @param enumClass    枚举的Class
     * @param ordinal      枚举的序号
     * @param defaultValue 转换失败时，返回的默认值
     * @return 转换后的结果
     * @throws IllegalArgumentException 如果 enumClass 为空
     */
    public static <E extends Enum<E>> E fromOrdinal(Class<E> enumClass, Integer ordinal, E defaultValue) {
        return fromOrdinalImplicit(enumClass, ordinal, defaultValue);
    }

    /**
     * 尝试将枚举序号转为枚举
     * <p>此方法接受任意类型的 {@link Class}，适用在一些需要绕过编译器的类型检查的场景，推荐优先使用 #{@link #fromOrdinal(Class, Integer, E)}</p>
     *
     * @param <E>          枚举类型（泛型）
     * @param enumClass    枚举的Class
     * @param ordinal      枚举的序号
     * @param defaultValue 转换失败时，返回的默认值
     * @return 转换后的结果
     * @throws IllegalArgumentException 如果 enumClass 为空
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E fromOrdinalImplicit(Class<?> enumClass, Integer ordinal, E defaultValue) {
        if (enumClass == null) throw new IllegalArgumentException("enumClass cannot be null");
        if (!enumClass.isEnum() || ordinal == null) return defaultValue;
        try {
            Enum<?>[] values = (Enum<?>[]) enumClass.getEnumConstants();
            if (values == null || ordinal < 0 || ordinal >= values.length) return defaultValue;
            return (E) values[ordinal];
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 尝试根据枚举的名称({@link Enum#name()})转换为枚举，忽略大小写
     *
     * @param <E>          枚举类型（泛型）
     * @param enumClass    枚举的Class
     * @param name         枚举的名称
     * @param defaultValue 转换失败时，返回的默认值
     * @return 转换后的结果
     * @throws IllegalArgumentException 如果 enumClass 为空
     * @see Enum#valueOf(Class, String)
     */
    public static <E extends Enum<E>> E fromNameIgnoreCase(Class<E> enumClass, String name, E defaultValue) {
        if (enumClass == null) throw new IllegalArgumentException("enumClass cannot be null");
        if (name == null || name.isEmpty()) return defaultValue;
        try {
            for (E enumConstant : enumClass.getEnumConstants()) {
                if (enumConstant.name().equalsIgnoreCase(name)) return enumConstant;
            }
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 根据所有的枚举项生成一个 Map
     *
     * @param <E>            枚举类型
     * @param <K>            Map 中键的类型
     * @param <V>            Map 中值的类型
     * @param enumClass      枚举的类
     * @param keyExtractor   将枚举映射为 Map 中的键
     * @param valueExtractor 将枚举映射为 Map 中的值
     * @return Map
     * @throws IllegalArgumentException enumClass / keyExtractor / valueExtractor 为空时抛出
     */
    public static <E extends Enum<E>, K, V> Map<K, V> toMap(Class<E> enumClass, Function<E, K> keyExtractor, Function<E, V> valueExtractor) {
        if (enumClass == null || keyExtractor == null || valueExtractor == null)
            throw new IllegalArgumentException("enumClass / keyExtractor / valueExtractor cannot be null");
        E[] enumConstants = enumClass.getEnumConstants();
        HashMap<K, V> map = new HashMap<>(enumConstants.length);
        for (E enumConstant : enumConstants) {
            map.put(keyExtractor.apply(enumConstant), valueExtractor.apply(enumConstant));
        }
        return map;
    }

    /**
     * 根据指定的映射方式，将枚举转换为列表
     *
     * @param <E>       枚举类型
     * @param <T>       映射后新值的类型
     * @param enumClass 枚举的类
     * @param mapper    将枚举映射为 List 中的值
     * @return 列表
     * @throws IllegalArgumentException enumClass / valueExtractor 为空时抛出
     */
    public static <E extends Enum<E>, T> List<T> toList(Class<E> enumClass, Function<E, T> mapper) {
        if (enumClass == null || mapper == null) throw new IllegalArgumentException("parameter(s) cannot be null");
        return Arrays.stream(enumClass.getEnumConstants()).map(mapper).collect(Collectors.toList());
    }

    /**
     * 获取枚举的名称列表（通过 {@link Enum#name()}）
     *
     * @param <E>       枚举类型
     * @param enumClass 枚举的类
     * @return 枚举的名称列表
     * @throws IllegalArgumentException enumClass 为空时抛出
     */
    public static <E extends Enum<E>> List<String> getNames(Class<E> enumClass) {
        return toList(enumClass, Enum::name);
    }

    protected EnumUtils() {
    }
}