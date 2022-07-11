package com.fenix.gauss.infrastructure.aspect;

import com.fenix.gauss.framework.GaussCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class GaussCacheAspect {

    @Pointcut("@annotation(com.fenix.gauss.framework.GaussCache)")
    public void cachePointcut() {}

    @Around("cachePointcut()")
    public Object cacheable(ProceedingJoinPoint joinPoint) {
        Object result = null;

        try
        {
            //1 获得重载后的方法名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = joinPoint.getTarget().getClass()
                    .getMethod(signature.getName(),signature.getMethod().getParameterTypes());

            //2 确定方法名后获得该方法上面配置的注解标签MyRedisCache
            GaussCache gaussCacheAnnotation = method.getAnnotation(GaussCache.class);

            //3 拿到了MyRedisCache这个注解标签，获得该注解上面配置的参数进行封装和调用
            String prefix =  gaussCacheAnnotation.prefix();
            String gaussCacheKey = gaussCacheAnnotation.key();

            //4 SpringEL 解析器
            ExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(gaussCacheKey);
            EvaluationContext context = new StandardEvaluationContext();

            /**
             *@MyRedisCache(value = "userv3",key = "#userId")  //redis:       userv3::17   UserObject
             *public User findUserById(Integer userId)
             *{
             *    return null;
             *}
             */
            //5 获得方法里面的形参个数
            Object[] args = joinPoint.getArgs(); //Integer userId  17
            DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
            String[] parameterNames = discoverer.getParameterNames(method);
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i],args[i].toString());
            }
            //6 通过上述，拼接redis的最终key形式
            String key = prefix+"::"+expression.getValue(context).toString();
            System.out.println("MyRedisCacheAspect注解封装key："+key);

//            //7 先去redis里面查询看有没有
//            result = redisTemplate.opsForValue().get(key);
//            if(result != null)
//            {
//                System.out.println("MyRedisCacheAspect里面从redis读取获得："+result);
//                return result;
//            }
//            //8 redis里面没有，去找msyql查询或叫进行后续业务逻辑
//            //-------aop精华部分,才去找findUserById方法干活
            result = joinPoint.proceed();
//
//            //9 mysql步骤结束，还需要把结果存入redis一次，缓存补偿
//            redisTemplate.opsForValue().set(key,result);

        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

        return result;
    }
}
