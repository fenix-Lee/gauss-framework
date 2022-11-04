package xyz.gaussframework.engine.framework;


interface Target<T> {

    Class<T> type();

    String name();

    class GaussTarget<T> implements Target<T> {

        private final Class<T> type;

        private final String name;

        public GaussTarget(Class<T> type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public String name() {
            return name;
        }
    }

    class GaussConversionTarget<T> extends GaussTarget<T> {

        private final GaussConversion<Object,Object> conversion;

        public GaussConversionTarget (Class<T> type, String name, GaussConversion<Object, Object> conversion) {
            super(type, name);
            this.conversion = conversion;
        }

        public GaussConversion<Object,Object> getConversion () {
            return conversion;
        }
    }
}
