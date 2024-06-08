package sugar.lambda;

public class LambdaParseException extends RuntimeException{
    public LambdaParseException(String message) {
        super(message);
    }

    public LambdaParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
