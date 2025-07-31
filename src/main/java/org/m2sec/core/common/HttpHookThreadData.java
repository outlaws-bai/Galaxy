package org.m2sec.core.common;

import org.m2sec.core.models.Request;

/**
 * @author: outlaws-bai
 * @date: 2024/8/16 13:33
 * @description:
 */

public class HttpHookThreadData {
    private static final ThreadLocal<Boolean> requestIsFromScannerFlag = ThreadLocal.withInitial(() -> false);

    private static final ThreadLocal<Request> request = ThreadLocal.withInitial(() -> null);

    public static void clear() {
        requestIsFromScannerFlag.set(false);
        request.remove();
    }

    public static boolean requestIsFromScanner() {
        return requestIsFromScannerFlag.get();
    }

    public static void setRequestIsFromScanner(boolean flag) {
        requestIsFromScannerFlag.set(flag);
    }

    public static void setRequest(Request request){
        HttpHookThreadData.request.set(request);
    }

    public static Request getRequest(){
        return request.get();
    }
}
