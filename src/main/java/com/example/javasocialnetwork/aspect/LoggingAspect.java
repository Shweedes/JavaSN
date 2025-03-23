package com.example.javasocialnetwork.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("(execution(* com.example.javasocialnetwork.controller..*(..)) || " +
            "execution(* com.example.javasocialnetwork.service..*(..))) && " +
            "!within(com.example.javasocialnetwork.aspect..*)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("START {} | Args: {}", methodName, joinPoint.getArgs());

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        logger.info("END {} | Time: {}ms | Result: {}",
                methodName,
                executionTime,
                result != null ? result.toString() : "void");

        return result;
    }
}