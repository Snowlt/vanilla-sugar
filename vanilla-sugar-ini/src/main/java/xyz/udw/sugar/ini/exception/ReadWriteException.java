package xyz.udw.sugar.ini.exception;

public class ReadWriteException extends RuntimeException{
    public ReadWriteException(String message) {
        super(message);
    }

    public ReadWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
