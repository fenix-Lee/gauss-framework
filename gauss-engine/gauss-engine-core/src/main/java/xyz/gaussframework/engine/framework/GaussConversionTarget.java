package xyz.gaussframework.engine.framework;

class GaussConversionTarget<T> implements Target<T> {

    private final Class<T> type;

    private final String name;

    private final GaussConversion<Object,Object> conversion;

    public GaussConversionTarget (Class<T> type, String name, GaussConversion<Object, Object> conversion) {
        this.type = type;
        this.name = name;
        this.conversion = conversion;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

    public GaussConversion<Object,Object> getConversion () {
        return conversion;
    }
}
