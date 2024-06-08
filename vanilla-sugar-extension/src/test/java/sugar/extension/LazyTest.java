package sugar.extension;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class LazyTest {

    Logger log = LoggerFactory.getLogger(LazyTest.class);

    public static final int THREAD_AMOUNT = 1000;

    @Test
    void initThreadSafe() {
        AtomicInteger counter = new AtomicInteger(0);
        Lazy<String> lazy = new Lazy<>(() -> {
            counter.addAndGet(1);
            trySleep();
            return "result";
        }, true);
        runParallel(lazy::value);
        assertEquals("result", lazy.value());
        assertTrue(lazy.isValueCreated());
        assertEquals(1, counter.get());
        assertEquals("result", lazy.value());
        assertTrue(lazy.isValueCreated());
        assertEquals(1, counter.get());
    }

    @Test
    void initThreadUnsafe() {
        Lazy<String> lazy = new Lazy<>(() -> "result", false);
        assertFalse(lazy.isValueCreated());
        assertEquals("result", lazy.value());
        assertTrue(lazy.isValueCreated());
        AtomicInteger invokeCounter = new AtomicInteger(0);
        lazy = new Lazy<>(() -> {
            invokeCounter.addAndGet(1);
            trySleep();
            return "result";
        }, false);
        List<Future<Void>> futures = runParallel(lazy::value);
        long exceptionCount = futures.stream().map(voidFuture -> {
                    try {
                        voidFuture.get();
                        return null;
                    } catch (ExecutionException e) {
                        log.debug(() -> "多线程中的异常: " + e.getCause().toString());
                        return e.getCause();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).filter(e -> e instanceof IllegalStateException)
                .count();
        assertTrue(exceptionCount != 0 || invokeCounter.get() > 1);
        assertEquals("result", lazy.value());
    }

    @Test
    void initWithExceptionThreadSafe() {
        AtomicInteger counter = new AtomicInteger(0);
        Lazy<String> lazy = new Lazy<>(() -> {
            counter.addAndGet(1);
            trySleep();
            throw new IllegalStateException();
        }, true);
        runParallel(lazy::value);
        assertEquals(1, counter.get());
        assertFalse(lazy.isValueCreated());
        try {
            lazy.value();
            fail("Expected thrown exception");
        } catch (IllegalStateException ignored) {

        } catch (Exception e) {
            fail("Expected thrown: IllegalStateException, current: " + e);
        }
        assertEquals(1, counter.get());
        assertFalse(lazy.isValueCreated());
    }

    @Test
    void initPublicationMode() {
        AtomicInteger counter = new AtomicInteger(0);
        final int totalCount = 5;
        final int factor = 7;
        ExecutorService executorService = new ThreadPoolExecutor(totalCount, totalCount, 0, TimeUnit.SECONDS,
                new SynchronousQueue<>());
        ThreadLocal<Integer> threadIndexHolder = new ThreadLocal<>();
        Lazy<Integer> lazy = Lazy.ofPublication(() -> {
            int i = threadIndexHolder.get();
            counter.addAndGet(1);
            trySleep(i * 100);
            return i * factor;
        });
        for (int i = 1; i <= totalCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                threadIndexHolder.set(finalI);
                lazy.value();
            });
        }
        executorService.shutdown();
        try {
            assertTrue(executorService.awaitTermination(1, TimeUnit.MINUTES));
        } catch (InterruptedException e) {
            throw new RuntimeException("Error occurred while waiting tasks", e);
        }
        assertTrue(lazy.isValueCreated());
        assertEquals(factor, lazy.value());
        assertEquals(totalCount, counter.get());
    }

    @Test
    void initPublicationModeSync() {
        AtomicInteger counter = new AtomicInteger(0);
        Lazy<Integer> lazy = Lazy.ofPublication(() -> {
            int i = counter.addAndGet(1);
            if (i == 1) {
                throw new StackOverflowError();
            }
            return i * 10;
        });
        assertFalse(lazy.isValueCreated());
        assertThrowsExactly(StackOverflowError.class, lazy::value);
        assertFalse(lazy.isValueCreated());
        assertEquals(1, counter.get());
        assertEquals(20, lazy.value());
        assertTrue(lazy.isValueCreated());
        assertEquals(2, counter.get());
    }

    @Test
    void initPublicationModeWithException() {
        AtomicInteger counter = new AtomicInteger(0);
        Lazy<String> lazy = Lazy.ofPublication(() -> {
            counter.addAndGet(1);
            throw new OutOfMemoryError();
        });
        runParallel(lazy::value);
        assertFalse(lazy.isValueCreated());
        assertEquals(THREAD_AMOUNT, counter.get());
        assertThrowsExactly(OutOfMemoryError.class, lazy::value, "Expect OOM to be thrown");
        assertFalse(lazy.isValueCreated());
        counter.set(0);
        final int result = 5;
        Lazy<Integer> lazy2 = Lazy.ofPublication(() -> {
            trySleep();
            int i = counter.addAndGet(1);
            if (i != result) {
                throw new OutOfMemoryError();
            }
            return i;
        });
        runParallel(lazy2::value);
        assertTrue(lazy2.isValueCreated());
        assertEquals(result, (int) lazy2.value());
        assertTrue(counter.get() >= 1);
    }

    @Test
    void recursiveInit() {
        AtomicReference<Supplier<String>> supplierHolder = new AtomicReference<>(() -> null);
        Lazy<String> lazy = new Lazy<>(() -> {
            trySleep();
            return supplierHolder.get().get();
        }, false);
        assertFalse(lazy.isValueCreated());
        supplierHolder.set(lazy::value);
        try {
            lazy.value();
            fail("Expected thrown exception");
        } catch (IllegalStateException ignored) {
        } catch (Exception e) {
            fail("Expected thrown: IllegalStateException, current: " + e);
        }
        assertFalse(lazy.isValueCreated());
    }

    private void trySleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            log.error(e, () -> "Interrupted while sleeping");
        }
    }

    private void trySleep() {
        trySleep(1);
    }

    @SuppressWarnings("unchecked")
    private static List<Future<Void>> runParallel(Runnable runnable) {
        AtomicInteger index = new AtomicInteger(0);
        ExecutorService executorService = new ThreadPoolExecutor(100, 100, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), r -> new Thread(r, String.valueOf(index.incrementAndGet())));
        List<Future<Void>> futures = new ArrayList<>(THREAD_AMOUNT);
        for (int i = 0; i < THREAD_AMOUNT; i++) {
            futures.add((Future<Void>) executorService.submit(runnable));
        }
        executorService.shutdown();
        try {
            assertTrue(executorService.awaitTermination(1, TimeUnit.MINUTES));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return futures;
    }

}