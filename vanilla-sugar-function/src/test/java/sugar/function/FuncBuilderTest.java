package sugar.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

class FuncBuilderTest {

    @Test
    void former() {
        BinaryOperator<String> former = FuncBuilder.former();
        Assertions.assertEquals("1", former.apply("1", "2"));
        Assertions.assertNotEquals("2", former.apply("1", "2"));
    }

    @Test
    void latter() {
        BinaryOperator<String> former = FuncBuilder.latter();
        Assertions.assertEquals("2", former.apply("1", "2"));
        Assertions.assertNotEquals("1", former.apply("1", "2"));
    }

    @Test
    void not() {
        Predicate<?> truePredicate = t -> true;
        Predicate<?> falsePredicate = t -> false;
        Assertions.assertTrue(FuncBuilder.not(falsePredicate).test(null));
        Assertions.assertFalse(FuncBuilder.not(truePredicate).test(null));
        Predicate<List<?>> notEmptyPredicate = FuncBuilder.not(List::isEmpty);
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
        Assertions.assertNull(FuncBuilder.nonEx(exSupplier).get());
        Assertions.assertEquals(defaultValue, FuncBuilder.nonEx(exSupplier, defaultValue).get());
        List<String> list = Arrays.asList("0", "1", "2");
        String defalutString = "-1";
        Assertions.assertEquals("1", FuncBuilder.nonEx(() -> list.get(1)).get());
        Supplier<String> supplierWithException = () -> list.get(4);
        Assertions.assertEquals(defalutString, FuncBuilder.nonEx(supplierWithException, defalutString).get());
        Assertions.assertNull(FuncBuilder.nonEx(supplierWithException).get());
        Assertions.assertThrows(IndexOutOfBoundsException.class, supplierWithException::get);
    }

    @Test
    void nonExFunction() {
        String param = "in";
        String defaultValue = "return";
        Assertions.assertThrowsExactly(InnerException.class, () -> exFunction.apply(param));
        Assertions.assertNull(FuncBuilder.nonEx(exFunction).apply(param));
        Assertions.assertEquals(defaultValue, FuncBuilder.nonEx(exFunction, defaultValue).apply(param));
        List<String> list = Arrays.asList("0", "1", "2");
        String defalutString = "-1";
        Function<Integer, String> funcWithException = list::get;
        Assertions.assertEquals("1", FuncBuilder.nonEx(funcWithException).apply(1));
        Assertions.assertEquals(defalutString, FuncBuilder.nonEx(funcWithException, defalutString).apply(4));
        Assertions.assertNull(FuncBuilder.nonEx(funcWithException).apply(4));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> funcWithException.apply(4));
    }

    @Test
    void toSupplier() {
        Callable<String> callable = () -> {
            throw new IOException();
        };
        Assertions.assertThrowsExactly(IOException.class, callable::call);
        final String expected = "return";
        Assertions.assertNull(FuncBuilder.toSupplier(callable, null).get());
        Assertions.assertEquals(expected, FuncBuilder.toSupplier(callable, expected).get());
        Assertions.assertEquals(expected, FuncBuilder.toSupplier(() -> expected, null).get());
    }

    @Test
    void toSupplierForRunnable() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        final String expected = "return";
        Assertions.assertFalse(atomicBoolean.get());
        Assertions.assertEquals(expected, FuncBuilder.toSupplier(()-> atomicBoolean.set(true), expected).get());
        Assertions.assertTrue(atomicBoolean.get());
        Assertions.assertEquals(1, FuncBuilder.toSupplier(()-> atomicBoolean.set(false), 1).get());
        Assertions.assertFalse(atomicBoolean.get());
    }

    @Test
    void indexConsumer() {
        Arrays.asList(0, 1, 2, 3).forEach(FuncBuilder.withIndex(Assertions::assertEquals));
        Arrays.asList("0123456789".split("")).forEach(FuncBuilder.withIndex((s, i) ->
                Assertions.assertEquals(Integer.parseInt(s), i)));
    }

    public static class InnerException extends RuntimeException {
    }
}