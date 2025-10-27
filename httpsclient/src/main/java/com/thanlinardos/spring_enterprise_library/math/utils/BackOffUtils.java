package com.thanlinardos.spring_enterprise_library.math.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for calculating backoff delays.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BackOffUtils {

    /**
     * Exponential backoff delay calculation.
     *
     * @param retryCount      the current retry attempt (0-based).
     * @param backOffStepSize the base delay in seconds.
     * @param maxDelay        the maximum delay in seconds.
     * @return the calculated delay in seconds.
     */
    public static long getExponentialBackoffDelay(int retryCount, int backOffStepSize, int maxDelay) {
        return Math.min((long) Math.pow(2, retryCount) * backOffStepSize, maxDelay);
    }
}
