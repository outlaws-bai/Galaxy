package org.m2sec.abilities;

import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import org.m2sec.core.common.Config;
import org.m2sec.core.httphook.IHttpHooker;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:53
 * @description:
 */

public class MasterHttpHandler implements HttpHandler {


    public static IHttpHooker hooker;

    private final Config config;

    public MasterHttpHandler(Config config) {
        this.config = config;
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
}
