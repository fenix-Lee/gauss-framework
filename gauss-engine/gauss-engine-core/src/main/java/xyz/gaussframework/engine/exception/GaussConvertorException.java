package xyz.gaussframework.engine.exception;

public class GaussConvertorException extends GaussException {

    String message;

    public GaussConvertorException(String message) {
        super(message);
        this.message = message;
    }
}
