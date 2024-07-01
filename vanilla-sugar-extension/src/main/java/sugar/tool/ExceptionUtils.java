package sugar.tool;

import java.util.*;
import java.util.function.Function;

public class ExceptionUtils {

    /**
     * 仅在 {@link Throwable#getMessage()} 不为空字符串时直接返回内容，否则返回 {@code null}。
     * 如果 {@code e} 为 {@code null} 也会返回 {@code null}。
     *
     * @param e 异常
     * @return 异常信息
     * @see #getNotEmptyMessage(Throwable, String)
     */
    public static String getNotEmptyMessage(Throwable e) {
        return getNotEmptyMessage(e, null);
    }

    /**
     * 如果 {@link Throwable#getMessage()} 不为空字符串或 {@code null} 则直接返回内容，否则返回 {@code defaultMessage}。
     * 如果 {@code e} 为 {@code null} 也会返回 {@code defaultMessage}。
     *
     * @param e              异常
     * @param defaultMessage 默认消息
     * @return 异常信息
     */
    public static String getNotEmptyMessage(Throwable e, String defaultMessage) {
        if (e == null) return defaultMessage;
        String message = e.getMessage();
        return message != null && !message.isEmpty() ? message : defaultMessage;
    }

    public static String trimExceptionAsString(Throwable e, int limit, String trimmedText) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder builder = new StringBuilder(e.getClass().getCanonicalName());
        String message = e.getMessage();
        if (message != null && !message.isEmpty()) {
            builder.append(": ").append(message).append("\n");
        }
        Arrays.stream(stackTrace).limit(limit).map(Object::toString).forEach(s -> builder.append("\t").append(s).append("\n"));
        if (stackTrace.length > limit) {
            builder.append("\t").append(trimmedText).append("\n");
        }
        return builder.toString();
    }

    /**
     * 判断异常是否为指定的类型或其子类型
     * <p>如果传入的异常 {@code e} 或者要检查的类型列表 {@code classes} 为 {@code null}，则返回 {@code false}。
     * 例如：<pre>
     * isClassOf(null, Exception.class) = false
     * isClassOf(new FileNotFoundException(), RuntimeException.class) = false
     * isClassOf(new FileNotFoundException(), IOException.class, RuntimeException.class) = true
     * isClassOf(new IllegalArgumentException(), IllegalArgumentException.class) = true
     * </pre>
     *
     * @param e       异常
     * @param classes 要检查的异常所属类型
     * @return 异常属于所检查的类型或子类型则返回 true，否则返回 false
     */
    @SafeVarargs
    public static boolean isTypeOf(Throwable e, Class<? extends Throwable>... classes) {
        if (e == null || classes == null || classes.length == 0) return false;
        Class<? extends Throwable> actual = e.getClass();
        for (Class<? extends Throwable> excepted : classes) {
            if (excepted.isAssignableFrom(actual)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从传入的异常中，获取嵌套异常中最内层的异常
     * <p>从 {@code e} 开始，根据 {@link Throwable#getCause()} 依次向上层查找，找到的最后一层即是最内层。</p>
     * <p>由于查找内层异常为递归查找，当异常内出现循环引用可能导致无限循环，故方法在首次遇到重复出现过的异常时会自动停止查找。</p>
     *
     * @param e 要处理的异常，可为 {@code null}
     * @return 最深层的异常。如果 {@code e} 为 {@code null} 则方法返回 {@code null}
     * @see #getThrowableList(Throwable)
     */
    public static Throwable getRootCause(Throwable e) {
        if (e == null) return null;
        Set<Throwable> set = new HashSet<>();
        Throwable cause = e.getCause();
        while (cause != null && set.add(e)) {
            e = cause;
            cause = cause.getCause();
        }
        return e;
    }

    /**
     * 获取传入的异常中所有嵌套的异常
     * <p>从 {@code e} 开始，根据 {@link Throwable#getCause()} 依次向上层查找，并按顺序返回。</p>
     * <p>如果 {@code e} 为 {@code null} ，则方法会返回一个空 {@link List}。</p>
     * <p>由于查找内层异常为递归查找，当异常内出现循环引用可能导致无限循环，故方法在首次遇到重复出现过的异常时会自动停止查找。</p>
     *
     * @param e 要处理的异常，可为 {@code null}
     * @return 按顺序获取的异常列表
     */
    public static List<Throwable> getThrowableList(Throwable e) {
        if (e == null) return Collections.emptyList();
        List<Throwable> list = new ArrayList<>(1);
        for (; e != null && !list.contains(e); e = e.getCause()) {
            list.add(e);
        }
        return list;
    }

    /**
     * 如果传入的异常属于 {@link RuntimeException} 则直接抛出，否则先包装为 {@link RuntimeException} 再抛出。
     *
     * @param e 异常
     * @throws RuntimeException {@code e} 或包装后抛出的异常
     */
    public static void throwAsRuntime(Throwable e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw new RuntimeException(e);
        }
    }

    /**
     * 如果传入的异常属于 {@link RuntimeException} 则直接抛出，否则先调用 {@code wrapper} 包装为 {@link RuntimeException} 再抛出。
     *
     * @param e       异常
     * @param wrapper 将 {@link Exception} 受检异常包装为 {@link RuntimeException} 非受检异常的函数
     * @throws IllegalArgumentException 如果 {@code wrapper} 为 {@code null} 则抛出
     * @throws RuntimeException         {@code e} 或包装后抛出的异常
     */
    public static void throwAsRuntime(Throwable e, Function<Throwable, ? extends RuntimeException> wrapper) {
        if (wrapper == null) throw new IllegalArgumentException();
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw wrapper.apply(e);
        }
    }
}
