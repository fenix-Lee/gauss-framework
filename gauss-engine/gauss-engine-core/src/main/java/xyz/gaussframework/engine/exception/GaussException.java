package xyz.gaussframework.engine.exception;

public abstract class GaussException extends RuntimeException {

    protected Exception superException;

    public GaussException(Exception e) {
        superException = e;
    }

    public GaussException(String message) {
        superException = new RuntimeException(message);
    }
}
