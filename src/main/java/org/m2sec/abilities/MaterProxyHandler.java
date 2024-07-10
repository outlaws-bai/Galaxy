package org.m2sec.abilities;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.proxy.http.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:52
 * @description:
 */

public class MaterProxyHandler implements ProxyRequestHandler, ProxyResponseHandler {
    private final MontoyaApi api;

    public MaterProxyHandler(MontoyaApi api) {
        this.api = api;
    }

    /**
     * 在客户端请求到达Burp时被调用
     */
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        return null;
    }

    /**
     * 在响应从Burp返回到客户端时被调用
     */
    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return null;
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        return ProxyResponseReceivedAction.continueWith(interceptedResponse);
    }
}
