package sugar.tuple;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 表示有三个元素的元组，每个元素都可以有独立的类型
 *
 * @param <E0> 第一个元素的类型
 * @param <E1> 第二个元素的类型
 * @param <E2> 第三个元素的类型
 */
public class Tuple3<E0, E1, E2> implements Tuple {

    private final E0 e0;
    private final E1 e1;
    private final E2 e2;

    Tuple3(E0 e0, E1 e1, E2 e2) {
        this.e0 = e0;
        this.e1 = e1;
        this.e2 = e2;
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

    @Override
    public int size() {
        return 3;
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
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public List<Object> toList() {
        return Arrays.asList(e0, e1, e2);
    }

    @Override
    public Stream<Object> stream() {
        return Stream.of(e0, e1, e2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple3<?, ?, ?> t = (Tuple3<?, ?, ?>) o;
        return Objects.equals(e0, t.e0) && Objects.equals(e1, t.e1) && Objects.equals(e2, t.e2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e0, e1, e2);
    }
}
