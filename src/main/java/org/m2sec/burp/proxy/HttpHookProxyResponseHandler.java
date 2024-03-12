package org.m2sec.burp.proxy;

import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.InterceptedResponse;
import burp.api.montoya.proxy.http.ProxyResponseHandler;
import burp.api.montoya.proxy.http.ProxyResponseReceivedAction;
import burp.api.montoya.proxy.http.ProxyResponseToBeSentAction;
import org.m2sec.modules.httphook.HttpHookTransfer;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class HttpHookProxyResponseHandler implements ProxyResponseHandler {

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(
            InterceptedResponse interceptedResponse) {
        return ProxyResponseReceivedAction.continueWith(interceptedResponse);
    }

    /** 在响应从Burp返回到客户端时被调用 */
    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(
            InterceptedResponse interceptedResponse) {
        // 处理Http Hook
        HttpResponse response = HttpHookTransfer.hookResponseToClient(interceptedResponse);
        return ProxyResponseToBeSentAction.continueWith(response);
    }
}
