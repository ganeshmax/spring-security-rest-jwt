package com.ba.ssrj.framework.util

import java.util.concurrent.TimeUnit

/**
 * TODO: Document Me
 *
 * @author Ganeshji Marwaha
 * @since 6/20/15
 */
public class DateUtil {
    public static Date fromNow(int numTime, TimeUnit timeUnit) {
        long millisAfter = timeUnit.toMillis(numTime)
        return new Date(System.currentTimeMillis() + millisAfter)
    }
}
