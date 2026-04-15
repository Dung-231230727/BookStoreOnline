package com.bookstore.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Pointcut: Theo dõi tất cả các hàm trong package controller
     */
    @Pointcut("within(com.bookstore.controller..*)")
    public void controllerPointcut() {}

    /**
     * Log trước khi hàm chạy (In ra Request)
     */
    @Before("controllerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        log.info(">>> API REQUEST: [{}].[{}] | Args: {}", 
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(), 
            Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Log sau khi hàm chạy xong (In ra Response)
     */
    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("<<< API RESPONSE: [{}].[{}] | Result: {}", 
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(), 
            result);
    }

    /**
     * Log khi có lỗi xảy ra (In ra Exception)
     */
    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("!!! API ERROR: [{}].[{}] | Message: {}", 
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(), 
            e.getMessage());
    }
}
