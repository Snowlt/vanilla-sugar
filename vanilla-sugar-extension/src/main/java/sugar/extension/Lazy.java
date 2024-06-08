package sugar.extension;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 提供延迟初始化的容器
 * <p>构造 Lazy<T> 实例时可指定一个初始化方法（{@link Supplier}），在首次访问实例 {@link #value()}
 * 时才调用指定的初始化方法，并缓存初始化的结果，达到延迟初始化并只触发一次的效果。
 * <p>对比已有实现主要增强了构造方法，异常处理，以及对多线程使用时的加锁控制，设计上参考
 * <a href="https://learn.microsoft.com/zh-cn/dotnet/api/system.lazy">.Net 中的 Lazy</a> 并移植了一些功能。
 *
 * <p>以下静态方法可以新建 Lazy<T> 实例，并提供不同的线程安全控制方式: <ol>
 * <li>{@link #ofThreadUnsafe(Supplier)}: 对<b>初始化方法</b>的调用是非线程安全的，适合对性能要求极高的情况</li>
 * <li>{@link #of(Supplier)}: 线程安全，一次只有一个线程尝试调用<b>初始化方法</b>，适合大部分多线程场景</li>
 * <li>{@link #ofPublication(Supplier)}: 允许多个线程并发调用<b>初始化方法</b>，竞争写入结果，但只有一个线程能赢得竞争</li>
 * </ol>
 * 三种方法构造的 Lazy<T> 实例，都会缓存初始化的结果。如果执行初始化方法时抛出了异常，{@link #ofPublication(Supplier)}
 * 构造的实例不缓存异常，下次调用 {@link #value()} 会再触发执行初始化方法；其余方法构造的实例会将异常也缓存起来，下次调用
 * {@link #value()} 时直接抛出。
 *
 * @author SnowLT
 * @see org.apache.commons.lang3.concurrent.LazyInitializer
 * @see org.springframework.data.util.Lazy
 * @since 2023/11/17
 */
public class Lazy<T> {
    private Supplier<T> factory;
    private T value;
    private volatile boolean initialized = false;
    private final byte threadSafeMode;
    private Throwable exception;

    /**
     * 新建一个容器，并使用指定的初始化方法来初始化对象。
     * <p>这个容器是非线程安全的，若多个线程同时初始化 Lazy<T> 实例，其行为不确定。建议仅在单线程，或外部已加锁（线程安全）的情况下使用。
     * <p>初始化结果会被缓存（无论是正常返回还是抛出异常）。
     *
     * @param <T>          值的类型
     * @param valueFactory 初始化方法
     * @return 容器
     */
    public static <T> Lazy<T> ofThreadUnsafe(Supplier<T> valueFactory) {
        return new Lazy<>(valueFactory, NONE);
    }

    /**
     * 新建一个容器，并使用指定的初始化方法来初始化对象。
     * <p>这个容器是线程安全的。内部使用锁来确保只有一个线程可以在线程安全的方式下初始化 Lazy<T> 实例。
     * <p>初始化结果会被缓存（无论是正常返回还是抛出异常）。
     *
     * @param <T>          值的类型
     * @param valueFactory 初始化方法
     * @return 容器
     */
    public static <T> Lazy<T> of(Supplier<T> valueFactory) {
        return new Lazy<>(valueFactory, EXECUTION_AND_PUBLICATION);
    }

    /**
     * 新建一个容器，并使用指定的初始化方法来初始化对象。
     * <p>当多个线程尝试同时初始化一个 Lazy<T>
     * 实例时，允许所有线程都运行初始化方法，完成初始化的第一个线程设置 Lazy<T> 实例的值，并且该值将返回给同时运行初始化方法的所有其他线程
     * （除非运行初始化方法时抛出了异常）。
     * <p>从某种意义上说，只有一个初始化的值可以被发布和被所有线程返回，初始化值的发布是线程安全的。
     * <p>请注意此方法返回的实例如果在执行初始化方法时抛出异常，则直接从当前线程上抛出，不缓存该异常。如果初始化方法递归访问
     * Lazy<T> 实例的 {@link #value()} 也不会主动引发异常。
     * <p>移植所参考的详细资料可见 <a href="https://learn.microsoft.com/en-us/dotnet/api/system.threading.lazythreadsafetymode">
     * .Net 中的 LazyThreadSafetyMode.PublicationOnly</a>。
     *
     * @param <T>          值的类型
     * @param valueFactory 初始化方法
     * @return 容器
     */
    public static <T> Lazy<T> ofPublication(Supplier<T> valueFactory) {
        return new Lazy<>(valueFactory, PUBLICATION_ONLY);
    }

    /**
     * 新建一个容器，使用指定的初始化方法来初始化对象。并手动指定初始化对象时是否线程安全。
     * <p>当 isThreadSafe 为 true 时，容器是线程安全的，等效于使用 {@link #of(Supplier)}；
     * <p>当 isThreadSafe 为 false 时，容器是非线程安全的，等效于使用 {@link #ofThreadUnsafe(Supplier)} 返回值。
     *
     * @param valueFactory 初始化方法
     * @param isThreadSafe 线程安全，true 保证线程安全，false 不对并发做控制
     */
    public Lazy(Supplier<T> valueFactory, boolean isThreadSafe) {
        this(valueFactory, isThreadSafe ? EXECUTION_AND_PUBLICATION : NONE);
    }

    /**
     * 返回当前 Lazy<T> 实例的结果是否已成功初始化。如果调用初始化方法时抛出异常，则也会返回 false。
     *
     * @return 如果已初始化，则返回 true，否则返回 false。
     */
    public boolean isValueCreated() {
        return initialized && exception == null;
    }

    /**
     * 获取当前 Lazy<T> 实例的延迟初始化值。如果此实例从未被初始化过，则先调用指定初始化方法，再返回结果。
     *
     * @return 初始化结果（值）
     * @throws RuntimeException      调用初始化方法时抛出的异常
     * @throws IllegalStateException 非线程安全的情况下并发访问，或初始化函数尝试递归访问此实例上的 value() 方法
     */
    public T value() {
        if (!initialized) {
            createValue();
        }
        if (exception != null) {
            throw exception instanceof RuntimeException ? (RuntimeException) exception :
                    new WrappedException("[Checked Exception]", exception);
        }
        return value;
    }

    /**
     * 非线程安全
     * 初始化结果会被缓存（无论是正常返回还是抛出异常）。
     */
    private static final byte NONE = 0;

    /**
     * 线程安全（单线程初始化）
     * 使用锁来确保只有一个线程可以在线程安全的方式下初始化 Lazy<T> 实例。
     * 初始化结果会被缓存（无论是正常返回还是抛出异常）。
     */
    private static final byte EXECUTION_AND_PUBLICATION = 1;

    /**
     * 线程安全（多线程初始化）
     * 当多个线程尝试同时初始化一个 Lazy<T> 实例时，允许所有线程都运行初始化方法，完成初始化的第一个线程设置 Lazy<T> 实例的值。
     * 如果在执行初始化方法时抛出异常，则直接从当前线程上抛出，不缓存该异常。
     */
    private static final byte PUBLICATION_ONLY = 2;

    /**
     * 新建一个容器，并使用指定的初始化方法初始化对象。
     * 并指定初始化对象时的线程安全模式
     *
     * @param valueFactory   初始化方法
     * @param threadSafeMode 指定工厂模式的触发模式
     */
    private Lazy(Supplier<T> valueFactory, byte threadSafeMode) {
        Objects.requireNonNull(valueFactory);
        this.threadSafeMode = threadSafeMode;
        this.factory = valueFactory;
    }

    private void createValue() {
        if (threadSafeMode == NONE) {
            Supplier<T> valueFactory = this.factory;
            if (valueFactory == null) {
                throw new IllegalStateException("并发调用或初始化方法递归访问 value()");
            }
            this.factory = null;
            invokeValueFactory(valueFactory);
        } else if (threadSafeMode == PUBLICATION_ONLY) {
            createValuePublicationMode();
        } else  {
            if (!initialized) {
                synchronized (this) {
                    if (!initialized) {
                        Supplier<T> valueFactory = this.factory;
                        if (valueFactory == null) {
                            throw new IllegalStateException("初始化方法递归访问 value()");
                        }
                        this.factory = null;
                        invokeValueFactory(valueFactory);
                    }
                }
            }
        }
    }

    private void createValuePublicationMode() {
        Supplier<T> valueFactory;
        if (initialized || (valueFactory = this.factory) == null) {
            return;
        }
        T possibleValue = valueFactory.get();
        synchronized (this) {
            if (!initialized) {
                this.factory = null;
                this.value = possibleValue;
                this.initialized = true;
            }
        }
    }

    private void invokeValueFactory(Supplier<T> valueFactory) {
        try {
            this.value = valueFactory.get();
        } catch (Throwable e) {
            // 防止用一些方式方式绕过了编译检查（例如 Lombok），在此处捕获到非 RuntimeException
            this.exception = e;
        } finally {
            // 出于加锁的目的必须在 value 写入后执行
            this.initialized = true;
        }
    }

}
