package com.blossom.common.base.util;

import java.util.concurrent.TimeUnit;

/**
 * Provides millisecond-level time of OS.
 *
 * @author xzzz
 */
public final class TimeUtil {

    private static volatile long currentTimeMillis;

    static {
        currentTimeMillis = System.currentTimeMillis();
        Thread daemon = new Thread(() -> {
            while (true) {
                currentTimeMillis = System.currentTimeMillis();
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (Throwable ignored) {

                }
            }
        });
        daemon.setDaemon(true);
        daemon.setName("blossom-time-tick-thread");
        daemon.start();
    }

    public static long currentTimeMillis() {
        return currentTimeMillis;
    }
}
