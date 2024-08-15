package org.m2sec.abilities;

import burp.api.montoya.http.handler.*;
import burp.api.montoya.proxy.http.*;
import org.m2sec.core.httphook.IHttpHooker;

/**
 * @author: outlaws-bai
 * @date: 2024/8/15 22:30
 * @description:
 */

public class HttpHookHandler implements HttpHandler, ProxyRequestHandler, ProxyResponseHandler {

    public static IHttpHooker hooker;


    /**
     * 在客户端请求到达Burp时被调用
     */
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        return ProxyRequestReceivedAction.continueWith(hooker.tryHookRequestToBurp(interceptedRequest, true, false),
            interceptedRequest.annotations());
    }

    /**
     * 请求从Burp发送到服务端时被调用
     */
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        return RequestToBeSentAction.continueWith(hooker.tryHookRequestToServer(requestToBeSent,
            requestToBeSent.messageId(), false), requestToBeSent.annotations());
    }

    /**
     * 响应从服务端返回给Burp时被调用
     */
    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        return ResponseReceivedAction.continueWith(hooker.tryHookResponseToBurp(responseReceived,
            responseReceived.messageId(), false), responseReceived.annotations());
    }

    /**
     * 在响应从Burp返回到客户端时被调用
     */
    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return ProxyResponseToBeSentAction.continueWith(hooker.tryHookResponseToClient(interceptedResponse, false),
            interceptedResponse.annotations());
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
