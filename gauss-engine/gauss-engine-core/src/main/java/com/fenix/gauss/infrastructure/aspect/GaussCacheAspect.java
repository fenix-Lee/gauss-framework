package com.fenix.gauss.infrastructure.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class GaussCacheAspect {

    @Pointcut("@annotation(com.fenix.gauss.framework.GaussCache)")
    public void cachePointcut() {}

    @Around("cachePointcut()")
    public Object cacheable(ProceedingJoinPoint joinPoint) {
        System.out.println(joinPoint);
        return null;
    }
}
