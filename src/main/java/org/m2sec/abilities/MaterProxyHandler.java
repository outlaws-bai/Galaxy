package org.m2sec.abilities;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.*;
import org.m2sec.core.httphook.AbstractHttpHooker;


/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:52
 * @description:
 */

public class MaterProxyHandler implements ProxyRequestHandler, ProxyResponseHandler {

    public static AbstractHttpHooker hooker;


    /**
     * 在客户端请求到达Burp时被调用
     */
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        // 有些情况下，path的位置会显示全路径，但通过.path()又拿不到，只能以这种办法解决
        HttpRequest request;
        if (hooker != null) {
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
        if (hooker != null) {
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
