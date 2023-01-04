package sugar.tuple;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 表示有四个元素的元组，每个元素都可以有独立的类型
 *
 * @param <E0> 第一个元素的类型
 * @param <E1> 第二个元素的类型
 * @param <E2> 第三个元素的类型
 * @param <E3> 第四个元素的类型
 */
public class Tuple4<E0, E1, E2, E3> implements Tuple {

    private final E0 e0;
    private final E1 e1;
    private final E2 e2;
    private final E3 e3;

    Tuple4(E0 e0, E1 e1, E2 e2, E3 e3) {
        this.e0 = e0;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    /**
     * 获取第一个元素
     */
    public E0 get0() {
        return e0;
    }

    /**
     * 获取第二个元素
     */
    public E1 get1() {
        return e1;
    }

    /**
     * 获取第三个元素
     */
    public E2 get2() {
        return e2;
    }

    /**
     * 获取第四个元素
     */
    public E3 get3() {
        return e3;
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public Object get(int index) {
        switch (index) {
            case 0:
                return e0;
            case 1:
                return e1;
            case 2:
                return e2;
            case 3:
                return e3;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public List<Object> toList() {
        return Arrays.asList(e0, e1, e2, e3);
    }

    @Override
    public Stream<Object> stream() {
        return Stream.of(e0, e1, e2, e3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple4<?, ?, ?, ?> t = (Tuple4<?, ?, ?, ?>) o;
        return Objects.equals(e0, t.e0) && Objects.equals(e1, t.e1) && Objects.equals(e2, t.e2) && Objects.equals(e3, t.e3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e0, e1, e2, e3);
    }
}
