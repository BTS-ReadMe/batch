package com.readme.batch.utils;

import io.micrometer.core.annotation.Timed;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimedAspect {

    @Around("@annotation(timed)")
    public Object timeMethodExecution(ProceedingJoinPoint joinPoint, Timed timed) throws Throwable {

        // 컨트롤러 메서드 실행전에 작업 수행
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        // 컨트롤러 메서드 실행후에 작업 수행
        long executionTime = System.currentTimeMillis() - startTime;

        return result;
    }
}
