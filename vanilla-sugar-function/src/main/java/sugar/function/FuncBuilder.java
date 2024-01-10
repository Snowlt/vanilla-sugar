package sugar.function;

import java.util.concurrent.Callable;
import java.util.function.*;

/**
 * 构建或加工常用的 {@link Function}、{@link Supplier}、{@link Predicate} 等对象
 *
 * @author SnowLT
 */
public class FuncBuilder {
    private FuncBuilder() {
    }

    /* 常用函数 */

    /**
     * 返回两个对象中的前者
     *
     * @param <T> 泛型
     * @return 对象
     */
    public static <T> BinaryOperator<T> former() {
        return (f, l) -> f;
    }

    /**
     * 返回两个对象中的后者
     *
     * @param <T> 泛型
     * @return 对象
     */
    public static <T> BinaryOperator<T> latter() {
        return (f, l) -> l;
    }

    /**
     * 返回一个反转原始 {@link Predicate} 的结果
     * <pre> {@code not(old).test(t)} 等效于: {@code !old.test(t)}</pre>
     *
     * @param <T> 泛型
     * @param old 原始 Predicate
     * @return 反转后的 Predicate
     * @throws IllegalArgumentException 如果参数 old 为空
     */
    public static <T> Predicate<T> not(Predicate<T> old) {
        if (old == null) throw new IllegalArgumentException();
        return t -> !old.test(t);
    }

    /* 可处理异常的函数 */

    /**
     * 将 {@link Supplier} 包装为抛出异常 {@link Exception} 时返回 null 的 Supplier
     *
     * @param <T>                   泛型
     * @param supplierWithException 可能抛出异常的 Supplier
     * @return 包装后的 Supplier
     * @throws IllegalArgumentException 如果参数 supplierWithException 为空
     */
    public static <T> Supplier<T> nonEx(Supplier<T> supplierWithException) {
        return nonEx(supplierWithException, null);
    }

    /**
     * 将 {@link Supplier} 包装为抛出异常 {@link Exception} 时返回默认值的 Supplier
     *
     * @param <T>                   泛型
     * @param supplierWithException 可能抛出异常的 Supplier
     * @param defaultValue          Supplier 抛出异常时返回的默认值
     * @return 包装后的 Supplier
     * @throws IllegalArgumentException 如果参数 supplierWithException 为空
     */
    public static <T> Supplier<T> nonEx(Supplier<T> supplierWithException, T defaultValue) {
        if (supplierWithException == null) throw new IllegalArgumentException();
        return () -> {
            try {
                return supplierWithException.get();
            } catch (Exception e) {
                return defaultValue;
            }
        };
    }

    /**
     * 将 {@link Function} 包装为抛出异常 {@link Exception} 时返回 null 的 Function
     *
     * @param <T>               泛型(参数类型)
     * @param <R>               泛型(返回值)
     * @param funcWithException 可能抛出异常的 Function
     * @return 包装后的 Function
     * @throws IllegalArgumentException 如果参数 funcWithException 为空
     */
    public static <T, R> Function<T, R> nonEx(Function<T, R> funcWithException) {
        return nonEx(funcWithException, null);
    }

    /**
     * 将 {@link Function} 包装为抛出异常 {@link Exception} 时返回默认值的 Function
     *
     * @param <T>               泛型(参数类型)
     * @param <R>               泛型(返回值)
     * @param funcWithException 可能抛出异常的 Function
     * @param defaultValue      Function 抛出异常时返回的默认值
     * @return 包装后的 Function
     * @throws IllegalArgumentException 如果参数 funcWithException 为空
     */
    public static <T, R> Function<T, R> nonEx(Function<T, R> funcWithException, R defaultValue) {
        if (funcWithException == null) throw new IllegalArgumentException();
        return param -> {
            try {
                return funcWithException.apply(param);
            } catch (Exception e) {
                return defaultValue;
            }
        };
    }

    /**
     * 返回一个内部自带计数器的 {@link Consumer}
     * <p>计数器从 0 开始自增。每次 {@link Consumer#accept(Object)} 被调用时，参数值和计数器的值会传递给
     * {@link ObjIntConsumer#accept(Object, int)}，之后计数器会自动 +1。
     * </p><p>
     * 可配合 {@link java.util.List#forEach(Consumer)} 等方法使用，进行计数循环。<i>类似于 Kotlin 的 forEachIndexed</i></p>
     * <pre>例如: {@code
     *  List<String> list = Arrays.asList("A", "B", "C");
     *  list.forEach(FuncBuilder.withIndex((val, i) ->
     *      System.out.println("Item " + i + ": " + val)));
     * }</pre>
     * <pre>输出结果为：
     * Item 0: A
     * Item 1: B
     * Item 2: C
     * </pre>
     *
     * @param <T>             泛型(参数类型)
     * @param indexedConsumer 接收一个对象 {@code T} 和 序号 的 {@link ObjIntConsumer}
     * @return 包装后的 Consumer
     * @throws IllegalArgumentException 如果参数 indexedConsumer 为空
     */
    public static <T> Consumer<T> withIndex(ObjIntConsumer<T> indexedConsumer) {
        if (indexedConsumer == null) throw new IllegalArgumentException();
        return new Consumer<T>() {
            private int i = 0;

            @Override
            public void accept(T t) {
                indexedConsumer.accept(t, i++);
            }
        };
    }

    /**
     * 将 {@link Callable} 包装为抛出异常 {@link Exception} 时返回默认值的 Supplier
     *
     * @param <T>          泛型(参数类型)
     * @param callable     Callable 对象
     * @param defaultValue Callable 抛出异常时返回的默认值
     * @return 包装后的 Supplier
     * @throws IllegalArgumentException 如果参数 callable 为空
     */
    public static <T> Supplier<T> toSupplier(Callable<T> callable, T defaultValue) {
        if (callable == null) throw new IllegalArgumentException();
        return () -> {
            try {
                return callable.call();
            } catch (Exception e) {
                return defaultValue;
            }
        };
    }

    /**
     * 将 {@link Runnable} 包装为返回指定值的 Supplier
     *
     * @param <T>      泛型(参数类型)
     * @param runnable Runnable 对象
     * @param value    Supplier 的返回值
     * @return 包装后的 Supplier
     * @throws IllegalArgumentException 如果参数 runnable 为空
     */
    public static <T> Supplier<T> toSupplier(Runnable runnable, T value) {
        if (runnable == null) throw new IllegalArgumentException();
        return () -> {
            runnable.run();
            return value;
        };
    }

}
