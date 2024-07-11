package org.m2sec.abilities;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.*;
import org.m2sec.core.common.Config;
import org.m2sec.core.httphook.AbstractHttpHooker;


/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:52
 * @description:
 */

public class MaterProxyHandler implements ProxyRequestHandler, ProxyResponseHandler {

    public static AbstractHttpHooker hooker;

    private final Config config;

    public MaterProxyHandler(Config config) {
        this.config = config;
    }


    /**
     * 在客户端请求到达Burp时被调用
     */
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        HttpRequest request;
        if (config.getOption().isHookStart()) {
            request = hooker.tryHookRequestToBurp(interceptedRequest);
        } else {
            request = interceptedRequest;
        }
        return ProxyRequestReceivedAction.continueWith(request, interceptedRequest.annotations());
    }

    /**
     * 在响应从Burp返回到客户端时被调用
     */
    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        HttpResponse response;
        if (config.getOption().isHookStart()) {
            response = hooker.tryHookResponseToClient(interceptedResponse);
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
