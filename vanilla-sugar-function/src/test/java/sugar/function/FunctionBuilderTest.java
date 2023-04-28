package sugar.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

class FunctionBuilderTest {

    @Test
    void former() {
        BinaryOperator<String> former = FunctionBuilder.former();
        Assertions.assertEquals("1", former.apply("1", "2"));
        Assertions.assertNotEquals("2", former.apply("1", "2"));
    }

    @Test
    void latter() {
        BinaryOperator<String> former = FunctionBuilder.latter();
        Assertions.assertEquals("2", former.apply("1", "2"));
        Assertions.assertNotEquals("1", former.apply("1", "2"));
    }

    @Test
    void not() {
        Predicate<?> truePredicate = t -> true;
        Predicate<?> falsePredicate = t -> false;
        Assertions.assertTrue(FunctionBuilder.not(falsePredicate).test(null));
        Assertions.assertFalse(FunctionBuilder.not(truePredicate).test(null));
        Predicate<List<?>> notEmptyPredicate = FunctionBuilder.not(List::isEmpty);
        Assertions.assertFalse(notEmptyPredicate.test(Collections.emptyList()));
        Assertions.assertTrue(notEmptyPredicate.test(Collections.singletonList("")));
    }

    static Supplier<Object> exSupplier = () -> {
        throw new InnerException();
    };
    static Function<Object, Object> exFunction = t -> {
        throw new InnerException();
    };

    @Test
    void nonExSupplier() {
        String defaultValue = "return";
        Assertions.assertThrowsExactly(InnerException.class, () -> exSupplier.get());
        Assertions.assertNull(FunctionBuilder.nonEx(exSupplier).get());
        Assertions.assertEquals(defaultValue, FunctionBuilder.nonEx(exSupplier, defaultValue).get());
        List<String> list = Arrays.asList("0", "1", "2");
        String defalutString = "-1";
        Assertions.assertEquals("1", FunctionBuilder.nonEx(() -> list.get(1)).get());
        Supplier<String> supplierWithException = () -> list.get(4);
        Assertions.assertEquals(defalutString, FunctionBuilder.nonEx(supplierWithException, defalutString).get());
        Assertions.assertNull(FunctionBuilder.nonEx(supplierWithException).get());
        Assertions.assertThrows(IndexOutOfBoundsException.class, supplierWithException::get);
    }

    @Test
    void nonExFunction() {
        String param = "in";
        String defaultValue = "return";
        Assertions.assertThrowsExactly(InnerException.class, () -> exFunction.apply(param));
        Assertions.assertNull(FunctionBuilder.nonEx(exFunction).apply(param));
        Assertions.assertEquals(defaultValue, FunctionBuilder.nonEx(exFunction, defaultValue).apply(param));
        List<String> list = Arrays.asList("0", "1", "2");
        String defalutString = "-1";
        Function<Integer, String> funcWithException = list::get;
        Assertions.assertEquals("1", FunctionBuilder.nonEx(funcWithException).apply(1));
        Assertions.assertEquals(defalutString, FunctionBuilder.nonEx(funcWithException, defalutString).apply(4));
        Assertions.assertNull(FunctionBuilder.nonEx(funcWithException).apply(4));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> funcWithException.apply(4));
    }

    @Test
    void toSupplier() {
        Callable<String> callable = () -> {
            throw new IOException();
        };
        Assertions.assertThrowsExactly(IOException.class, callable::call);
        String expected = "return";
        Assertions.assertNull(FunctionBuilder.toSupplier(callable, null).get());
        Assertions.assertEquals(expected, FunctionBuilder.toSupplier(callable, expected).get());
        Assertions.assertEquals(expected, FunctionBuilder.toSupplier(() -> expected, null).get());
    }

    @Test
    void indexConsumer() {
        Arrays.asList(0, 1, 2, 3).forEach(FunctionBuilder.withIndex(Assertions::assertEquals));
        Arrays.asList("0123456789".split("")).forEach(FunctionBuilder.withIndex((s, i) ->
                Assertions.assertEquals(Integer.parseInt(s), i)));
    }

    public static class InnerException extends RuntimeException {
    }
}