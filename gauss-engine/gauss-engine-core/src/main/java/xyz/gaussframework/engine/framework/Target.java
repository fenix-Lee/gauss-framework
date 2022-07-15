package xyz.gaussframework.engine.framework;


interface Target<T> {

    Class<T> type();

    String name();

    class GaussCodedTarget<T> implements Target<T> {

        private final Class<T> type;

        private final String name;

        public GaussCodedTarget(Class<T> type, String name) {
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
}
