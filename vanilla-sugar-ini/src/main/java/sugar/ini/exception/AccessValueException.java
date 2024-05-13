package sugar.ini.exception;

/**
 * 抛出时表示遇到无法读取 INI 中特定值的情况
 */
public class AccessValueException extends RuntimeException {
    public AccessValueException(String message) {
        super(message);
    }
}
