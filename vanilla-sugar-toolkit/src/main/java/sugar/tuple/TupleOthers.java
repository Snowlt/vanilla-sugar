package sugar.tuple;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 元组的默认实现类
 * <p><i>请使用 {@link Tuple#of(Object...)} 创建元组</i>
 */
public class TupleOthers implements Tuple {
    private final Object[] elements;

    TupleOthers(Object... elements) {
        if (elements == null || elements.length == 0) {
            this.elements = EMPTY;
        } else {
            Object[] na = new Object[elements.length];
            System.arraycopy(elements, 0, na, 0, elements.length);
            this.elements = na;
        }
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public Object get(int index) {
        return elements[index];
    }

    @Override
    public List<Object> toList() {
        return Arrays.asList(elements);
    }

    @Override
    public Stream<Object> stream() {
        return Arrays.stream(elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleOthers objects = (TupleOthers) o;
        return Arrays.equals(elements, objects.elements);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    static final Object[] EMPTY = new Object[0];
}
