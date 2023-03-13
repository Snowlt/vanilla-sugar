package sugar.tuple;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 表示有两个元素的元组，每个元素都可以有独立的类型
 *
 * @param <E0> 第一个元素的类型
 * @param <E1> 第二个元素的类型
 */
public class Tuple2<E0, E1> implements Tuple {

    private final E0 e0;
    private final E1 e1;

    Tuple2(E0 e0, E1 e1) {
        this.e0 = e0;
        this.e1 = e1;
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

    @Override
    public int size() {
        return 2;
    }

    @Override
    public Object get(int index) {
        switch (index) {
            case 0:
                return e0;
            case 1:
                return e1;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public List<Object> toList() {
        return Arrays.asList(e0, e1);
    }

    @Override
    public Stream<Object> stream() {
        return Stream.of(e0, e1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple2<?, ?> t = (Tuple2<?, ?>) o;
        return Objects.equals(e0, t.e0) && Objects.equals(e1, t.e1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e0, e1);
    }

}
