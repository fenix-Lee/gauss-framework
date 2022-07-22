package xyz.gaussframework.engine.infrastructure.aspect;

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
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Gauss Cache aspect class
 * @author Chang Su
 * @version 1.0
 * @since 7/7/2022
 */
@Aspect
public class GaussCacheAspect {

    private static final Log logger = LogFactory.getLog(GaussCacheAspect.class);

    private static final int LRU_SIZE = 5;

    private static final long DEFAULT_EXPIRED_TIME = 30000;

    private static final GaussCacheContext CONTEXT = new GaussCacheContext(LRU_SIZE);

    @Pointcut("@annotation(xyz.gaussframework.engine.framework.GaussCache)")
    public void cachePointcut() {}

    @Around("cachePointcut()")
    public Object cacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method proxyMethod;
        try {
            proxyMethod = joinPoint.getTarget().getClass()
                    .getMethod(signature.getName(),signature.getMethod().getParameterTypes());
        } catch (NoSuchMethodException e) {
            return null;
        }
        if (proxyMethod.getReturnType().equals(void.class)) {
            return null;
        }

        GaussCache gaussCacheAnnotation = proxyMethod.getAnnotation(GaussCache.class);
        String prefix =  gaussCacheAnnotation.prefix();
        if (ObjectUtils.isEmpty(prefix)) {
            prefix = joinPoint.getTarget().getClass().getName();
        }
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        String key;
        if (args.length != 0) {
            String gaussCacheKey = gaussCacheAnnotation.key();
            String[] parameterNames = new DefaultParameterNameDiscoverer().getParameterNames(proxyMethod);
            if (!ObjectUtils.isEmpty(gaussCacheKey)) {
                Expression expression = new SpelExpressionParser().parseExpression(gaussCacheKey);
                for (int i = 0; i < Objects.requireNonNull(parameterNames).length; i++) {
                    context.setVariable(parameterNames[i],args[i]);
                }
                try {
                    key = prefix + "::" + expression.getValue(context);
                } catch (SpelEvaluationException e) {
                    logger.error("GaussCache.key is not a springEL expression.....");
                    key = "";
                }
            } else {
                key = String.join(":", String.join("::", prefix, proxyMethod.getName()),
                        String.join(":", Objects.requireNonNull(parameterNames)));
            }
        } else {
            key = String.join("::", prefix, proxyMethod.getName());
        }
        if (!ObjectUtils.isEmpty(key) && CONTEXT.containsKey(key)) {

            return CONTEXT.get(key).getReference();
        }

        result = joinPoint.proceed();
        if (ObjectUtils.isEmpty(key)) {
            return result;
        }
        synchronized (CONTEXT) {
            CONTEXT.put(key, GaussContent.store(getExpire(gaussCacheAnnotation.expire()),
                    key, result));
        }
        return result;
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

        public static GaussContent store(long time, String key, Object reference) {
            setTimer(time, key);
            return new GaussContent(reference);
        }

        public Object getReference() {
            return reference;
        }

        private static void setTimer(long second, String key) {
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (CONTEXT) {
                        System.out.println("---- remove ----");
                        CONTEXT.remove(key);
                    }
                }
            }, second);
        }
    }
}
