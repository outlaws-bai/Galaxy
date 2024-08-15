package org.m2sec.abilities;

import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.*;
import org.m2sec.core.common.Config;
import org.m2sec.core.httphook.IHttpHooker;

/**
 * @author: outlaws-bai
 * @date: 2024/8/15 22:30
 * @description:
 */

public class HttpHookHandler implements HttpHandler, ProxyRequestHandler, ProxyResponseHandler {

    public static IHttpHooker hooker;

    private final Config config;

    public HttpHookHandler(Config config) {
        this.config = config;
    }

    /**
     * 在客户端请求到达Burp时被调用
     */
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        HttpRequest request;
        if (config.getOption().isHookStart()) {
            request = hooker.tryHookRequestToBurp(interceptedRequest, true, false);
        } else {
            request = interceptedRequest;
        }
        return ProxyRequestReceivedAction.continueWith(request, interceptedRequest.annotations());
    }

    /**
     * 请求从Burp发送到服务端时被调用
     */
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        HttpRequest request;
        if (config.getOption().isHookStart()) {
            request = hooker.tryHookRequestToServer(requestToBeSent, requestToBeSent.messageId(), false);
        } else {
            request = requestToBeSent;
        }
        return RequestToBeSentAction.continueWith(request, requestToBeSent.annotations());
    }

    /**
     * 响应从服务端返回给Burp时被调用
     */
    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        HttpResponse response;
        if (config.getOption().isHookStart()) {
            response = hooker.tryHookResponseToBurp(responseReceived, responseReceived.messageId(), false);
        } else {
            response = responseReceived;
        }
        return ResponseReceivedAction.continueWith(response, responseReceived.annotations());
    }

    /**
     * 在响应从Burp返回到客户端时被调用
     */
    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        HttpResponse response;
        if (config.getOption().isHookStart()) {
            response = hooker.tryHookResponseToClient(interceptedResponse, false);
        } else {
            response = interceptedResponse;
        }
        return ProxyResponseToBeSentAction.continueWith(response, interceptedResponse.annotations());
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
