package com.thanlinardos.spring_enterprise_library.spring_cloud_security.aspect;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Helper class for logging aspects, providing method execution time logging.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingAspectHelper {

    /**
     * Logs the execution time of a method.
     *
     * @param proceedingJoinPoint the join point representing the method execution.
     * @return the result of the method execution.
     * @throws Throwable if the method execution throws an exception.
     */
    public static Object timeMethodExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("Method: {} | Duration: {}ms", proceedingJoinPoint.getSignature(), duration);
        return result;
    }
}
