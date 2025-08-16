package com.thanlinardos.spring_enterprise_library.spring_cloud_security.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

@Slf4j
public class LoggingAspectHelper {

    private LoggingAspectHelper() {
    }

    public static Object timeMethodExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("Method: {} | Duration: {}ms", proceedingJoinPoint.getSignature(), duration);
        return result;
    }
}
