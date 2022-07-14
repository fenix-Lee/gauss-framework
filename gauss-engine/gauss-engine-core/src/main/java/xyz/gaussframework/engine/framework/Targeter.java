package xyz.gaussframework.engine.framework;

public interface Targeter {

    <T> T target(Target<T> target);

    class GaussTargeter implements Targeter {

        @Override
        public <T> T target(Target<T> target) {
            return newInstance(target);
        }

        private <T> T newInstance(Target<T> target) {
            // dynamic proxy
            return null;
        }
    }
}
