package sugar.tuple;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 元组(Tuple) 表示一个不可变的内容集合。
 * <p>此集合有长度，内部元素有序，每一个元素的类型都可以不同。等效于不可修改的对象数组。
 *
 * <p>主要用法如下：<ul>
 * <li>使用 {@code Tuple.of()} 方法创建一个元组对象</li>
 * <li>使用 {@link #get(int)} 方法获取元组中的元素</li>
 * <li>使用 {@link #size()} 方法获取元组中的长度</li>
 * <li>使用 {@link #toList()} 方法将元组转为标准 {@link List}</li>
 * <li>使用 {@link #stream()} 方法将元组的元素生成一个流对象</li>
 * </ul></p>
 *
 * <p>类 {@link Tuple2} 到 {@link Tuple6} 通过泛型为每个内部元素传递了单独的类型，可用于方法返回多个值等场景。
 * 可通过 {@code get*()} 方法获取到原始类型（例如 {@link Tuple6#get0()}, ..., {@link Tuple6#get5()}）。
 * </p>
 *
 * @author SnowLT
 * @version 1.0
 */
public interface Tuple extends Iterable<Object> {

    /**
     * 使用给定的元素创建 {@link Tuple2}
     *
     * @param <E0>     第一个元素的类型
     * @param <E1>     第二个元素的类型
     * @param element0 第一个元素
     * @param element1 第二个元素
     * @return {@link Tuple2}
     */
    static <E0, E1> Tuple2<E0, E1> of(E0 element0, E1 element1) {
        return new Tuple2<>(element0, element1);
    }

    /**
     * 使用给定的元素创建 {@link Tuple3}
     *
     * @param <E0>     第一个元素的类型
     * @param <E1>     第二个元素的类型
     * @param <E2>     第三个元素的类型
     * @param element0 第一个元素
     * @param element1 第二个元素
     * @param element2 第三个元素
     * @return {@link Tuple3}
     */
    static <E0, E1, E2> Tuple3<E0, E1, E2> of(E0 element0, E1 element1, E2 element2) {
        return new Tuple3<>(element0, element1, element2);
    }

    /**
     * 使用给定的元素创建 {@link Tuple4}
     *
     * @param <E0>     第一个元素的类型
     * @param <E1>     第二个元素的类型
     * @param <E2>     第三个元素的类型
     * @param <E3>     第四个元素的类型
     * @param element0 第一个元素
     * @param element1 第二个元素
     * @param element2 第三个元素
     * @param element3 第四个元素
     * @return {@link Tuple4}
     */
    static <E0, E1, E2, E3> Tuple4<E0, E1, E2, E3> of(E0 element0, E1 element1, E2 element2, E3 element3) {
        return new Tuple4<>(element0, element1, element2, element3);
    }

    /**
     * 使用给定的元素创建 {@link Tuple5}
     *
     * @param <E0>     第一个元素的类型
     * @param <E1>     第二个元素的类型
     * @param <E2>     第三个元素的类型
     * @param <E3>     第四个元素的类型
     * @param <E4>     第五个元素的类型
     * @param element0 第一个元素
     * @param element1 第二个元素
     * @param element2 第三个元素
     * @param element3 第四个元素
     * @param element4 第五个元素
     * @return {@link Tuple5}
     */
    static <E0, E1, E2, E3, E4> Tuple5<E0, E1, E2, E3, E4> of(E0 element0, E1 element1, E2 element2, E3 element3, E4 element4) {
        return new Tuple5<>(element0, element1, element2, element3, element4);
    }

    /**
     * 使用给定的元素创建 {@link Tuple6}
     *
     * @param <E0>     第一个元素的类型
     * @param <E1>     第二个元素的类型
     * @param <E2>     第三个元素的类型
     * @param <E3>     第四个元素的类型
     * @param <E4>     第五个元素的类型
     * @param <E5>     第六个元素的类型
     * @param element0 第一个元素
     * @param element1 第二个元素
     * @param element2 第三个元素
     * @param element3 第四个元素
     * @param element4 第五个元素
     * @param element5 第六个元素
     * @return {@link Tuple6}
     */
    static <E0, E1, E2, E3, E4, E5> Tuple6<E0, E1, E2, E3, E4, E5> of(E0 element0, E1 element1, E2 element2, E3 element3, E4 element4, E5 element5) {
        return new Tuple6<>(element0, element1, element2, element3, element4, element5);
    }

    /**
     * 使用给定的元素创建一个元组
     *
     * @param elements 元素
     * @return 元组
     */
    static Tuple of(Object... elements) {
        return new TupleOthers(elements);
    }

    /**
     * 获取集合的长度（元素的总数量）
     *
     * @return 长度
     */
    int size();

    /**
     * 获取在指定位置的元素（从0开始）
     * <p>由于元素可以是任意类型所以返回类型是 Object</p>
     *
     * @param index 位置
     * @return index 指定位置的元素
     * @throws IndexOutOfBoundsException 索引超出范围时抛出（大于等于 size() 或为负数）
     */
    Object get(int index);

    /**
     * 将内容转换为一个 {@link List} 对象
     * <p><i>此 List 不一定是可修改的（例如 {@link java.util.ArrayList}）</i></p>
     *
     * @return List 对象
     */
    List<Object> toList();

    /**
     * 将内容生成为一个 {@link Stream} 流
     *
     * @return Stream 对象
     */
    default Stream<Object> stream() {
        return toList().stream();
    }

    /**
     * 将内容转换为一个数组
     *
     * @return List 对象
     */
    default Object[] toArray() {
        return toList().toArray();
    }

    @Override
    default Iterator<Object> iterator() {
        return toList().iterator();
    }

}
