package xyz.gaussframework.engine.exception;

public class GaussMapperException extends GaussException {

    private final String message;

    public GaussMapperException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
