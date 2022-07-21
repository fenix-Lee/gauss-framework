package xyz.gaussframework.engine.exception;

public class GaussFactoryException extends GaussException {

    private final String message;

    public GaussFactoryException(String message) {
        super(message);
        this.message = message;
    }

    public GaussFactoryException(Exception e) {
        super(e);
        this.message = e.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
