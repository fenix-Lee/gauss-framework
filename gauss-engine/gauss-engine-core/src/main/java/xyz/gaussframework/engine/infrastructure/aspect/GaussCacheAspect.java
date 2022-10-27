package xyz.gaussframework.engine.infrastructure.aspect;

import lombok.Getter;
import org.aspectj.lang.JoinPoint;
import xyz.gaussframework.engine.framework.GaussBeanFactory;
import xyz.gaussframework.engine.framework.GaussCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Gauss Cache aspect class
 *
 * @author Chang Su
 * @version 1.1
 * @since 7/7/2022
 */
@Aspect
public class GaussCacheAspect {

    private static final Log logger = LogFactory.getLog(GaussCacheAspect.class);

    private static final int LRU_SIZE = 5;

    private static final long DEFAULT_EXPIRED_TIME = 30000;

    private static final GaussCacheContext CONTEXT = new GaussCacheContext(LRU_SIZE);

    private static final Map<Method, GaussCacheConfig> CONFIG = new ConcurrentHashMap<>();

    @Pointcut("@annotation(xyz.gaussframework.engine.framework.GaussCache)")
    public void cachePointcut() {}

    @Around("cachePointcut()")
    public Object cacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        Method proxyMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (proxyMethod.getReturnType().equals(void.class)) {
            return null;
        }

        String key = getKey(joinPoint, proxyMethod);
        if (!ObjectUtils.isEmpty(key) && CONTEXT.containsKey(key)) {
            return CONTEXT.get(key).getReference();
        }

        Object result = joinPoint.proceed();
        if (ObjectUtils.isEmpty(result)) {
            return result;
        }
        synchronized (CONTEXT) {
            CONTEXT.put(key, GaussContent.store(result));
        }
        setTimer(proxyMethod);
        return result;
    }

    @SuppressWarnings("unchecked")
    private String resolveExp(String[] parameterNames, String expressionKey, Object[] args) {
        if (ObjectUtils.isEmpty(expressionKey)) {
            return String.join(":", parameterNames) + "::" +
                    Arrays.stream(args).map(Object::toString)
                    .collect(Collectors.joining(":"));
        } else {
            EvaluationContext context = new StandardEvaluationContext();
            Expression expression = new SpelExpressionParser().parseExpression(expressionKey);
            for (int i = 0; i < Objects.requireNonNull(parameterNames).length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
            return String.join(":", ((List<String>) Objects.requireNonNull(expression.getValue(context))));
        }
    }

    private String getKey(JoinPoint joinPoint, Method proxyMethod) {
        if (CONFIG.containsKey(proxyMethod)) {
            return CONFIG.get(proxyMethod).getCacheKey();
        } else {
            GaussCache gaussCacheAnnotation = proxyMethod.getAnnotation(GaussCache.class);
            String targetName = joinPoint.getTarget().getClass().getName();
            String prefix = ObjectUtils.isEmpty(gaussCacheAnnotation.prefix())?
                    targetName : gaussCacheAnnotation.prefix() + ":" + targetName;
            String key;
            try {
                key = prefix + "::" + resolveExp(new DefaultParameterNameDiscoverer().getParameterNames(proxyMethod),
                        gaussCacheAnnotation.key(), joinPoint.getArgs());
            } catch (Exception e) {
                logger.error("");
                key = prefix + "::" + resolveExp(new String[]{"id"}, null, new Object[]{UUID.randomUUID()});
            }
            CONFIG.putIfAbsent(proxyMethod, GaussCacheConfig.create(gaussCacheAnnotation, key));
            return key;
        }
    }

    private long getExpire(long expire) {
        if (expire <= 0) {
            return DEFAULT_EXPIRED_TIME;
        }
        if (expire >= Integer.MAX_VALUE) {
            return DEFAULT_EXPIRED_TIME;
        }
        return expire;
    }

    static class GaussCacheContext extends LinkedHashMap<String, GaussContent> {

        public GaussCacheContext (int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, GaussContent> eldest) {
            return size() >= LRU_SIZE;
        }
    }

    static class GaussContent {

        private final Object reference;

        GaussContent (Object reference) {
            this.reference = reference;
        }

        public static GaussContent store(Object reference) {
            return new GaussContent(reference);
        }

        public Object getReference() {
            return GaussBeanFactory.copyObject(reference);
        }
    }

    private void setTimer(Method proxyMethod) {
        GaussCacheConfig config = CONFIG.get(proxyMethod);
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (CONTEXT) {
                    CONTEXT.remove(config.getCacheKey());
                }
            }
        }, getExpire(((GaussCache)config.getGaussCache()).expire()));
    }

    @Getter
    static class GaussCacheConfig {

        private final Annotation gaussCache;

        private final String cacheKey;

        GaussCacheConfig(Annotation annotation, String cacheKey) {
            this.gaussCache = annotation;
            this.cacheKey = cacheKey;
        }

        public static GaussCacheConfig create(Annotation annotation, String cacheKey) {
            return new GaussCacheConfig(annotation, cacheKey);
        }
    }
}
