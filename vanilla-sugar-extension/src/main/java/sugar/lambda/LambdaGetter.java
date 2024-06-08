package sugar.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 用于封装对象字段 getter 方法的 Lambda 函数
 *
 * @param <T> 对象的类型
 * @param <V> 字段值的类型
 * @author SnowLT
 */
@FunctionalInterface
public interface LambdaGetter<T, V> extends Serializable {
    /**
     * 获取一个对象中的字段值
     *
     * @param t 对象
     * @return 字段值
     */
    V invoke(T t);

    /**
     * 转换为 {@link Function} 对象
     *
     * @return {@link Function}
     */
    default Function<T, V> toFunction() {
        return this::invoke;
    }
}
