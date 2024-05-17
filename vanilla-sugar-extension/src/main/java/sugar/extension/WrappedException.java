package sugar.extension;

/**
 * 表示一个嵌套的异常。
 * <p>与 {@link java.util.concurrent.ExecutionException} 相似，实际的异常可以通过 {@link #getCause()} 获取。
 * <p>一般用于期望传递 {@code RuntimeException} 而非 {@code Exception} 时，包装异常。
 */
public class WrappedException extends RuntimeException {
    public WrappedException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrappedException(Throwable cause) {
        super(cause);
    }

}
