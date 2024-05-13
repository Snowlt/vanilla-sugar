package sugar.ini.exception;

/**
 * 抛出时表示通过 I/O 读写 INI 时出现意外的情况
 */
public class ReadWriteException extends RuntimeException {
    public ReadWriteException(String message) {
        super(message);
    }

    public ReadWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
