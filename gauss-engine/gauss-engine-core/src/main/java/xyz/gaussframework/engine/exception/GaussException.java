package xyz.gaussframework.engine.exception;

public abstract class GaussException extends RuntimeException {

    protected Exception superException;

    public GaussException() {
        superException = new RuntimeException();
    }

    public GaussException(Exception e) {
        superException = e;
    }

    public GaussException(String message) {
        superException = new RuntimeException(message);
    }

    public GaussException(String message, Exception superException) {
        super(message);
        this.superException = superException;
    }
}
