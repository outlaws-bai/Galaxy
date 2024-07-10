package org.m2sec.abilities;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:53
 * @description:
 */

public class MasterHttpHandler implements HttpHandler {

    private final MontoyaApi api;

    public MasterHttpHandler(MontoyaApi api) {
        this.api = api;
    }

    /**
     * 请求从Burp发送到服务端时被调用
     */
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        return null;
    }

    /**
     * 响应从服务端返回给Burp时被调用
     */
    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        return null;
    }
}
