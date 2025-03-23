package com.example.javasocialnetwork.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserActionLoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @AfterReturning(
            pointcut = "execution(* com.example.javasocialnetwork.controller..*(..))",
            returning = "result"
    )
    public void logUserAction(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        logger.info("USER ACTION: {} | Params: {} | Response: {}",
                methodName,
                args,
                result != null ? result.toString() : "void");
    }
}
