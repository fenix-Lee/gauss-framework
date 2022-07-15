package xyz.gaussframework.engine.infrastructure.aspect;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import xyz.gaussframework.engine.framework.GaussCache;
import com.google.common.collect.Maps;
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

import javax.annotation.CheckForNull;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Aspect
public class GaussCacheAspect {

    private static final Log logger = LogFactory.getLog(GaussCacheAspect.class);

    private static final Map<String, Object> local = Maps.newConcurrentMap();

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
        if (!ObjectUtils.isEmpty(key) && local.containsKey(key)) {
            logger.info("GaussCache: " + key + " is functional....");
            return local.get(key);
        }

        result = joinPoint.proceed();
        if (ObjectUtils.isEmpty(key)) {
            return result;
        }
        local.put(key, result);
        return result;
    }
}
