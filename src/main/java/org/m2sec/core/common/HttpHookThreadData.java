package org.m2sec.core.common;

/**
 * @author: outlaws-bai
 * @date: 2024/8/16 13:33
 * @description:
 */

public class HttpHookThreadData {
    private static final ThreadLocal<Boolean> requestIsFromScannerFlag = ThreadLocal.withInitial(() -> false);

    public static boolean requestIsFromScanner() {
        return requestIsFromScannerFlag.get();
    }

    public static void setRequestIsFromScanner(boolean flag) {
        requestIsFromScannerFlag.set(flag);
    }
}
