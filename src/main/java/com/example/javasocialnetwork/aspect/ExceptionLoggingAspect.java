package com.example.javasocialnetwork.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import java.util.Arrays;

@Aspect
@Component
public class ExceptionLoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @AfterThrowing(
            pointcut = "execution(* com.example.javasocialnetwork..*.*(..))",
            throwing = "ex"
    )

    public void logException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        logger.error("Exception in {} with args {}: {}",
                methodName, Arrays.toString(args), ex.getMessage());
    }
}